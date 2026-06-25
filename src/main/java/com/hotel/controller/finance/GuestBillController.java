package com.hotel.controller.finance;

import com.hotel.common.Result;
import com.hotel.common.UserContext;
import com.hotel.dto.GuestBillDTO;
import com.hotel.entity.OrderDetail;
import com.hotel.mapper.OrderDetailMapper;
import com.hotel.mapper.OrderMainMapper;
import com.hotel.service.GuestBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
public class GuestBillController {
    @Autowired private GuestBillService guestBillService;
    @Autowired private OrderDetailMapper orderDetailMapper;

    @GetMapping("/bill/{guestId}")
    public Result<GuestBillDTO> getGuestBill(@PathVariable Integer guestId) {
        return guestBillService.getGuestBill(guestId);
    }
    @PostMapping("/charge")
    public Result<Void> addCharge(@RequestBody Map<String, Object> params) {
        Integer guestId = (Integer) params.get("guestId");
        String itemName = (String) params.get("itemName");
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        String remark = (String) params.get("remark");

        OrderDetail detail = new OrderDetail();
        detail.setGuestId(guestId);
        detail.setItemType("杂项");
        detail.setItemName(itemName);
        detail.setQuantity(1);
        detail.setPrice(amount);
        detail.setAmount(amount);
        detail.setIsSettled(0);
        detail.setOperator(UserContext.getEmpName());
        detail.setCreateTime(LocalDateTime.now());
        detail.setRemark(remark);
        orderDetailMapper.insert(detail);
        return Result.success();
    }
}