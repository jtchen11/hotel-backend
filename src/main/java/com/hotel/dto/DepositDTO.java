package com.hotel.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DepositDTO {
    private Integer guestId;
    private BigDecimal amount;      // 正数收取，负数退还
    private String type;            // 收取/追加/退还
    private String payMethod;       // 微信/支付宝/现金/银行卡
    private String remark;
}