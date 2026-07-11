package com.hotel.controller.reception;

import com.hotel.common.Result;
import com.hotel.dto.GuestQueryDTO;
import com.hotel.entity.Guest;
import com.hotel.mapper.DepositMapper;
import com.hotel.mapper.GuestMapper;
import com.hotel.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guest")
public class GuestController {

    @Autowired
    private GuestService guestService;

    @Operation(summary = "公共客人列表查询，支持状态/关键词/房间/类型/团体筛选")
    @GetMapping("/list")
    public Result<List<GuestQueryDTO>> list(
            @Parameter(description = "状态：在住/已离店") @RequestParam(required = false) String status,
            @Parameter(description = "姓名/身份证/电话模糊匹配") @RequestParam(required = false) String keyword,
            @Parameter(description = "房间ID") @RequestParam(required = false) Integer roomId,
            @Parameter(description = "散客/团体") @RequestParam(required = false) String guestType,
            @Parameter(description = "团体ID") @RequestParam(required = false) Integer groupId) {
        List<GuestQueryDTO> list = guestService.queryGuestList(status, keyword, roomId, guestType, groupId);
        return Result.success(list);
    }

    @Operation(summary = "获取客人详情")
    @GetMapping("/detail/{guestId}")
    public Result<Guest> getGuestDetail(
            @Parameter(description = "客人ID") @PathVariable Integer guestId) {
        Guest guest = guestMapper.selectById(guestId);
        if (guest == null) return Result.error("客人不存在");
        return Result.success(guest);
    }

    @Autowired
    private GuestMapper guestMapper;
}
