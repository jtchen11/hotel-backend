package com.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.dto.DepositDTO;
import com.hotel.common.Result;
import com.hotel.dto.DepositListDTO;
import com.hotel.dto.DepositRecordDTO;
import com.hotel.entity.Deposit;

import java.math.BigDecimal;
import java.util.List;

public interface DepositService {
    Result<Void> addDeposit(DepositDTO dto);
    BigDecimal getBalance(Integer guestId);
    Result<IPage<DepositRecordDTO>> listDepositRecords(DepositListDTO dto);
    Result<List<Deposit>> getDepositHistory(Integer guestId);
}