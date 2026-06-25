package com.hotel.controller.reception;

import com.hotel.common.Result;
import com.hotel.dto.RoomStatusDTO;
import com.hotel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reception/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // 修改：返回带客人姓名的房态列表
    @GetMapping("/statusList")
    public Result<List<RoomStatusDTO>> getRoomStatus() {
        return Result.success(roomService.getAllRoomsWithGuest());
    }

    // 原有统计接口保留
    @GetMapping("/count")
    public Result<?> getCount() {
        return Result.success(roomService.getRoomCount());
    }
}