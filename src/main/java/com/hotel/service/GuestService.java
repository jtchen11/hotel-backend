package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.dto.GuestQueryDTO;
import com.hotel.entity.Guest;
import java.util.List;

public interface GuestService extends IService<Guest> {
    List<GuestQueryDTO> queryGuestList(String status, String keyword, Integer roomId, String guestType, Integer groupId);
    Guest getById(Integer guestId);
}