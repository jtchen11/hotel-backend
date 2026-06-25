package com.hotel.controller.reception;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.common.Result;
import com.hotel.entity.Guest;
import com.hotel.entity.Message;
import com.hotel.entity.Room;
import com.hotel.mapper.GuestMapper;
import com.hotel.mapper.MessageMapper;
import com.hotel.mapper.RoomMapper;
import com.hotel.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reception/message")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private GuestMapper guestMapper;
    @Autowired
    private RoomMapper roomMapper;

    // 列表查询（含房间号）
    @GetMapping("/list")
    public Result<List<Message>> list(
            @RequestParam(required = false) String msgType,
            @RequestParam(required = false) Integer isRead   // 0未读 1已读
    ) {
        QueryWrapper<Message> qw = new QueryWrapper<>();
        if (msgType != null && !msgType.isEmpty()) {
            qw.eq("msg_type", msgType);
        }
        if (isRead != null) {
            qw.eq("is_read", isRead);
        }
        qw.orderByDesc("create_time");
        List<Message> list = messageMapper.selectList(qw);
        // 填充房间号（同上）
        for (Message msg : list) {
            Guest guest = guestMapper.selectById(msg.getGuestId());
            if (guest != null) {
                Room room = roomMapper.selectById(guest.getRoomId());
                if (room != null) msg.setRoomNumber(room.getRoomNumber());
            }
        }
        return Result.success(list);
    }

    // 未读留言数量
    @GetMapping("/unread")
    public Result<Integer> unread() {
        QueryWrapper<Message> qw = new QueryWrapper<>();
        qw.eq("is_read", 0);
        return Result.success(messageMapper.selectCount(qw).intValue());
    }

    // 新增留言
    @PostMapping("/add")
    public Result<?> add(@RequestBody Message msg) {
        if (msg.getRoomId() == null) {
            return Result.error("请选择房间");
        }
        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.eq("room_id", msg.getRoomId()).eq("status", "在住");
        Guest guest = guestMapper.selectOne(qw);
        if (guest == null) {
            return Result.error("该房间暂无入住客人");
        }
        msg.setGuestId(guest.getGuestId());
        msg.setCreateTime(LocalDateTime.now());
        msg.setIsRead(0);
        messageMapper.insert(msg);
        return Result.success("添加成功");
    }

    // 标记已读
    @PutMapping("/read/{id}")
    public Result<?> read(@PathVariable Integer id) {
        Message msg = messageMapper.selectById(id);
        if (msg == null) return Result.error("留言不存在");
        msg.setIsRead(1);
        msg.setReadTime(LocalDateTime.now());
        messageMapper.updateById(msg);
        return Result.success("标记已读成功");
    }

    @GetMapping("/guest/search")
    public Result<List<Map<String, Object>>> searchGuest(@RequestParam String keyword) {
        // 查询在住客人，并关联房间表
        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.eq("status", "在住");
        // 关键字搜索：姓名、电话模糊匹配，房号精确匹配（房号在room表）
        List<Guest> guests = guestMapper.selectList(qw);

        // 收集 roomId 列表
        List<Integer> roomIds = guests.stream().map(Guest::getRoomId).collect(Collectors.toList());
        if (roomIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        // 查询房间信息
        List<Room> rooms = roomMapper.selectBatchIds(roomIds);
        Map<Integer, Room> roomMap = rooms.stream().collect(Collectors.toMap(Room::getRoomId, r -> r));

        // 过滤并组装结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (Guest guest : guests) {
            Room room = roomMap.get(guest.getRoomId());
            if (room == null) continue;
            String roomNumber = room.getRoomNumber();
            // 匹配关键字：房号精确匹配 或 姓名/电话包含关键字（不区分大小写）
            boolean match = false;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.trim();
                if (roomNumber.equals(kw) ||
                        (guest.getName() != null && guest.getName().contains(kw)) ||
                        (guest.getPhone() != null && guest.getPhone().contains(kw))) {
                    match = true;
                }
            } else {
                match = true; // 无关键字时返回全部在住客人
            }
            if (match) {
                Map<String, Object> item = new HashMap<>();
                item.put("guestId", guest.getGuestId());
                item.put("name", guest.getName());
                item.put("phone", guest.getPhone());
                item.put("roomId", guest.getRoomId());
                item.put("roomNumber", roomNumber);
                result.add(item);
            }
        }
        // 按房号排序
        result.sort(Comparator.comparing(m -> (String) m.get("roomNumber")));
        return Result.success(result);
    }
}