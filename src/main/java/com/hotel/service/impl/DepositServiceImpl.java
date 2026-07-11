package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.common.UserContext;
import com.hotel.dto.DepositDTO;
import com.hotel.dto.DepositListDTO;
import com.hotel.dto.DepositRecordDTO;
import com.hotel.entity.Deposit;
import com.hotel.mapper.DepositMapper;
import com.hotel.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepositServiceImpl implements DepositService {

    @Autowired
    private DepositMapper depositMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addDeposit(DepositDTO dto) {
        // 1. 参数基础校验
        if (dto.getGuestId() == null) return Result.error("客人ID不能为空");
        if (dto.getAmount() == null) return Result.error("金额不能为空");

        // 2. 获取当前押金余额
        BigDecimal balance = getBalance(dto.getGuestId());

        // 3. 根据类型校验
        if ("收取".equals(dto.getType()) || "追加".equals(dto.getType())) {
            if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Result.error("收取/追加金额必须大于0");
            }
        } else if ("退还".equals(dto.getType())) {
            if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Result.error("退还金额必须为正数");
            }
            if (dto.getAmount().compareTo(balance) > 0) {
                return Result.error("退还金额不能大于押金余额（当前余额：" + balance + "）");
            }
            // 将退还金额转为负数存储
            dto.setAmount(dto.getAmount().negate());
        } else {
            return Result.error("无效的操作类型");
        }

        // 4. 保存记录
        Deposit deposit = new Deposit();
        deposit.setGuestId(dto.getGuestId());
        deposit.setAmount(dto.getAmount());
        deposit.setType(dto.getType());
        deposit.setPayMethod(dto.getPayMethod());
        deposit.setRemark(dto.getRemark());
        deposit.setCreateTime(LocalDateTime.now());
        deposit.setOperator(UserContext.getEmpName());
        depositMapper.insert(deposit);
        return Result.success();
    }

    @Override
    public BigDecimal getBalance(Integer guestId) {
        return depositMapper.selectBalanceByGuestId(guestId);
    }
    @Override
    public Result<IPage<DepositRecordDTO>> listDepositRecords(DepositListDTO dto) {
        Page<DepositRecordDTO> page = new Page<>(dto.getPage(), dto.getSize());
        IPage<DepositRecordDTO> result = depositMapper.selectDepositRecords(
                page, dto.getStartDate(), dto.getEndDate(), dto.getKeyword(), dto.getStatus());
        return Result.success(result);
    }

    @Override
    public Result<List<Deposit>> getDepositHistory(Integer guestId) {
        List<Deposit> list = depositMapper.selectHistoryByGuestId(guestId);
        return Result.success(list);
    }
}