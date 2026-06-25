package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.common.Result;
import com.hotel.dto.GuestBillDTO;
import com.hotel.dto.OrderDetailDTO;
import com.hotel.dto.OrderMainDTO;
import com.hotel.entity.*;
import com.hotel.mapper.*;
import com.hotel.service.GuestBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GuestBillServiceImpl implements GuestBillService {

    @Autowired private GuestMapper guestMapper;
    @Autowired private RoomMapper roomMapper;
    @Autowired private DepositMapper depositMapper;
    @Autowired private OrderDetailMapper orderDetailMapper;
    @Autowired private OrderMainMapper orderMainMapper;
    @Autowired private KtvRecordMapper ktvRecordMapper;
    @Autowired private KtvRoomMapper ktvRoomMapper;

    @Override
    public Result<GuestBillDTO> getGuestBill(Integer guestId) {
        Guest guest = guestMapper.selectById(guestId);
        if (guest == null) return Result.error("客人不存在");
        Room room = roomMapper.selectById(guest.getRoomId());

        GuestBillDTO dto = new GuestBillDTO();
        dto.setGuestId(guestId);
        dto.setGuestName(guest.getName());
        if (room != null) {
            dto.setRoomNumber(room.getRoomNumber());
            dto.setRoomPrice(room.getPrice());   // 新增：设置单日房费
        } else {
            dto.setRoomNumber("");
        }
        dto.setCheckInDate(guest.getCheckInDate());
        dto.setPreLeaveDate(guest.getPreLeaveDate().atStartOfDay());
        // 押金余额
        BigDecimal balance = depositMapper.selectBalanceByGuestId(guestId);
        dto.setDepositBalance(balance);

        // 未结消费分组
        List<Map<String, Object>> groups = orderDetailMapper.selectUnsettledGroupByType(guestId);
        BigDecimal foodFee = BigDecimal.ZERO, damageFee = BigDecimal.ZERO;
        for (Map<String, Object> map : groups) {
            String type = (String) map.get("item_type");
            BigDecimal total = (BigDecimal) map.get("total");
            if ("餐饮".equals(type)) foodFee = total;
            else if ("杂项".equals(type)) damageFee = total;
        }
        dto.setUnsettledFoodFee(foodFee);
        dto.setUnsettledDamageFee(damageFee);

        // 未结明细列表
        List<OrderDetail> details = orderDetailMapper.selectUnsettledNotKtv(guestId);
        List<OrderDetailDTO> detailDTOs = details.stream().map(d -> {
            OrderDetailDTO dto1 = new OrderDetailDTO();
            dto1.setOrderId(d.getOrderId());
            dto1.setItemType(d.getItemType());
            dto1.setItemName(d.getItemName());
            dto1.setQuantity(d.getQuantity());
            dto1.setPrice(d.getPrice());
            dto1.setAmount(d.getAmount());
            dto1.setCreateTime(d.getCreateTime());
            dto1.setOperator(d.getOperator());
            return dto1;
        }).collect(Collectors.toList());
        dto.setUnsettledDetails(detailDTOs);

        // 已结 KTV 记录
        List<KtvRecord> ktvRecords = ktvRecordMapper.selectList(
                new QueryWrapper<KtvRecord>()
                        .eq("guest_id", guestId)
                        .eq("is_settled", 1)
                        .orderByDesc("end_time")
        );
        List<OrderDetailDTO> ktvDTOs = new ArrayList<>();
        BigDecimal ktvTotal = BigDecimal.ZERO;
        for (KtvRecord kr : ktvRecords) {
            KtvRoom ktvRoom = ktvRoomMapper.selectById(kr.getKtvId());
            String roomName = ktvRoom != null ? ktvRoom.getRoomName() : "包房";
            OrderDetailDTO ktvDto = new OrderDetailDTO();
            ktvDto.setItemName(roomName + " " + kr.getDuration() + "小时");
            ktvDto.setQuantity(1);
            ktvDto.setPrice(kr.getTotalFee());
            ktvDto.setAmount(kr.getTotalFee());
            ktvDto.setEndTime(kr.getEndTime());
            ktvDTOs.add(ktvDto);
            ktvTotal = ktvTotal.add(kr.getTotalFee());
        }
        dto.setKtvList(ktvDTOs);
        dto.setKtvTotal(ktvTotal);

        // 历史已结订单
        List<OrderMainDTO> orders = orderMainMapper.selectSettledOrdersByGuestId(guestId);
        dto.setSettledOrders(orders);

        return Result.success(dto);
    }
}