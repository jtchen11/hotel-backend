package com.hotel.service;

import com.hotel.common.Result;
import com.hotel.dto.GuestBillDTO;

public interface GuestBillService {
    Result<GuestBillDTO> getGuestBill(Integer guestId);
}