package com.hotel.controller.finance;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.common.Result;
import com.hotel.dto.SettleListDTO;
import com.hotel.dto.SettlePreviewDTO;
import com.hotel.dto.SettleRecordDTO;
import com.hotel.entity.*;
import com.hotel.mapper.*;
import com.hotel.service.SettleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
public class SettleController {

    @Autowired
    private SettleService settleService;
    @Autowired
    private OrderMainMapper orderMainMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private GuestMapper guestMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private KtvRecordMapper ktvRecordMapper;
    @Autowired
    private KtvRoomMapper ktvRoomMapper;

    // 预览（支持可选折扣率）
    @GetMapping("/settle/prepare/{guestId}")
    public Result<SettlePreviewDTO> prepare(
            @PathVariable Integer guestId,
            @RequestParam(required = false) BigDecimal discountRate) {
        if (discountRate == null) {
            return settleService.preview(guestId);
        } else {
            return settleService.preview(guestId, discountRate);
        }
    }

    // 结账（支持可选折扣率）
    @PostMapping("/settle")
    public Result<Map<String, Object>> settle(
            @RequestParam Integer guestId,
            @RequestParam(required = false) BigDecimal discountRate) {
        if (discountRate == null) {
            return settleService.settle(guestId);
        } else {
            return settleService.settle(guestId, discountRate);
        }
    }

    @GetMapping("/order/bill/{orderId}")
    public Result<Map<String, Object>> getOrderBill(@PathVariable Integer orderId) {
        OrderMain order = orderMainMapper.selectById(orderId);
        if (order == null) return Result.error("订单不存在");

        Guest guest = guestMapper.selectById(order.getGuestId());
        Room room = roomMapper.selectById(order.getRoomId());

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", order.getOrderId());
        data.put("totalAmount", order.getTotalAmount());
        data.put("depositTotal", order.getDepositTotal());
        data.put("refundAmount", order.getRefundAmount());
        data.put("settleTime", order.getSettleTime());
        data.put("operator", order.getOperator());
        data.put("guestName", guest != null ? guest.getName() : "");
        data.put("roomNumber", room != null ? room.getRoomNumber() : "");
        data.put("roomType", room != null ? room.getRoomType() : "");
        return Result.success(data);
    }

    @GetMapping("/settle/list")
    public Result<IPage<SettleRecordDTO>> listSettledOrders(SettleListDTO dto) {
        return settleService.listSettledOrders(dto);
    }

    @GetMapping("/order/details/{orderId}")
    public Result<List<Map<String, Object>>> getOrderDetails(@PathVariable Integer orderId) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 1. 房费
        OrderMain order = orderMainMapper.selectById(orderId);
        if (order != null && order.getRoomFee() != null && order.getRoomFee().compareTo(BigDecimal.ZERO) > 0) {
            Map<String, Object> roomFeeItem = new HashMap<>();
            roomFeeItem.put("itemName", "房费");
            roomFeeItem.put("itemType", "房费");
            roomFeeItem.put("quantity", 1);
            roomFeeItem.put("price", order.getRoomFee());
            roomFeeItem.put("amount", order.getRoomFee());
            result.add(roomFeeItem);
        }

        // 2. 从 order_detail 查询所有明细（包括 KTV）
        List<OrderDetail> details = orderDetailMapper.selectList(
                new QueryWrapper<OrderDetail>().eq("order_id", orderId)
        );
        for (OrderDetail d : details) {
            Map<String, Object> item = new HashMap<>();
            item.put("itemName", d.getItemName());
            item.put("itemType", d.getItemType());
            item.put("quantity", d.getQuantity());
            item.put("price", d.getPrice());
            item.put("amount", d.getAmount());
            result.add(item);
        }

        // 3. 补充从 ktv_record 中未关联到 order_detail 的 KTV 记录（如有遗漏）
        List<KtvRecord> ktvRecords = ktvRecordMapper.selectList(
                new QueryWrapper<KtvRecord>().eq("order_id", orderId)
        );
        for (KtvRecord kr : ktvRecords) {
            // 检查是否已经存在，避免重复
            boolean exists = result.stream().anyMatch(m ->
                    "KTV".equals(m.get("itemType")) && m.get("itemName").toString().contains(kr.getDuration() + "小时")
            );
            if (!exists) {
                KtvRoom ktvRoom = ktvRoomMapper.selectById(kr.getKtvId());
                String roomName = ktvRoom != null ? ktvRoom.getRoomName() : "包房";
                Map<String, Object> ktvItem = new HashMap<>();
                ktvItem.put("itemName", roomName + " " + kr.getDuration() + "小时");
                ktvItem.put("itemType", "KTV");
                ktvItem.put("quantity", 1);
                ktvItem.put("price", kr.getTotalFee());
                ktvItem.put("amount", kr.getTotalFee());
                result.add(ktvItem);
            }
        }

        return Result.success(result);
    }
}