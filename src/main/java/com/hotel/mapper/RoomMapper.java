package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.dto.RoomStatusDTO;
import com.hotel.entity.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {
    @Select("SELECT r.room_id, r.room_number, r.room_type, r.price, r.status, r.description, r.image, " +
            "       g.name as guest_name, g.guest_id as guest_id " +
            "FROM room r " +
            "LEFT JOIN guest g ON r.room_id = g.room_id AND g.status = '在住' " +
            "ORDER BY r.room_id")
    List<RoomStatusDTO> selectRoomsWithGuest();
}