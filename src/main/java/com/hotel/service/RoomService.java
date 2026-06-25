package com.hotel.service;

import com.hotel.dto.RoomStatusDTO;
import java.util.List;
import java.util.Map;

public interface RoomService {
    List<RoomStatusDTO> getAllRoomsWithGuest();
    Map<String, Integer> getRoomCount();
}