package com.hotel.controller.reception;

import com.hotel.common.Result;
import com.hotel.dto.GuestQueryDTO;
import com.hotel.entity.Guest;
import com.hotel.mapper.DepositMapper;
import com.hotel.mapper.GuestMapper;
import com.hotel.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guest")
public class GuestController {

    @Autowired
    private GuestService guestService;

    /**
     * 公共客人列表查询
     * @param status 在住/已离店
     * @param keyword 姓名/身份证/电话模糊匹配
     * @param roomId 房间ID
     * @param guestType 散客/团体
     * @param groupId 团体ID
     * @return
     */
    @GetMapping("/list")
    public Result<List<GuestQueryDTO>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) String guestType,
            @RequestParam(required = false) Integer groupId) {
        List<GuestQueryDTO> list = guestService.queryGuestList(status, keyword, roomId, guestType, groupId);
        return Result.success(list);
    }
    @GetMapping("/detail/{guestId}")
    public Result<Guest> getGuestDetail(@PathVariable Integer guestId) {
        Guest guest = guestMapper.selectById(guestId);
        if (guest == null) return Result.error("客人不存在");
        return Result.success(guest);
    }
    @Autowired
    private GuestMapper guestMapper;
}