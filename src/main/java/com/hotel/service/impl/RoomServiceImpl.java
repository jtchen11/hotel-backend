package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.dto.RoomStatusDTO;
import com.hotel.entity.Room;
import com.hotel.mapper.RoomMapper;
import com.hotel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomMapper roomMapper;

    @Override
    public List<RoomStatusDTO> getAllRoomsWithGuest() {
        return roomMapper.selectRoomsWithGuest();
    }

    @Override
    public Map<String, Integer> getRoomCount() {
        Map<String, Integer> res = new HashMap<>();
        int total = Math.toIntExact(roomMapper.selectCount(null));
        int free = Math.toIntExact(roomMapper.selectCount(new QueryWrapper<Room>().eq("status", "空闲")));
        int used = Math.toIntExact(roomMapper.selectCount(new QueryWrapper<Room>().eq("status", "占用")));
        int repair = Math.toIntExact(roomMapper.selectCount(new QueryWrapper<Room>().eq("status", "维修中")));
        res.put("total", total);
        res.put("free", free);
        res.put("used", used);
        res.put("repair", repair);
        return res;
    }
}