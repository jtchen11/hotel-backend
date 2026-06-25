package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.common.UserContext;
import com.hotel.dto.OrderDetailDTO;
import com.hotel.dto.SettleListDTO;
import com.hotel.dto.SettlePreviewDTO;
import com.hotel.dto.SettleRecordDTO;
import com.hotel.entity.*;
import com.hotel.mapper.*;
import com.hotel.service.SettleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SettleServiceImpl implements SettleService {

    @Autowired private GuestMapper guestMapper;
    @Autowired private RoomMapper roomMapper;
    @Autowired private OrderDetailMapper orderDetailMapper;
    @Autowired private DepositMapper depositMapper;
    @Autowired private OrderMainMapper orderMainMapper;
    @Autowired private KtvRecordMapper ktvRecordMapper;
    @Autowired private KtvRoomMapper ktvRoomMapper;

    // ---------- 无折扣版本（调用有折扣版本，折扣率为1） ----------
    @Override
    public Result<SettlePreviewDTO> preview(Integer guestId) {
        return preview(guestId, BigDecimal.ONE);
    }

    @Override
    public Result<Map<String, Object>> settle(Integer guestId) {
        return settle(guestId, BigDecimal.ONE);
    }

    // ---------- 带折扣版本 ----------
    @Override
    public Result<SettlePreviewDTO> preview(Integer guestId, BigDecimal discountRate) {
        Guest guest = guestMapper.selectById(guestId);
        if (guest == null || !"在住".equals(guest.getStatus())) {
            return Result.error("客人不存在或不在住");
        }

        // 检查未结账KTV
        Integer unsettledKtvCount = ktvRecordMapper.countUnsettledByGuestId(guestId);
        if (unsettledKtvCount != null && unsettledKtvCount > 0) {
            return Result.error("该客人尚有未结账的KTV消费，请先完成KTV结账");
        }

        // 房费
        Room room = roomMapper.selectById(guest.getRoomId());
        long days = calcStayDaysUpward(guest.getCheckInDate(), LocalDateTime.now());
        BigDecimal roomFee = room.getPrice().multiply(BigDecimal.valueOf(days));

        // 挂账消费（餐饮+杂项，不含KTV）
        List<OrderDetail> details = orderDetailMapper.selectUnsettledNotKtv(guestId);
        BigDecimal foodFee = BigDecimal.ZERO;
        BigDecimal otherFee = BigDecimal.ZERO;
        List<OrderDetailDTO> detailDTOs = new ArrayList<>();
        for (OrderDetail d : details) {
            if ("餐饮".equals(d.getItemType())) {
                foodFee = foodFee.add(d.getAmount());
            } else {
                otherFee = otherFee.add(d.getAmount());
            }
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setItemName(d.getItemName());
            dto.setQuantity(d.getQuantity());
            dto.setPrice(d.getPrice());
            dto.setAmount(d.getAmount());
            detailDTOs.add(dto);
        }

        // 已结KTV记录（仅展示，不参与金额计算）
        List<KtvRecord> ktvRecords = ktvRecordMapper.selectList(
                new QueryWrapper<KtvRecord>()
                        .eq("guest_id", guestId)
                        .eq("is_settled", 1)
                        .isNull("order_id")
        );
        List<OrderDetailDTO> ktvDTOs = new ArrayList<>();
        BigDecimal ktvTotal = BigDecimal.ZERO;
        for (KtvRecord kr : ktvRecords) {
            KtvRoom ktvRoom = ktvRoomMapper.selectById(kr.getKtvId());
            String roomName = ktvRoom != null ? ktvRoom.getRoomName() : "包房";
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setItemName(roomName + " " + kr.getDuration() + "小时");
            dto.setQuantity(1);
            dto.setPrice(kr.getTotalFee());
            dto.setAmount(kr.getTotalFee());
            ktvDTOs.add(dto);
            ktvTotal = ktvTotal.add(kr.getTotalFee()); // 仅用于展示，不用于计费
        }

        // 押金余额
        BigDecimal depositBalance = depositMapper.selectBalanceByGuestId(guestId);

        // 计算总消费（不含KTV）
        BigDecimal discountBase = roomFee.add(foodFee);
        BigDecimal discountedPart = discountBase.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
        // 总消费 = 折扣后部分 + 其他杂项（不含餐饮、不含KTV）
        BigDecimal totalConsume = discountedPart.add(otherFee);

        // 应付款 = 总消费 - 押金余额
        BigDecimal needPay = totalConsume.subtract(depositBalance);

        SettlePreviewDTO preview = new SettlePreviewDTO();
        preview.setGuestId(guestId);
        preview.setGuestName(guest.getName());
        preview.setRoomNumber(room.getRoomNumber());
        preview.setRoomFee(roomFee);
        preview.setDetails(detailDTOs);
        preview.setKtvList(ktvDTOs);
        preview.setKtvTotal(ktvTotal); // 仅展示
        preview.setTotalFee(totalConsume);
        preview.setDiscountedTotal(discountedPart);
        preview.setDepositBalance(depositBalance);
        preview.setNeedPay(needPay);
        preview.setDiscountRate(discountRate);
        return Result.success(preview);
    }

    @Override
    @Transactional
    public Result<Map<String, Object>> settle(Integer guestId, BigDecimal discountRate) {
        // 1. 获取预览数据
        Result<SettlePreviewDTO> previewResult = preview(guestId, discountRate);
        if (previewResult.getCode() != 200) {
            return Result.error(previewResult.getMsg());
        }
        SettlePreviewDTO preview = previewResult.getData();

        Guest guest = guestMapper.selectById(guestId);
        Room room = roomMapper.selectById(guest.getRoomId());

        // 2. 获取所有未结明细（不含KTV）
        List<OrderDetail> allDetails = orderDetailMapper.selectAllUnsettled(guestId);

        // 3. 计算分类金额
        BigDecimal foodFee = BigDecimal.ZERO, otherFee = BigDecimal.ZERO;
        for (OrderDetail d : allDetails) {
            if ("餐饮".equals(d.getItemType())) {
                foodFee = foodFee.add(d.getAmount());
            } else {
                otherFee = otherFee.add(d.getAmount());
            }
        }

        // KTV总额（仅用于记录，不参与总消费）
        BigDecimal ktvTotal = preview.getKtvTotal();

        // 4. 计算总消费（不含KTV）
        BigDecimal discountBase = preview.getRoomFee().add(foodFee);
        BigDecimal discountedPart = discountBase.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalConsume = discountedPart.add(otherFee);

        // 5. 插入订单主表
        OrderMain order = new OrderMain();
        order.setGuestId(guestId);
        order.setRoomId(guest.getRoomId());
        order.setRoomFee(preview.getRoomFee());
        order.setFoodFee(foodFee);
        order.setKtvFee(ktvTotal);               // 记录KTV总额，但不影响totalAmount
        order.setOtherFee(otherFee);
        order.setTotalAmount(totalConsume);      // 总消费不含KTV
        order.setDepositTotal(preview.getDepositBalance());
        // 退款金额 = 押金 - 总消费（如果押金大于总消费，则为正；否则为0）
        BigDecimal refundAmount = preview.getDepositBalance().subtract(totalConsume);
        order.setRefundAmount(refundAmount.compareTo(BigDecimal.ZERO) > 0 ? refundAmount : BigDecimal.ZERO);
        order.setStatus("已结算");
        order.setSettleTime(LocalDateTime.now());
        order.setOperator(UserContext.getEmpName());
        orderMainMapper.insert(order);

        // 6. 统一更新所有未结明细（关联订单）
        for (OrderDetail detail : allDetails) {
            detail.setIsSettled(1);
            detail.setOrderId(order.getOrderId());
            orderDetailMapper.updateById(detail);
        }

        // 7. 同步更新 ktv_record 的 order_id
        ktvRecordMapper.update(null,
                new UpdateWrapper<KtvRecord>()
                        .set("order_id", order.getOrderId())
                        .eq("guest_id", guestId)
                        .eq("is_settled", 1)
                        .isNull("order_id")
        );

        // 8. 客人离店
        guest.setStatus("已离店");
        guest.setActualLeaveDate(LocalDateTime.now());
        guestMapper.updateById(guest);

        // 9. 释放房间
        room.setStatus("打扫中");
        roomMapper.updateById(room);

        // 10. 自动退还押金（如果押金有剩余）
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            Deposit refund = new Deposit();
            refund.setGuestId(guestId);
            refund.setAmount(refundAmount.negate()); // 负值表示退还
            refund.setType("退还");
            refund.setPayMethod("原路返回");
            refund.setRemark("结账自动退还");
            refund.setCreateTime(LocalDateTime.now());
            refund.setOperator(UserContext.getEmpName());
            depositMapper.insert(refund);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("needPay", totalConsume.subtract(preview.getDepositBalance()));
        result.put("orderId", order.getOrderId());
        result.put("discountRate", discountRate);
        return Result.success(result);
    }

    // 计算入住天数（不足一天按一天计）
    private long calcStayDaysUpward(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (checkOut == null) checkOut = LocalDateTime.now();
        LocalDate startDate = checkIn.toLocalDate();
        LocalDate endDate = checkOut.toLocalDate();
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (checkOut.toLocalTime().getHour() > 0 || checkOut.toLocalTime().getMinute() > 0) {
            days++;
        }
        return days == 0 ? 1 : days;
    }

    @Override
    public Result<IPage<SettleRecordDTO>> listSettledOrders(SettleListDTO dto) {
        Page<SettleRecordDTO> page = new Page<>(dto.getPage(), dto.getSize());
        IPage<SettleRecordDTO> result = orderMainMapper.selectSettledOrders(
                page, dto.getStartDate(), dto.getEndDate(), dto.getKeyword());
        return Result.success(result);
    }
}