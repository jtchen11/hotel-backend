package com.hotel.controller.reception;

import com.hotel.common.Result;
import com.hotel.dto.RoomStatusDTO;
import com.hotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "获取所有房间状态列表（带客人姓名）")
    @GetMapping("/statusList")
    public Result<List<RoomStatusDTO>> getRoomStatus() {
        return Result.success(roomService.getAllRoomsWithGuest());
    }

    @Operation(summary = "获取房间数量统计")
    @GetMapping("/count")
    public Result<?> getCount() {
        return Result.success(roomService.getRoomCount());
    }
}
