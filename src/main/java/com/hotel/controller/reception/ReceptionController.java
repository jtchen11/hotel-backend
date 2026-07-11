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

    // ========== 散客入住 ==========
    // ========== 散客入住 ==========
    @Operation(summary = "散客入住登记")
    @PostMapping("/checkin")
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> checkIn(@RequestBody Map<String, Object> params) {
        Guest guest = new Guest();
        guest.setName((String) params.get("name"));
        guest.setGender((String) params.get("gender"));
        guest.setIdCard((String) params.get("idCard"));
        guest.setPhone((String) params.get("phone"));
        guest.setRoomId(Integer.valueOf(params.get("roomId").toString()));

        // ====== 新增：补全字段 ======
        guest.setNativePlace((String) params.get("nativePlace"));
        guest.setCompany((String) params.get("company"));
        guest.setOccupation((String) params.get("occupation"));
        guest.setReason((String) params.get("reason"));
        // ==========================

        String inDateStr = (String) params.get("checkInDate");
        String outDateStr = (String) params.get("preLeaveDate");
        LocalDateTime checkIn = LocalDate.parse(inDateStr).atStartOfDay();
        LocalDate preLeave = LocalDate.parse(outDateStr);

        // ====== 新增：身份证格式校验 ======
        if (guest.getIdCard() == null || !guest.getIdCard().matches("^[1-9]\\d{5}(18|19|20)?\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$")) {
            return Result.error("身份证号格式不正确，请输入18位有效身份证号");
        }
        // ====== 新增：手机号格式校验 ======
        if (guest.getPhone() == null || !guest.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确，请输入11位手机号");
        }
        // ====== 新增：籍贯必填 ======
        if (guest.getNativePlace() == null || guest.getNativePlace().trim().isEmpty()) {
            return Result.error("籍贯为必填项");
        }

        // 身份证重复校验
        QueryWrapper<Guest> existsQw = new QueryWrapper<>();
        existsQw.eq("id_card", guest.getIdCard())
                .in("status", Arrays.asList("在住", "已预订"));
        if (guestMapper.selectCount(existsQw) > 0) {
            return Result.error("该身份证已有在住或预订记录，不能重复预订");
        }

        guest.setCheckInDate(checkIn);
        guest.setPreLeaveDate(preLeave);

        Room room = roomMapper.selectById(guest.getRoomId());
        if (room == null) return Result.error("房间不存在");


        // 时间段冲突查询
        QueryWrapper<Guest> guestQw = new QueryWrapper<>();
        guestQw.eq("room_id", guest.getRoomId())
                .in("status", Arrays.asList("在住", "已预订"))
                .lt("check_in_date", preLeave.atStartOfDay())
                .gt("pre_leave_date", checkIn.toLocalDate());
        long count = guestMapper.selectCount(guestQw);
        if (count > 0) {
            return Result.error("该时间段已被预订！");
        }

        guest.setStatus("已预订");
        guest.setGuestType("散客");
        guest.setCreateTime(LocalDateTime.now());
        guestMapper.insert(guest);

        Map<String, Object> result = new HashMap<>();
        result.put("guestId", guest.getGuestId());
        return Result.success(result);
    }

    // ========== 团体入住（修复冲突、日期、身份证） ==========
    @Operation(summary = "团体入住登记")
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
                    .in("status", Arrays.asList("在住", "已预订"))
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
            guest.setCreateTime(LocalDateTime.now());
            guestMapper.insert(guest);
        }

        return Result.success("团体入住成功");
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
        return Result.success("ok");
    }

    @Operation(summary = "按日期查询房间状态列表")
    @GetMapping("/room/dateList")
    public Result<List<Map<String, Object>>> getRoomDateList(
            @RequestParam String inDate,
            @RequestParam String outDate) {
        LocalDateTime d1 = LocalDate.parse(inDate).atStartOfDay();
        LocalDateTime d2 = LocalDate.parse(outDate).atStartOfDay();

        List<Room> all = roomMapper.selectList(null);

        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.in("status", Arrays.asList("在住", "已预订"))
                .lt("check_in_date", d2)
                .gt("pre_leave_date", d1);
        qw.select("room_id");
        List<Object> objList = guestMapper.selectObjs(qw);
        Set<Integer> busyRoomIds = objList.stream().map(id -> (Integer) id).collect(Collectors.toSet());

        List<Map<String, Object>> list = new ArrayList<>();
        for (Room r : all) {
            Map<String, Object> map = new HashMap<>();
            map.put("roomId", r.getRoomId());
            map.put("roomNumber", r.getRoomNumber());
            map.put("roomType", r.getRoomType());
            map.put("price", r.getPrice());
            map.put("isBusy", busyRoomIds.contains(r.getRoomId()));
            map.put("status", r.getStatus());   // 新增：返回房间当前状态
            list.add(map);
        }
        return Result.success(list);
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
    public Result<List<Map<String, Object>>> pendingBookings() {
        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.eq("status", "已预订")
                .ge("check_in_date", LocalDate.now().atStartOfDay())  // 入住日期 >= 今天凌晨
                .orderByAsc("check_in_date");
        List<Guest> guests = guestMapper.selectList(qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Guest guest : guests) {
            Room room = roomMapper.selectById(guest.getRoomId());
            Map<String, Object> map = new HashMap<>();
            map.put("guestId", guest.getGuestId());
            map.put("name", guest.getName());
            map.put("phone", guest.getPhone());
            map.put("roomNumber", room.getRoomNumber());
            map.put("roomType", room.getRoomType());
            map.put("checkInDate", guest.getCheckInDate());
            map.put("preLeaveDate", guest.getPreLeaveDate());
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

        LocalDate today = LocalDate.now();
        LocalDate checkInDate = guest.getCheckInDate().toLocalDate();
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime allowedStart = checkInDate.atTime(12, 0, 0);
        if (now.isBefore(allowedStart)) {
            return Result.error("未到办理入住时间，请于" + allowedStart + "后办理");
        }

        // ====== 新增：校验房间状态 ======
        Room room = roomMapper.selectById(guest.getRoomId());
        if (room == null) return Result.error("房间不存在");
        if (!"空闲".equals(room.getStatus())) {
            return Result.error("房间当前状态为【" + room.getStatus() + "】，无法办理入住");
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
    private DepositMapper depositMapper;
    @Operation(summary = "获取房间预订信息")
    @GetMapping("/room/bookings/{roomId}")
    public Result<List<Map<String, Object>>> getRoomBookings(@PathVariable Integer roomId) {
        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.eq("room_id", roomId)
                .eq("status", "已预订")
                .apply("DATE(check_in_date) >= DATE(NOW())")
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
        guestMapper.deleteById(guestId);
        return Result.success("取消成功");
    }
    /**
     * 登记物品损坏
     * @param params { roomId, itemName, amount }
     */
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

}