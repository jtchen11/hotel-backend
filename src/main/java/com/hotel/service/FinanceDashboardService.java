package com.hotel.service;

import java.util.List;
import java.util.Map;

public interface FinanceDashboardService {
    List<Map<String, Object>> getLivingGuests();
    List<Map<String, Object>> getTodayAttendance();
    Map<String, Object> getDashboardStats();
    int getUnsettledCount();
    List<Map<String, Object>> getWeeklyRevenue();
}