package com.hotel.service;

import java.util.Map;

public interface GmService {
    Map<String, Object> getKpiData();
    Map<String, Object> getRevenueByType(String startDate, String endDate);
    Map<String, Object> getOccupancyRate(String startDate, String endDate, String granularity);
    Map<String, Object> getStaffRoomRatio();
    Map<String, Object> getGuestSource(String dimension);
}