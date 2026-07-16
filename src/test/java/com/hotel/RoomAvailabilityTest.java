package com.hotel;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.entity.Guest;
import com.hotel.mapper.GuestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RoomAvailabilityTest {

    @Autowired
    private GuestMapper guestMapper;

    @Test
    public void testDateOverlap_shouldDetectConflict() {
        Guest existing = new Guest();
        existing.setRoomId(101);
        existing.setCheckInDate(LocalDateTime.of(2026, 7, 20, 14, 0));
        existing.setPreLeaveDate(LocalDate.of(2026, 7, 22));
        existing.setStatus("在住");
        existing.setName("张三");
        existing.setIdCard("A001");
        guestMapper.insert(existing);

        long conflictCount = guestMapper.selectCount(new QueryWrapper<Guest>()
                .eq("room_id", 101)
                .in("status", Arrays.asList("已预订", "在住"))
                .lt("check_in_date", LocalDate.of(2026, 7, 23).atStartOfDay())
                .gt("pre_leave_date", LocalDate.of(2026, 7, 21)));
        assertTrue(conflictCount > 0, "重叠日期应检测为冲突");

        long freeCount = guestMapper.selectCount(new QueryWrapper<Guest>()
                .eq("room_id", 101)
                .in("status", Arrays.asList("已预订", "在住"))
                .lt("check_in_date", LocalDate.of(2026, 7, 25).atStartOfDay())
                .gt("pre_leave_date", LocalDate.of(2026, 7, 23)));
        assertEquals(0, freeCount, "非重叠日期应可用");
    }
}