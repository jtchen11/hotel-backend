package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.entity.Guest;
import com.hotel.entity.KtvRecord;
import com.hotel.entity.KtvRoom;
import com.hotel.entity.OrderDetail;
import com.hotel.exception.BusinessException;
import com.hotel.mapper.KtvRecordMapper;
import com.hotel.mapper.KtvRoomMapper;
import com.hotel.mapper.OrderDetailMapper;
import com.hotel.service.GuestService;
import com.hotel.service.KtvService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KtvServiceImpl implements KtvService {

    @Resource
    private KtvRoomMapper ktvRoomMapper;

    @Resource
    private KtvRecordMapper ktvRecordMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private GuestService guestService;

    @Override
    public List<KtvRoom> listRooms() {
        return ktvRoomMapper.selectList(null);
    }

    @Override
    @Transactional
    public void startKtv(Integer ktvId, Integer guestId, String operator) {
        Guest guest = guestService.getById(guestId);
        if (guest == null || !"在住".equals(guest.getStatus())) {
            throw new BusinessException("该客人当前不在住，无法开房");
        }
        KtvRoom room = ktvRoomMapper.selectById(ktvId);
        if (room == null) {
            throw new BusinessException("包房不存在");
        }
        if (!KtvRoom.STATUS_FREE.equals(room.getStatus())) {
            throw new BusinessException("包房当前状态为[" + room.getStatus() + "]，无法开房");
        }

        LambdaQueryWrapper<KtvRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KtvRecord::getGuestId, guestId)
                .isNull(KtvRecord::getEndTime);
        long count = ktvRecordMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该客人已有未结账的KTV消费，请先结账");
        }

        KtvRecord record = new KtvRecord();
        record.setKtvId(ktvId);
        record.setGuestId(guestId);
        record.setStartTime(LocalDateTime.now());
        record.setIsSettled(0);
        record.setOperator(operator);
        ktvRecordMapper.insert(record);

        room.setStatus(KtvRoom.STATUS_IN_USE);
        ktvRoomMapper.updateById(room);
    }

    @Override
    @Transactional
    public void endKtv(Integer guestId, String operator, BigDecimal customDuration) {
        LambdaQueryWrapper<KtvRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KtvRecord::getGuestId, guestId).isNull(KtvRecord::getEndTime);
        KtvRecord record = ktvRecordMapper.selectOne(wrapper);
        if (record == null) {
            throw new BusinessException("未找到进行中的KTV消费记录");
        }

        LocalDateTime start = record.getStartTime();
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        double realHours = duration.toMinutes() / 60.0;
        double minHours = Math.ceil(realHours); // 自动计算用

        KtvRoom room = ktvRoomMapper.selectById(record.getKtvId());

        BigDecimal finalDuration;
        if (customDuration != null && customDuration.compareTo(BigDecimal.ZERO) > 0) {
            // 手动模式：必须为整数，且 >= 1
            if (customDuration.scale() > 0) {
                throw new BusinessException("时长必须为整数小时");
            }
            if (customDuration.compareTo(BigDecimal.ONE) < 0) {
                throw new BusinessException("时长至少为1小时");
            }
            // 直接使用用户输入，不四舍五入
            finalDuration = customDuration;
        } else {
            // 自动模式：向上取整，受 maxHour 封顶
            double maxHour = room.getMaxHour() != null ? room.getMaxHour() : Double.MAX_VALUE;
            double finalHours = Math.min(minHours, maxHour);
            finalDuration = BigDecimal.valueOf(finalHours).setScale(0, RoundingMode.HALF_UP);
        }

        BigDecimal totalFee = finalDuration.multiply(room.getHourlyRate()).setScale(2, RoundingMode.HALF_UP);

        record.setEndTime(end);
        record.setDuration(finalDuration);
        record.setTotalFee(totalFee);
        record.setOperator(operator);
        ktvRecordMapper.updateById(record);

        OrderDetail detail = new OrderDetail();
        detail.setGuestId(guestId);
        detail.setItemType("KTV");
        detail.setItemName(room.getRoomName() + " " + finalDuration + "小时");
        detail.setQuantity(1);
        detail.setPrice(totalFee);
        detail.setAmount(totalFee);
        detail.setCreateTime(LocalDateTime.now());
        detail.setOperator(operator);
        detail.setIsSettled(1);
        orderDetailMapper.insert(detail);

        record.setIsSettled(1);
        ktvRecordMapper.updateById(record);

        room.setStatus(KtvRoom.STATUS_CLEANING);
        ktvRoomMapper.updateById(room);
    }
    @Override
    public Map<String, Object> prepareEndKtv(Integer guestId) {
        LambdaQueryWrapper<KtvRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KtvRecord::getGuestId, guestId).isNull(KtvRecord::getEndTime);
        KtvRecord record = ktvRecordMapper.selectOne(wrapper);
        if (record == null) {
            throw new BusinessException("未找到进行中的KTV消费记录");
        }

        LocalDateTime start = record.getStartTime();
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(start, now);
        double realHours = duration.toMinutes() / 60.0;
        double minHours = Math.ceil(realHours);   // 向上取整

        KtvRoom room = ktvRoomMapper.selectById(record.getKtvId());

        // 自动计算时长（封顶 maxHour）
        double maxHour = room.getMaxHour() != null ? room.getMaxHour() : Double.MAX_VALUE;
        double finalHours = Math.min(minHours, maxHour);
        BigDecimal durationBD = BigDecimal.valueOf(finalHours).setScale(0, RoundingMode.HALF_UP);
        BigDecimal totalFee = durationBD.multiply(room.getHourlyRate()).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> result = new HashMap<>();
        result.put("duration", durationBD);         // 自动计算整数时长
        result.put("totalFee", totalFee);
        result.put("roomName", room.getRoomName());
        result.put("hourlyRate", room.getHourlyRate());
        result.put("maxHour", room.getMaxHour());   // 返回 maxHour 供前端展示
        return result;
    }

    @Override
    @Transactional
    public void changeStatus(Integer ktvId, String status) {
        KtvRoom room = ktvRoomMapper.selectById(ktvId);
        if (room == null) {
            throw new BusinessException("包房不存在");
        }
        if (KtvRoom.STATUS_IN_USE.equals(room.getStatus())) {
            throw new BusinessException("包房正在使用中，无法手动修改状态，请先结账");
        }
        room.setStatus(status);
        ktvRoomMapper.updateById(room);
    }

    @Override
    public List<KtvRecord> getKtvRecordsByGuest(Integer guestId) {
        List<KtvRecord> records = ktvRecordMapper.selectList(
                new LambdaQueryWrapper<KtvRecord>()
                        .eq(KtvRecord::getGuestId, guestId)
                        .isNotNull(KtvRecord::getEndTime)
                        .orderByDesc(KtvRecord::getEndTime)
        );
        Guest guest = guestService.getById(guestId);
        String gName = guest == null ? "" : guest.getName();
        for (KtvRecord record : records) {
            KtvRoom room = ktvRoomMapper.selectById(record.getKtvId());
            if (room != null)
                record.setRoomName(room.getRoomName());
            record.setGuestName(gName);
        }
        return records;
    }

    @Override
    public List<KtvRecord> getAllKtvRecords(Integer days) {
        int range = (days == null) ? 30 : days;
        LocalDateTime startTime = LocalDateTime.now().minusDays(range);
        List<KtvRecord> records = ktvRecordMapper.selectList(
                new LambdaQueryWrapper<KtvRecord>()
                        .isNotNull(KtvRecord::getEndTime)
                        .ge(KtvRecord::getEndTime, startTime)
                        .orderByDesc(KtvRecord::getEndTime)
        );
        for (KtvRecord record : records) {
            KtvRoom room = ktvRoomMapper.selectById(record.getKtvId());
            if (room != null) {
                record.setRoomName(room.getRoomName());
            }
            Guest guest = guestService.getById(record.getGuestId());
            if (guest != null) {
                record.setGuestName(guest.getName());
            } else {
                record.setGuestName("");
            }
        }
        return records;
    }

    @Override
    public Guest getRoomCurrentGuest(Integer ktvId) {
        LambdaQueryWrapper<KtvRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KtvRecord::getKtvId, ktvId)
                .isNull(KtvRecord::getEndTime);

        KtvRecord record = ktvRecordMapper.selectOne(wrapper);
        if (record == null) {
            throw new BusinessException("该包房暂无正在使用的订单");
        }
        return guestService.getById(record.getGuestId());
    }
}