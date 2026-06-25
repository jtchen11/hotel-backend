package com.hotel.service;

import com.hotel.entity.Guest;
import com.hotel.entity.KtvRecord;
import com.hotel.entity.KtvRoom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface KtvService {
    List<KtvRoom> listRooms();
    void startKtv(Integer ktvId, Integer guestId, String operator);
    void endKtv(Integer guestId, String operator, BigDecimal customDuration);
    Map<String, Object> prepareEndKtv(Integer guestId);
    void changeStatus(Integer ktvId, String status);
    List<KtvRecord> getKtvRecordsByGuest(Integer guestId);
    List<KtvRecord> getAllKtvRecords(Integer days);
    Guest getRoomCurrentGuest(Integer ktvId);
}