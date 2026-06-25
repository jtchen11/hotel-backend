package com.hotel.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.entity.Guest;
import com.hotel.mapper.GuestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class AutoCancelBookingTask {

    @Autowired
    private GuestMapper guestMapper;

    // 每天22:00执行
    @Scheduled(cron = "0 0 22 * * ?")
    @Transactional
    public void cancelExpiredBookings() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 条件1：今天入住，且当前时间 > 22:00，仍未办理入住
        LocalDateTime today22 = today.atTime(22, 0, 0);

        List<Guest> expired = guestMapper.selectList(
                new QueryWrapper<Guest>()
                        .eq("status", "已预订")
                        .and(w -> w
                                // 条件1：入住日期为今天，且现在已过22:00（直接用日期比较）
                                .and(w2 -> w2
                                        .apply("DATE(check_in_date) = {0}", today)
                                        .lt("check_in_date", now)
                                )
                                // 条件2：预离日期已过
                                .or(w2 -> w2.le("pre_leave_date", today))
                        )
        );
        if (expired.isEmpty()) {
            log.info("今日无过期预订需要取消");
            return;
        }
        for (Guest guest : expired) {
            guestMapper.deleteById(guest.getGuestId());
            log.info("自动取消未入住订单：guestId={}, roomId={}, checkIn={}, preLeave={}",
                    guest.getGuestId(), guest.getRoomId(),
                    guest.getCheckInDate(), guest.getPreLeaveDate());
        }
        log.info("自动取消任务执行完成，共取消 {} 条预订", expired.size());
    }
}