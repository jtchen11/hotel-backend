package com.hotel.controller.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.common.Result;
import com.hotel.dto.DepositDTO;
import com.hotel.dto.DepositListDTO;
import com.hotel.dto.DepositRecordDTO;
import com.hotel.entity.Deposit;
import com.hotel.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
public class DepositController {
    @Autowired private DepositService depositService;

    @GetMapping("/deposit/list")
    public Result<IPage<DepositRecordDTO>> listDeposits(DepositListDTO dto) {
        return depositService.listDepositRecords(dto);
    }

    @GetMapping("/deposit/history/{guestId}")
    public Result<List<Deposit>> getHistory(@PathVariable Integer guestId) {
        return depositService.getDepositHistory(guestId);
    }
    @PostMapping("/deposit")
    public Result<Void> addDeposit(@RequestBody DepositDTO dto) {
        return depositService.addDeposit(dto);
    }
}