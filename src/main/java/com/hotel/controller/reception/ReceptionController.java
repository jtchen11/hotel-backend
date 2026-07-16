package com.hotel.controller.reception;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hotel.common.Result;
import com.hotel.common.UserContext;
import com.hotel.entity.*;
import com.hotel.mapper.*;
import com.hotel.service.GuestService;
import com.hotel.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Arrays;

@RestController
@RequestMapping("/api/reception")
public class ReceptionController {

    @Autowired
    private GuestMapper guestMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private GroupInfoMapper groupInfoMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private OrderMainMapper orderMainMapper;
    @Autowired
    private GuestService guestService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private DepositMapper depositMapper;
    @Autowired
    private OrderExtensionLogMapper orderExtensionLogMapper;
    @Autowired
    private IdempotentRecordMapper idempotentRecordMapper;

    // ========== 散客入住 ==========
    @Operation(summary = "散客入住登记")
    @PostMapping("/checkin")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> checkIn(@RequestBody Map<String, Object> params) {
        // 安全提取参数，支持 Integer 或 String
        Object guestIdObj = params.get("guestId");
        Integer guestId = null;
        if (guestIdObj instanceof Integer) {
            guestId = (Integer) guestIdObj;
        } else if (guestIdObj instanceof String) {
            String s = (String) guestIdObj;
            if (!s.isEmpty()) guestId = Integer.valueOf(s);
        }

        Object roomIdObj = params.get("roomId");
        if (roomIdObj == null) return Result.error("房间ID不能为空");
        Integer roomId = (roomIdObj instanceof Integer) ? (Integer) roomIdObj : Integer.valueOf(roomIdObj.toString());

        String name = (String) params.get("name");
        String idCard = (String) params.get("idCard");
        String phone = (String) params.get("phone");
        if (name == null || name.isEmpty()) return Result.error("客人姓名不能为空");
        if (idCard == null || idCard.isEmpty()) return Result.error("身份证号不能为空");

        // ====== 新增：身份证重复在住校验（位置1） ======
        if (idCard != null && !idCard.isEmpty()) {
            QueryWrapper<Guest> dupQw = new QueryWrapper<>();
            dupQw.eq("id_card", idCard).eq("status", "在住");
            if (guestId != null) {
                dupQw.ne("guest_id", guestId);
            }
            if (guestMapper.selectCount(dupQw) > 0) {
                return Result.error("该身份证已有在住记录，不能重复办理入住");
            }
        }

        // 日期处理：允许为空，使用默认值
        String outDateStr = (String) params.get("outDate");
        if (outDateStr == null || outDateStr.isEmpty()) {
            outDateStr = LocalDate.now().plusDays(1).toString();
        }
        LocalDate outDate = LocalDate.parse(outDateStr);
        String inDateStr = (String) params.get("inDate");
        if (inDateStr == null || inDateStr.isEmpty()) {
            inDateStr = LocalDate.now().toString();
        }
        LocalDate inDate = LocalDate.parse(inDateStr);
        LocalDate today = LocalDate.now();

        // 校验：入住日期不能晚于离店日期
        if (today.isAfter(outDate)) {
            return Result.error("离店日期不能早于今天");
        }
        Object depositObj = params.get("depositAmount");
        BigDecimal depositAmount = depositObj != null ? new BigDecimal(depositObj.toString()) : BigDecimal.ZERO;
        String payMethod = (String) params.get("payMethod");

        // 幂等校验：相同 roomId + idCard + inDate 视为重复请求
        String idemKey = "checkin:" + roomId + ":" + (idCard != null ? idCard : "") + ":" + inDate;
        Long idemCount = idempotentRecordMapper.selectCount(
                new QueryWrapper<IdempotentRecord>().eq("idempotent_key", idemKey));
        if (idemCount > 0) {
            return Result.success("该入住请求已处理");
        }
        // 行级锁：锁定房间行，防止并发超售
        roomMapper.selectForUpdate(roomId);
        // 校验房间状态
        Room room = roomMapper.selectById(roomId);
        if (room == null) return Result.error("房间不存在");
        if ("维修中".equals(room.getStatus()) || "打扫中".equals(room.getStatus()) || "占用".equals(room.getStatus())) {
            return Result.error("房间当前状态为【" + room.getStatus() + "】，无法办理入住");
        }

        // 检查日期范围内是否有冲突（行锁 guest 表冲突记录，防并发超售）
        int conflictCount = guestMapper.countConflictingForUpdate(roomId, outDate.atStartOfDay(), inDate, guestId);
        if (conflictCount > 0) {
            return Result.error("该房间在" + inDate + "至" + outDate + "期间已被其他预订占用，请选择其他房间");
        }

        // 幂等记录：通过所有校验后插入
        IdempotentRecord idem = new IdempotentRecord();
        idem.setIdempotentKey(idemKey);
        idem.setStatus("SUCCESS");
        idem.setCreatedAt(LocalDateTime.now());
        idempotentRecordMapper.insert(idem);

        Guest guest;
        if (guestId != null) {
            // 从预订转入住
            guest = guestMapper.selectById(guestId);
            if (guest == null) return Result.error("预订记录不存在");
            if (!"已预订".equals(guest.getStatus())) return Result.error("该客人不是预订状态");
            // 校验入住日期：今天必须 >= 预订的入住日期
            LocalDate bookingDate = guest.getCheckInDate() != null ? guest.getCheckInDate().toLocalDate() : null;
            if (bookingDate != null && LocalDate.now().isBefore(bookingDate)) {
                return Result.error("未到入住日期(" + bookingDate + "),请在当天或之后办理入住");
            }
            // 如果选了不同房间，释放原锁定房间
            if (!room.getRoomId().equals(guest.getRoomId())) {
                Room oldRoom = roomMapper.selectById(guest.getRoomId());
                if (oldRoom != null && "已锁".equals(oldRoom.getStatus())) {
                    oldRoom.setStatus("空闲");
                    roomMapper.updateById(oldRoom);
                }
                guest.setRoomId(room.getRoomId());
            }
            guest.setName(name);
            guest.setIdCard(idCard);
            guest.setPhone(phone);
            guest.setGender((String) params.getOrDefault("gender", null));
            guest.setNativePlace((String) params.getOrDefault("nativePlace", null));
            guest.setCompany((String) params.getOrDefault("company", null));
            guest.setOccupation((String) params.getOrDefault("occupation", null));
            guest.setReason((String) params.getOrDefault("reason", null));
        } else {
            // 散客入住（walk-in）
            guest = new Guest();
            guest.setName(name);
            guest.setIdCard(idCard);
            guest.setPhone(phone);
            guest.setGuestType("散客");
            guest.setGender((String) params.getOrDefault("gender", null));
            guest.setNativePlace((String) params.getOrDefault("nativePlace", null));
            guest.setCompany((String) params.getOrDefault("company", null));
            guest.setOccupation((String) params.getOrDefault("occupation", null));
            guest.setReason((String) params.getOrDefault("reason", null));
            guest.setCreateTime(LocalDateTime.now());
            guest.setRoomId(roomId);
        }

        guest.setStatus("在住");
        guest.setCheckInDate(inDate.atStartOfDay());
        guest.setPreLeaveDate(outDate);
        if (guestId != null) {
            guestMapper.updateById(guest);
        } else {
            guestMapper.insert(guest);
        }

        // 更新房间状态为占用
        room.setStatus("占用");
        roomMapper.updateById(room);

        // 创建订单主表
        OrderMain order = new OrderMain();
        order.setGuestId(guest.getGuestId());
        order.setRoomId(roomId);
        order.setStatus("未结算");
        orderMainMapper.insert(order);

        // 收取押金
        Deposit deposit = new Deposit();
        deposit.setGuestId(guest.getGuestId());
        deposit.setAmount(depositAmount);
        deposit.setType("收取");
        deposit.setPayMethod(payMethod);
        deposit.setRemark("入住押金");
        deposit.setCreateTime(LocalDateTime.now());
        deposit.setOperator(UserContext.getEmpName());
        depositMapper.insert(deposit);

        return Result.success("入住成功");
    }

    @PostMapping("/groupcheckin")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> groupCheckIn(@RequestBody Map<String, Object> params) {
        String groupName = (String) params.get("groupName");
        String groupLeader = (String) params.get("groupLeader");
        String leaderPhone = (String) params.get("leaderPhone");
        Integer personCount = Integer.valueOf(params.get("personCount").toString());
        String inDateStr = (String) params.get("inDate");
        String outDateStr = (String) params.get("outDate");

        // 安全转换 roomIdList
        List<Integer> roomIdList = new ArrayList<>();
        List<?> rawList = (List<?>) params.get("roomIdList");
        if (rawList != null) {
            for (Object obj : rawList) {
                roomIdList.add(Integer.valueOf(obj.toString()));
            }
        }

        // ====== 新增：手机号格式校验 ======
        if (leaderPhone == null || !leaderPhone.matches("^1[3-9]\\d{9}$")) {
            return Result.error("领队手机号格式不正确，请输入11位手机号");
        }
        // ====== 新增：团体名称必填 ======
        if (groupName == null || groupName.trim().isEmpty()) {
            return Result.error("团体名称不能为空");
        }
        // ====== 新增：领队姓名必填 ======
        if (groupLeader == null || groupLeader.trim().isEmpty()) {
            return Result.error("领队姓名不能为空");
        }
        // ====== 新增：人数校验 ======
        if (personCount == null || personCount < 1) {
            return Result.error("总人数必须大于0");
        }

        LocalDateTime checkInTime = LocalDate.parse(inDateStr).atStartOfDay();
        LocalDate preLeave = LocalDate.parse(outDateStr);

        // 对每个房间做冲突检测
        for (Integer roomId : roomIdList) {
            Room room = roomMapper.selectById(roomId);
            if (room == null) return Result.error("房间 " + roomId + " 不存在");

            QueryWrapper<Guest> qw = new QueryWrapper<>();
            qw.eq("room_id", roomId)
                    .in("status", Arrays.asList("在住", "已预订", "已锁"))
                    .lt("check_in_date", preLeave.atStartOfDay())
                    .gt("pre_leave_date", checkInTime.toLocalDate());
            if (guestMapper.selectCount(qw) > 0) {
                return Result.error("房间 " + room.getRoomNumber() + " 在所选时间段已被占用");
            }
        }
        // 假设每间房最多住2人，则所需最少房间数 = ceil(personCount / 2)
        int minRooms = (int) Math.ceil(personCount / 2.0);
        if (roomIdList.size() < minRooms) {
            return Result.error("房间数不足，至少需要" + minRooms + "间房");
        }

        // 保存团体信息
        GroupInfo info = new GroupInfo();
        info.setGroupName(groupName);
        info.setGroupLeader(groupLeader);
        info.setLeaderPhone(leaderPhone);
        info.setPersonCount(personCount);
        info.setPreLeaveDate(preLeave);
        info.setCheckInDate(checkInTime);
        info.setCreateTime(LocalDateTime.now());
        groupInfoMapper.insert(info);

        // 批量生成客人记录
        int index = 1;
        for (Integer roomId : roomIdList) {
            Guest guest = new Guest();
            guest.setName(groupLeader);
            guest.setPhone(leaderPhone);
            String uniqueId = "GROUP" + info.getGroupId() + "_" + (index++);
            guest.setIdCard(uniqueId);
            guest.setRoomId(roomId);
            guest.setCheckInDate(checkInTime);
            guest.setPreLeaveDate(preLeave);
            guest.setStatus("已预订");
            guest.setGuestType("团体");
            guest.setGroupId(info.getGroupId());
            guest.setCreateTime(LocalDateTime.now());
            guestMapper.insert(guest);
        }

        return Result.success("团体入住成功");
    }

    // ========== 团体入住登记（个人入住信息补全） ==========
    @Operation(summary = "团队入住补全个人信息")
    @PostMapping("/groupCheckinMember")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> groupCheckinMember(@RequestBody Map<String, Object> params) {
        Object guestIdObj = params.get("guestId");
        if (guestIdObj == null) return Result.error("guestId不能为空");
        Integer guestId = Integer.valueOf(guestIdObj.toString());
        Object roomIdObj = params.get("roomId");
        if (roomIdObj == null) return Result.error("roomId不能为空");
        Integer roomId = Integer.valueOf(roomIdObj.toString());
        String name = (String) params.get("name");
        String idCard = (String) params.get("idCard");
        String phone = (String) params.get("phone");
        String outDateStr = (String) params.get("outDate");
        if (name == null || name.isEmpty()) return Result.error("客人姓名不能为空");
        if (idCard == null || idCard.isEmpty()) return Result.error("身份证号不能为空");

        // ====== 新增：身份证重复在住校验（位置3） ======
        if (idCard != null && !idCard.isEmpty()) {
            QueryWrapper<Guest> dupQw = new QueryWrapper<>();
            dupQw.eq("id_card", idCard).eq("status", "在住").ne("guest_id", guestId);
            if (guestMapper.selectCount(dupQw) > 0) {
                return Result.error("该身份证已有在住记录，不能重复办理入住");
            }
        }

        if (outDateStr == null || outDateStr.isEmpty()) {
            outDateStr = LocalDate.now().plusDays(1).toString();
        }
        LocalDate outDate = LocalDate.parse(outDateStr);

        Guest guest = guestMapper.selectById(guestId);
        if (guest == null) return Result.error("预订记录不存在");
        if (!"已预订".equals(guest.getStatus())) return Result.error("该客人不是预订状态");

        // 校验房间
        Room room = roomMapper.selectById(roomId);
        if (room == null) return Result.error("房间不存在");
        if ("维修中".equals(room.getStatus())) {
            return Result.error("房间当前状态为【维修中】，无法办理入住");
        }

        // 如果选了不同房间，释放原锁房
        if (!room.getRoomId().equals(guest.getRoomId())) {
            Room oldRoom = roomMapper.selectById(guest.getRoomId());
            if (oldRoom != null && "已锁".equals(oldRoom.getStatus())) {
                oldRoom.setStatus("空闲");
                roomMapper.updateById(oldRoom);
            }
        }

        // 更新客人个人信息，保留 guestType 和 groupId
        guest.setName(name);
        guest.setIdCard(idCard);
        guest.setPhone(phone);
        guest.setPreLeaveDate(outDate);
        guest.setGender((String) params.getOrDefault("gender", null));
        guest.setNativePlace((String) params.getOrDefault("nativePlace", null));
        guest.setCompany((String) params.getOrDefault("company", null));
        guest.setOccupation((String) params.getOrDefault("occupation", null));
        guest.setReason((String) params.getOrDefault("reason", null));
        guest.setStatus("在住");
        guest.setCheckInDate(LocalDateTime.now());
        guest.setRoomId(roomId);
        guestMapper.updateById(guest);

        // 房间改为占用
        room.setStatus("占用");
        roomMapper.updateById(room);

        // 创建空订单主表
        OrderMain order = new OrderMain();
        order.setGuestId(guestId);
        order.setRoomId(roomId);
        order.setStatus("未结算");
        orderMainMapper.insert(order);

        // 收取押金
        Object depositObj = params.get("depositAmount");
        BigDecimal depositAmount = depositObj != null ? new BigDecimal(depositObj.toString()) : BigDecimal.ZERO;
        if (depositAmount.compareTo(BigDecimal.ZERO) > 0) {
            Deposit deposit = new Deposit();
            deposit.setGuestId(guestId);
            deposit.setAmount(depositAmount);
            deposit.setType("收取");
            deposit.setPayMethod((String) params.get("payMethod"));
            deposit.setRemark("团队入住押金");
            deposit.setCreateTime(LocalDateTime.now());
            deposit.setOperator(UserContext.getEmpName());
            depositMapper.insert(deposit);
        }

        return Result.success("入住成功");
    }


    // ========== 换房接口（新增） ==========
    @Operation(summary = "换房操作")
    @PostMapping("/changeRoom")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> changeRoom(@RequestParam Integer guestId, @RequestParam Integer newRoomId) {
        Guest guest = guestMapper.selectById(guestId);
        if (guest == null) return Result.error("客人不存在");
        if (!"在住".equals(guest.getStatus())) return Result.error("客人不在住，无法换房");

        Integer oldRoomId = guest.getRoomId();
        Room oldRoom = roomMapper.selectById(oldRoomId);
        Room newRoom = roomMapper.selectById(newRoomId);

        if (newRoom == null || !"空闲".equals(newRoom.getStatus())) {
            return Result.error("目标房间不可用");
        }

        // 记录换房留言
        Message message = new Message();
        message.setGuestId(guestId);
        message.setContent("换房记录：从 " + oldRoom.getRoomNumber() + " 换至 " + newRoom.getRoomNumber());
        message.setMsgType("换房记录");
        message.setCreateTime(LocalDateTime.now());
        message.setIsRead(0);
        message.setOperator(UserContext.getEmpName());
        messageMapper.insert(message);

        // 更新房间状态
        oldRoom.setStatus("空闲");
        roomMapper.updateById(oldRoom);
        newRoom.setStatus("占用");
        roomMapper.updateById(newRoom);

        // 更新客人的房间ID
        guest.setRoomId(newRoomId);
        guestMapper.updateById(guest);

        // 更新订单主表中的房间ID
        UpdateWrapper<OrderMain> uw = new UpdateWrapper<>();
        uw.eq("guest_id", guestId).eq("status", "未结算");
        OrderMain order = new OrderMain();
        order.setRoomId(newRoomId);
        orderMainMapper.update(order, uw);

        return Result.success("换房成功");
    }

    @Operation(summary = "更新房间状态")
    @PutMapping("/room/updateStatus")
    public Result<?> updateRoomStatus(Integer roomId, String status) {
        Room room = roomMapper.selectById(roomId);
        if (room == null) return Result.error("房间不存在");
        // 如果当前是占用状态，不允许修改（除非改为打扫中？根据业务，占用只能由结账改为打扫中）
        if ("占用".equals(room.getStatus()) && !"打扫中".equals(status)) {
            return Result.error("房间正在使用中，不能直接修改状态");
        }
        room.setStatus(status);
        roomMapper.updateById(room);
        // 设维修中时检查未来预订
        if ("维修中".equals(status)) {
            QueryWrapper<Guest> qw = new QueryWrapper<>();
            qw.eq("room_id", roomId).eq("status", "已预订").ge("check_in_date", LocalDate.now().atStartOfDay());
            long count = guestMapper.selectCount(qw);
            if (count > 0) {
                Result r = Result.success();
                r.setMsg("状态修改成功。该房间有 " + count + " 条未来预订，请通知客人换房");
                return r;
            }
        }
        return Result.success("状态修改成功");
    }

    @Operation(summary = "按日期查询房间状态列表")
    @GetMapping("/room/dateList")
    public Result<List<Map<String, Object>>> getRoomDateList(
            @RequestParam(required = false) String inDate,
            @RequestParam(required = false) String outDate) {
        if (inDate == null || inDate.isEmpty()) inDate = LocalDate.now().toString();
        if (outDate == null || outDate.isEmpty()) outDate = LocalDate.now().plusDays(1).toString();
        LocalDateTime d1 = LocalDate.parse(inDate).atStartOfDay();
        LocalDateTime d2 = LocalDate.parse(outDate).atStartOfDay();

        List<Room> all = roomMapper.selectList(null);

        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.in("status", Arrays.asList("在住", "已预订", "已锁"))
                .lt("check_in_date", d2)
                .gt("pre_leave_date", d1);
        qw.select("room_id");
        List<Object> objList = guestMapper.selectObjs(qw);
        Set<Integer> busyRoomIds = objList.stream().map(id -> (Integer) id).collect(Collectors.toSet());

        // 查今天时，被占用的房间也标记为忙碌（超期未退房）
        if (!LocalDate.parse(inDate).isAfter(LocalDate.now())) {
            List<Room> occupiedRooms = roomMapper.selectList(new QueryWrapper<Room>().eq("status", "占用"));
            occupiedRooms.forEach(r -> busyRoomIds.add(r.getRoomId()));
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Room r : all) {
            Map<String, Object> map = new HashMap<>();
            map.put("roomId", r.getRoomId());
            map.put("roomNumber", r.getRoomNumber());
            map.put("roomType", r.getRoomType());
            map.put("price", r.getPrice());
            map.put("floor", r.getFloor());
            map.put("isBusy", busyRoomIds.contains(r.getRoomId()));
            map.put("status", r.getStatus());   // 新增：返回房间当前状态
            list.add(map);
        }
        return Result.success(list);
    }

    @Operation(summary = "获取各房型信息（含剩余可预订数量）")
    @GetMapping("/room/typeList")
    public Result<List<Map<String, Object>>> getRoomTypeList(
            @RequestParam(required = false) String inDate,
            @RequestParam(required = false) String outDate) {
        // 空日期使用默认值
        if (inDate == null || inDate.isEmpty()) inDate = LocalDate.now().toString();
        if (outDate == null || outDate.isEmpty()) outDate = LocalDate.now().plusDays(1).toString();
        LocalDateTime d1 = LocalDate.parse(inDate).atStartOfDay();
        LocalDateTime d2 = LocalDate.parse(outDate).atStartOfDay();

        List<Room> all = roomMapper.selectList(null);

        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.in("status", Arrays.asList("在住", "已预订", "已锁"))
                .lt("check_in_date", d2)
                .gt("pre_leave_date", d1.toLocalDate());
        qw.select("room_id");
        List<Object> objList = guestMapper.selectObjs(qw);
        Set<Integer> busyRoomIds = objList.stream()
                .map(id -> (Integer) id)
                .collect(Collectors.toSet());

        List<Map<String, Object>> typeList = new ArrayList<>();
        Map<String, List<Room>> typeGroup = all.stream().collect(Collectors.groupingBy(Room::getRoomType));
        int idx = 1;
        for (Map.Entry<String, List<Room>> entry : typeGroup.entrySet()) {
            String typeName = entry.getKey();
            List<Room> rooms = entry.getValue();
            BigDecimal price = rooms.get(0).getPrice();
            int total = rooms.size();
            int available = 0;
            for (Room r : rooms) {
                if (!"维修中".equals(r.getStatus()) && !busyRoomIds.contains(r.getRoomId())) {
                    if (!"占用".equals(r.getStatus()) || d1.toLocalDate().isAfter(LocalDate.now())) {
                        available++;
                    }
                }
            }
            Map<String, Object> item = new HashMap<>();
            item.put("id", idx++);
            item.put("name", typeName);
            item.put("price", price);
            String desc = rooms.get(0).getDescription();
            item.put("description", desc != null ? desc : "");
            item.put("availableCount", available);
            typeList.add(item);
        }
        return Result.success(typeList);
    }

    @Operation(summary = "创建预订（选择房型，自动分配房间）")
    @PostMapping("/booking")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> createBooking(@RequestBody Map<String, Object> params) {
        String roomType = (String) params.get("roomType");
        String guestName = (String) params.get("guestName");
        String phone = (String) params.get("phone");
        String inDateStr = (String) params.get("inDate");
        String outDateStr = (String) params.get("outDate");
        String remark = (String) params.get("remark");

        if (roomType == null || guestName == null || phone == null || inDateStr == null || outDateStr == null) {
            return Result.error("缺少必填参数");
        }
        LocalDate inDate = LocalDate.parse(inDateStr);
        LocalDate outDate = LocalDate.parse(outDateStr);

        // 在事务内逐一锁定候选房间并检测冲突
        List<Room> candidates = findAvailableRooms(inDate, outDate, roomType, null);
        candidates.removeIf(r -> "维修中".equals(r.getStatus()));
        Room assigned = null;
        for (Room candidate : candidates) {
            roomMapper.selectForUpdate(candidate.getRoomId());
            Room locked = roomMapper.selectById(candidate.getRoomId());
            if ("维修中".equals(locked.getStatus())) continue;
            int conflict = guestMapper.countConflictingForUpdate(candidate.getRoomId(), outDate.atStartOfDay(), inDate, null);
            if (conflict == 0) {
                assigned = locked;
                break;
            }
        }
        if (assigned == null) {
            return Result.error("该房型在所选日期范围内已满，请选择其他房型或调整日期");
        }

        Guest guest = new Guest();
        guest.setName(guestName);
        guest.setPhone(phone);
        guest.setIdCard("");
        guest.setRoomId(assigned.getRoomId());
        guest.setCheckInDate(inDate.atStartOfDay());
        guest.setPreLeaveDate(outDate);
        guest.setStatus("已预订");
        guest.setGuestType("散客");
        guest.setCreateTime(LocalDateTime.now());
        guestMapper.insert(guest);

        Map<String, Object> result = new HashMap<>();
        result.put("guestId", guest.getGuestId());
        result.put("roomNumber", assigned.getRoomNumber());
        // 不修改房间物理状态，预订状态由 guest 表记录
        // 物理状态不变
        return Result.success(result);
    }

    @Operation(summary = "获取可入住房间列表（按房型）")
    @GetMapping("/room/availableRooms")
    public Result<List<Map<String, Object>>> getAvailableRooms(
            @RequestParam String roomType,
            @RequestParam(required = false) String inDate,
            @RequestParam(required = false) String outDate,
            @RequestParam(required = false) Integer guestId) {
        if (inDate == null || inDate.isEmpty()) inDate = LocalDate.now().toString();
        if (outDate == null || outDate.isEmpty()) outDate = LocalDate.now().plusDays(1).toString();
        LocalDateTime d1 = LocalDate.parse(inDate).atStartOfDay();
        LocalDateTime d2 = LocalDate.parse(outDate).atStartOfDay();

        QueryWrapper<Room> rqw = new QueryWrapper<>();
        rqw.eq("room_type", roomType);
        List<Room> allRooms = roomMapper.selectList(rqw);

        // 用通用方法计算可用房间
        LocalDate startDate = LocalDate.parse(inDate);
        LocalDate endDate = LocalDate.parse(outDate);
        List<Room> available = findAvailableRooms(startDate, endDate, roomType, guestId);
        Set<Integer> availableIds = available.stream().map(Room::getRoomId).collect(java.util.stream.Collectors.toSet());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Room r : allRooms) {
            if ("维修中".equals(r.getStatus())) continue;
            Map<String, Object> item = new HashMap<>();
            item.put("roomId", r.getRoomId());
            item.put("roomNumber", r.getRoomNumber());
            item.put("price", r.getPrice());
            item.put("status", r.getStatus());
            item.put("roomType", r.getRoomType());
            item.put("floor", r.getFloor());
            item.put("description", r.getDescription());
            item.put("available", availableIds.contains(r.getRoomId()));
            result.add(item);
        }
        return Result.success(result);
    }

    @Operation(summary = "退房操作")
    @PostMapping("/checkout")
    public Result<?> checkout(@RequestParam Integer roomId) {
        // 保留原逻辑，但前台可能不再调用此接口（改为跳转财务）
        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.eq("room_id", roomId).eq("status", "在住").last("LIMIT 1");
        Guest g = guestMapper.selectOne(qw);
        if (g == null) return Result.error("无人入住");
        g.setStatus("已退房");
        g.setActualLeaveDate(LocalDateTime.now());
        guestMapper.updateById(g);
        Room r = new Room();
        r.setRoomId(roomId);
        r.setStatus("空闲");
        roomMapper.updateById(r);
        return Result.success("退房成功");
    }

    @Operation(summary = "获取在住客人列表")
    @GetMapping("/living-list")
    public Result<?> livingList() {
        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.eq("status", "在住");
        return Result.success(guestMapper.selectList(qw));
    }

    @Operation(summary = "获取未读消息数")
    @GetMapping("/messages/unread")
    public Result<Integer> unread() {
        QueryWrapper<Message> qw = new QueryWrapper<>();
        qw.eq("is_read", 0);
        return Result.success(messageMapper.selectCount(qw).intValue());
    }

    @Operation(summary = "获取在住客人及消费信息")
    @GetMapping("/livingWithConsume")
    public Result<List<Map<String, Object>>> livingWithConsume() {
        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.eq("status", "在住");
        List<Guest> guests = guestMapper.selectList(qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Guest guest : guests) {
            Room room = roomMapper.selectById(guest.getRoomId());
            // 统计挂账消费（餐饮 + 杂项，不含KTV）
            QueryWrapper<OrderDetail> odQw = new QueryWrapper<>();
            odQw.eq("guest_id", guest.getGuestId())
                    .eq("is_settled", 0)
                    .ne("item_type", "KTV");
            List<OrderDetail> details = orderDetailMapper.selectList(odQw);
            BigDecimal consume = details.stream()
                    .map(OrderDetail::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> map = new HashMap<>();
            map.put("guestId", guest.getGuestId());
            map.put("name", guest.getName());
            map.put("phone", guest.getPhone());
            map.put("roomId", guest.getRoomId());
            map.put("roomNumber", room.getRoomNumber());
            map.put("checkInDate", guest.getCheckInDate());
            map.put("preLeaveDate", guest.getPreLeaveDate());

            map.put("consume", consume);
            result.add(map);
        }
        return Result.success(result);
    }

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Operation(summary = "获取待办理入住列表")
    @GetMapping("/pendingBookings")
    public Result<List<Map<String, Object>>> pendingBookings(@RequestParam(required = false) String keyword,
                                                             @RequestParam(required = false) String guestType) {
        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.eq("status", "已预订")
                .apply("DATE(pre_leave_date) >= DATE(NOW())")
                .orderByAsc("check_in_date");
        if (guestType != null && !guestType.isEmpty()) {
            qw.eq("guest_type", guestType);
        }
        List<Guest> guests = guestMapper.selectList(qw);
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w.like("name", keyword).or().like("phone", keyword));
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Guest guest : guests) {
            Room room = roomMapper.selectById(guest.getRoomId());
            Map<String, Object> map = new HashMap<>();
            map.put("guestId", guest.getGuestId());
            map.put("name", guest.getName());
            map.put("phone", guest.getPhone());
            map.put("roomId", guest.getRoomId());
            map.put("roomNumber", room.getRoomNumber());
            map.put("roomType", room.getRoomType());
            map.put("checkInDate", guest.getCheckInDate());
            map.put("preLeaveDate", guest.getPreLeaveDate());
            map.put("guestType", guest.getGuestType());
            map.put("groupId", guest.getGroupId());
            String groupName = "";
            if (guest.getGroupId() != null) {
                GroupInfo gi = groupInfoMapper.selectById(guest.getGroupId());
                if (gi != null) groupName = gi.getGroupName();
            }
            map.put("groupName", groupName);
            result.add(map);
        }
        return Result.success(result);
    }

    @Operation(summary = "预订客人办理入住")
    @PostMapping("/checkinFromBooking")
    @Transactional
    public Result<?> checkinFromBooking(@RequestBody Map<String, Object> params) {
        Integer guestId = (Integer) params.get("guestId");
        BigDecimal depositAmount = new BigDecimal(params.get("depositAmount").toString());

        Guest guest = guestMapper.selectById(guestId);
        if (guest == null) return Result.error("预订记录不存在");
        if (!"已预订".equals(guest.getStatus())) return Result.error("该客人不是预订状态");

        // ====== 新增：身份证重复在住校验（位置2） ======
        String checkIdCard = guest.getIdCard();
        if (checkIdCard != null && !checkIdCard.isEmpty()) {
            QueryWrapper<Guest> dupQw = new QueryWrapper<>();
            dupQw.eq("id_card", checkIdCard).eq("status", "在住").ne("guest_id", guest.getGuestId());
            if (guestMapper.selectCount(dupQw) > 0) {
                return Result.error("该身份证已有在住记录，不能重复办理入住");
            }
        }

        LocalDate today = LocalDate.now();
        LocalDate checkInDate = guest.getCheckInDate().toLocalDate();
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime allowedStart = checkInDate.atTime(12, 0, 0);
        if (now.isBefore(allowedStart)) {
            return Result.error("未到办理入住时间，请于" + allowedStart + "后办理");
        }

        // 校验房间状态：允许"空闲"或"已锁"
        Room room = roomMapper.selectById(guest.getRoomId());
        if (room == null) return Result.error("房间不存在");
        if ("维修中".equals(room.getStatus())) {
            return Result.error("房间当前状态为【维修中】，无法办理入住");
        }
        // 如果房间是"已锁"，校验是否属于当前客人预订的房间
        if ("已锁".equals(room.getStatus()) && !room.getRoomId().equals(guest.getRoomId())) {
            return Result.error("该房间已被其他预订占用");
        }
        // 更新客人状态
        guest.setStatus("在住");
        guest.setCheckInDate(LocalDateTime.now());
        guestMapper.updateById(guest);

        // 房间改为占用
        room.setStatus("占用");
        roomMapper.updateById(room);

        // 创建空订单主表
        OrderMain order = new OrderMain();
        order.setGuestId(guestId);
        order.setRoomId(guest.getRoomId());
        order.setStatus("未结算");
        orderMainMapper.insert(order);

        // 收取押金
        Deposit deposit = new Deposit();
        deposit.setGuestId(guestId);
        deposit.setAmount(depositAmount);
        deposit.setType("收取");
        deposit.setPayMethod((String) params.get("payMethod"));
        deposit.setRemark("入住押金");
        deposit.setCreateTime(LocalDateTime.now());
        deposit.setOperator(UserContext.getEmpName());
        depositMapper.insert(deposit);

        return Result.success("入住成功");
    }

    @Autowired
    @Operation(summary = "团队批量预订（创建集团组 + 批量锁房）")
    @PostMapping("/teamBooking")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> teamBooking(@RequestBody Map<String, Object> params) {
        String groupName = (String) params.get("groupName");
        String groupLeader = (String) params.get("groupLeader");
        String leaderPhone = (String) params.get("leaderPhone");
        String roomType = (String) params.get("roomType");
        String inDateStr = (String) params.get("inDate");
        String outDateStr = (String) params.get("outDate");
        Object roomCountObj = params.get("roomCount");

        if (groupName == null || groupName.isEmpty()) return Result.error("团队名称不能为空");
        if (roomType == null || roomType.isEmpty()) return Result.error("房型不能为空");
        if (inDateStr == null || inDateStr.isEmpty()) return Result.error("入住日期不能为空");
        if (outDateStr == null || outDateStr.isEmpty()) return Result.error("离店日期不能为空");
        if (roomCountObj == null) return Result.error("房间数量不能为空");

        Integer roomCount = Integer.valueOf(roomCountObj.toString());
        if (roomCount < 1) return Result.error("房间数量必须大于0");

        LocalDate inDate = LocalDate.parse(inDateStr);
        LocalDate outDate = LocalDate.parse(outDateStr);
        GroupInfo info = new GroupInfo();
        info.setGroupName(groupName);
        info.setGroupLeader(groupLeader);
        info.setLeaderPhone(leaderPhone);
        info.setPersonCount(roomCount);
        info.setCheckInDate(inDate.atStartOfDay());
        info.setPreLeaveDate(outDate);
        info.setCreateTime(LocalDateTime.now());
        groupInfoMapper.insert(info);

        List<String> roomNumbers = new ArrayList<>();
        for (int i = 0; i < roomCount; i++) {
            QueryWrapper<Room> rqw = new QueryWrapper<>();
            rqw.eq("room_type", roomType);
            List<Room> candidates = roomMapper.selectList(rqw);
            Room assigned = null;
            for (Room r : candidates) {
                QueryWrapper<Guest> gqw = new QueryWrapper<>();
                gqw.eq("room_id", r.getRoomId())
                        .in("status", Arrays.asList("在住", "已预订", "已锁"))
                        .lt("check_in_date", outDate.atStartOfDay())
                        .gt("pre_leave_date", inDate);
                if (guestMapper.selectCount(gqw) == 0) { assigned = r; break; }
            }
            if (assigned == null) {
                return Result.error("房源不足，已锁定" + i + "间，请减少房间数量");
            }
            Guest guest = new Guest();
            guest.setName(groupLeader);
            guest.setPhone(leaderPhone);
            guest.setIdCard("");
            guest.setRoomId(assigned.getRoomId());
            guest.setCheckInDate(inDate.atStartOfDay());
            guest.setPreLeaveDate(outDate);
            guest.setStatus("已预订");
            guest.setGuestType("团体");
            guest.setGroupId(info.getGroupId());
            guest.setCreateTime(LocalDateTime.now());
            guestMapper.insert(guest);
            roomNumbers.add(assigned.getRoomNumber());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("groupId", info.getGroupId());
        result.put("roomNumbers", roomNumbers);
        return Result.success(result);
    }

    @Operation(summary = "按团名/领队电话搜索集团组及旗下房间")
    @GetMapping("/group/search")
    public Result<List<Map<String, Object>>> groupSearch(@RequestParam(required = false) String keyword) {
        QueryWrapper<GroupInfo> qw = new QueryWrapper<>();
        if (keyword == null || keyword.isEmpty()) { qw = new QueryWrapper<>(); } else { qw.like("group_name", keyword).or().like("leader_phone", keyword); }
        List<GroupInfo> groups = groupInfoMapper.selectList(qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (GroupInfo g : groups) {
            QueryWrapper<Guest> gqw = new QueryWrapper<>();
            gqw.eq("group_id", g.getGroupId()).eq("status", "已预订").apply("DATE(pre_leave_date) >= DATE(NOW())");
            List<Guest> guests = guestMapper.selectList(gqw);
            List<Map<String, Object>> roomList = new ArrayList<>();
            for (Guest guest : guests) {
                Room room = roomMapper.selectById(guest.getRoomId());
                Map<String, Object> rm = new HashMap<>();
                rm.put("guestId", guest.getGuestId());
                rm.put("roomId", guest.getRoomId());
                rm.put("roomNumber", room != null ? room.getRoomNumber() : "");
                rm.put("roomType", room != null ? room.getRoomType() : "");
                rm.put("price", room != null ? room.getPrice() : 0);
                rm.put("guestName", guest.getName());
                rm.put("checkInDate", guest.getCheckInDate());
                rm.put("preLeaveDate", guest.getPreLeaveDate());
                roomList.add(rm);
            }
            Map<String, Object> item = new HashMap<>();
            item.put("groupId", g.getGroupId());
            item.put("groupName", g.getGroupName());
            item.put("groupLeader", g.getGroupLeader());
            item.put("leaderPhone", g.getLeaderPhone());
            item.put("personCount", g.getPersonCount());
            item.put("rooms", roomList);
            // 只返回仍有未入住房间的团队
            if (!roomList.isEmpty()) {
                result.add(item);
            }
        }
        return Result.success(result);
    }


    @Operation(summary = "获取房间预订信息")
    @GetMapping("/room/bookings/{roomId}")
    public Result<List<Map<String, Object>>> getRoomBookings(@PathVariable Integer roomId) {
        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.eq("room_id", roomId)
                .eq("status", "已预订")
                .apply("DATE(pre_leave_date) >= DATE(NOW())")
                .orderByAsc("check_in_date");
        List<Guest> bookings = guestMapper.selectList(qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Guest g : bookings) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", g.getName());
            map.put("checkInDate", g.getCheckInDate());
            map.put("preLeaveDate", g.getPreLeaveDate());
            map.put("phone", g.getPhone());
            result.add(map);
        }
        return Result.success(result);
    }

    @Operation(summary = "取消预订")
    @PostMapping("/cancelBooking")
    @Transactional
    public Result<?> cancelBooking(@RequestParam Integer guestId) {
        Guest guest = guestMapper.selectById(guestId);
        if (guest == null) return Result.error("预订记录不存在");
        if (!"已预订".equals(guest.getStatus())) return Result.error("该订单不是预订状态，不能取消");
        // 软删除：标记为"已取消"
        guest.setStatus("已取消");
        guestMapper.updateById(guest);
        // 房间可用性由 guest 表动态计算，无需释放 room.status
        return Result.success("取消成功");
    }

    /**
     * 登记物品损坏
     * @param params { roomId, itemName, amount }
     */
    @Operation(summary = "续房操作")
    @PostMapping("/extendStay")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> extendStay(@RequestBody Map<String, Object> params) {
        Integer guestId = Integer.valueOf(params.get("guestId").toString());
        String newLeaveDateStr = (String) params.get("newLeaveDate");
        BigDecimal additionalDeposit = params.get("additionalDeposit") != null
                ? new BigDecimal(params.get("additionalDeposit").toString()) : BigDecimal.ZERO;
        String payMethod = (String) params.get("payMethod");
        String remark = (String) params.get("remark");

        if (guestId == null || newLeaveDateStr == null) return Result.error("参数不完整");

        LocalDate newLeaveDate = LocalDate.parse(newLeaveDateStr);
        Guest guest = guestMapper.selectById(guestId);
        if (guest == null) return Result.error("客人不存在");
        if (!"在住".equals(guest.getStatus())) return Result.error("该客人不在住");

        LocalDate oldLeaveDate = guest.getPreLeaveDate();
        if (newLeaveDate.isBefore(oldLeaveDate) || newLeaveDate.isEqual(oldLeaveDate)) {
            return Result.error("新离店日期必须晚于原离店日期");
        }

        // 检查续房日期范围内是否有冲突
        List<Room> conflictCheck = findAvailableRooms(oldLeaveDate.plusDays(1), newLeaveDate, null, guestId);
        boolean roomStillAvailable = conflictCheck.stream().anyMatch(r -> r.getRoomId().equals(guest.getRoomId()));
        if (!roomStillAvailable) {
            return Result.error("该房间在" + oldLeaveDate.plusDays(1) + "至" + newLeaveDate + "期间已被其他预订占用，无法续房到该日期");
        }

        // 更新客人离店日期
        guest.setPreLeaveDate(newLeaveDate);
        guestMapper.updateById(guest);

        // 记录续房日志
        OrderExtensionLog log = new OrderExtensionLog();
        log.setGuestId(guestId);
        log.setRoomId(guest.getRoomId());
        log.setOldLeaveDate(oldLeaveDate);
        log.setNewLeaveDate(newLeaveDate);
        log.setAdditionalDeposit(additionalDeposit);
        log.setOperator(UserContext.getEmpName());
        log.setCreateTime(LocalDateTime.now());
        log.setRemark(remark);
        orderExtensionLogMapper.insert(log);

        // 补收押金
        if (additionalDeposit.compareTo(BigDecimal.ZERO) > 0) {
            Deposit deposit = new Deposit();
            deposit.setGuestId(guestId);
            deposit.setAmount(additionalDeposit);
            deposit.setType("收取");
            deposit.setPayMethod(payMethod);
            deposit.setRemark("续房补收押金");
            deposit.setCreateTime(LocalDateTime.now());
            deposit.setOperator(UserContext.getEmpName());
            depositMapper.insert(deposit);
        }

        return Result.success("续房成功，新离店日期：" + newLeaveDate);
    }

    @Operation(summary = "登记物品损坏赔偿")
    @PostMapping("/damage")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> registerDamage(@RequestBody Map<String, Object> params) {
        Integer roomId = (Integer) params.get("roomId");
        String itemName = (String) params.get("itemName");
        BigDecimal amount = new BigDecimal(params.get("amount").toString());

        // 1. 根据房间号查询当前在住客人
        QueryWrapper<Guest> guestQw = new QueryWrapper<>();
        guestQw.eq("room_id", roomId).eq("status", "在住");
        Guest guest = guestMapper.selectOne(guestQw);
        if (guest == null) {
            return Result.error("该房间无在住客人，无法登记损坏");
        }

        // 2. 插入 message 记录
        Message message = new Message();
        message.setGuestId(guest.getGuestId());
        message.setContent("物品损坏：" + itemName + "，赔偿金额：" + amount);
        message.setMsgType("损坏记录");
        message.setCreateTime(LocalDateTime.now());
        message.setIsRead(0);
        message.setOperator(UserContext.getEmpName());
        messageMapper.insert(message);

        // 3. 插入 order_detail 记录（未结账的杂项消费）
        OrderDetail detail = new OrderDetail();
        detail.setGuestId(guest.getGuestId());
        detail.setItemType("杂项");
        detail.setItemName("物品损坏赔偿 - " + itemName);
        detail.setQuantity(1);
        detail.setPrice(amount);
        detail.setAmount(amount);
        detail.setCreateTime(LocalDateTime.now());
        detail.setOperator(UserContext.getEmpName());
        detail.setIsSettled(0);
        orderDetailMapper.insert(detail);

        return Result.success("损坏登记成功，已记录到客人账单");
    }

    // ========== 通用：按日期范围查找可用房间 ==========
    private List<Room> findAvailableRooms(LocalDate startDate, LocalDate endDate, String roomType, Integer excludeGuestId) {
        QueryWrapper<Room> rqw = new QueryWrapper<>();
        if (roomType != null && !roomType.isEmpty()) {
            rqw.eq("room_type", roomType);
        }
        List<Room> allRooms = roomMapper.selectList(rqw);
        List<Room> available = new ArrayList<>();
        for (Room room : allRooms) {
            // 跳过物理状态为"占用"或"维修中"的房间
            if ("维修中".equals(room.getStatus())) continue;
            // 占用：查询日期包含今天则不可用，仅查未来日期可用
            if ("占用".equals(room.getStatus()) && !startDate.isAfter(LocalDate.now())) continue;
            QueryWrapper<Guest> gqw = new QueryWrapper<>();
            gqw.eq("room_id", room.getRoomId())
                    .in("status", Arrays.asList("已预订", "在住"))
                    .lt("check_in_date", endDate.atStartOfDay())
                    .gt("pre_leave_date", startDate);
            if (excludeGuestId != null) {
                gqw.ne("guest_id", excludeGuestId);
            }
            if (guestMapper.selectCount(gqw) == 0) {
                available.add(room);
            }
        }
        return available;
    }

}
