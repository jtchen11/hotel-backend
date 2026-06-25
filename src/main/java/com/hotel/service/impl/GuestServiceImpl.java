package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.dto.GuestQueryDTO;
import com.hotel.entity.Guest;
import com.hotel.entity.Room;
import com.hotel.mapper.GuestMapper;
import com.hotel.mapper.RoomMapper;
import com.hotel.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class GuestServiceImpl implements GuestService {

    @Autowired
    private GuestMapper guestMapper;

    @Autowired
    private RoomMapper roomMapper;

    // ———————— 你原来的代码 保留不动 ————————
    @Override
    public List<GuestQueryDTO> queryGuestList(String status, String keyword, Integer roomId, String guestType, Integer groupId) {
        return guestMapper.selectGuestList(status, keyword, roomId, guestType, groupId);
    }

    @Override
    public Guest getById(Integer guestId) {
        return guestMapper.selectById(guestId);
    }

    @Override
    public boolean save(Guest guest) {

        return false;
    }

    @Override
    public boolean saveBatch(Collection<Guest> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<Guest> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<Guest> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(Guest entity) {
        return false;
    }

    @Override
    public Guest getOne(Wrapper<Guest> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<Guest> queryWrapper) {
        return Map.of();
    }

    @Override
    public <V> V getObj(Wrapper<Guest> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<Guest> getBaseMapper() {
        return null;
    }

    @Override
    public Class<Guest> getEntityClass() {
        return null;
    }

    // ———————— 新增：散客入住 ————————
    @Transactional
    public boolean checkIn(Guest guest) {
        // 校验房间是否空闲
        Room room = roomMapper.selectById(guest.getRoomId());
        if (room == null || !"空闲".equals(room.getStatus())) {
            throw new RuntimeException("房间不存在或不是空闲状态");
        }

        // 赋值默认信息
        guest.setCheckInDate(LocalDateTime.now());
        guest.setStatus("在住");
        guest.setCreateTime(LocalDateTime.now());

        // 插入客人
        int rows = guestMapper.insert(guest);

        // 修改房间状态为占用
        room.setStatus("占用");
        roomMapper.updateById(room);

        return rows > 0;
    }

    // ———————— 新增：换房 ————————
    @Transactional
    public boolean changeRoom(Integer guestId, Integer newRoomId) {
        // 获取客人信息
        Guest guest = guestMapper.selectById(guestId);
        if (guest == null) throw new RuntimeException("客人不存在");
        if (!"在住".equals(guest.getStatus())) throw new RuntimeException("客人不在住，无法换房");

        // 旧房间 → 空闲
        Integer oldRoomId = guest.getRoomId();
        Room oldRoom = roomMapper.selectById(oldRoomId);
        oldRoom.setStatus("空闲");
        roomMapper.updateById(oldRoom);

        // 新房间 → 占用
        Room newRoom = roomMapper.selectById(newRoomId);
        if (newRoom == null || !"空闲".equals(newRoom.getStatus())) {
            throw new RuntimeException("目标房间不可用");
        }
        newRoom.setStatus("占用");
        roomMapper.updateById(newRoom);

        // 更新客人房间
        guest.setRoomId(newRoomId);
        guestMapper.updateById(guest);

        return true;
    }
}