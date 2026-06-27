package com.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.common.Result;
import com.hotel.dto.SettleListDTO;
import com.hotel.dto.SettlePreviewDTO;
import com.hotel.dto.SettleRecordDTO;

import java.math.BigDecimal;
import java.util.Map;

public interface SettleService {
    // 无折扣（默认折扣为1）
    Result<SettlePreviewDTO> preview(Integer guestId);
    Result<Map<String, Object>> settle(Integer guestId);

    // 带折扣（折扣率 0.5 ~ 1，1表示无折扣）+支付方式
    Result<SettlePreviewDTO> preview(Integer guestId, BigDecimal discountRate);
    Result<Map<String, Object>> settle(Integer guestId, BigDecimal discountRate);
    Result<Map<String, Object>> settle(Integer guestId, BigDecimal discountRate, String payMethod);

    Result<IPage<SettleRecordDTO>> listSettledOrders(SettleListDTO dto);
}