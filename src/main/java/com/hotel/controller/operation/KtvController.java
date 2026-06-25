package com.hotel.controller.operation;

import com.hotel.common.Result;
import com.hotel.common.UserContext;
import com.hotel.dto.KtvEndDTO;
import com.hotel.dto.KtvStartDTO;
import com.hotel.entity.Guest;
import com.hotel.entity.KtvRecord;
import com.hotel.entity.KtvRoom;
import com.hotel.service.KtvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ktv")
public class KtvController {
    @Autowired
    private KtvService ktvService;

    @GetMapping("/rooms")
    public Result<List<KtvRoom>> listRooms() {
        return Result.success(ktvService.listRooms());
    }

    @PostMapping("/start")
    public Result<Void> startKtv(@RequestBody KtvStartDTO dto) {
        String operator = UserContext.getEmpName();
        ktvService.startKtv(dto.getKtvId(), dto.getGuestId(), operator);
        return Result.success();
    }

    @PostMapping("/end")
    public Result<Void> endKtv(@RequestBody Map<String, Object> params) {
        Integer guestId = (Integer) params.get("guestId");
        BigDecimal customDuration = null;
        if (params.containsKey("customDuration") && params.get("customDuration") != null) {
            customDuration = new BigDecimal(params.get("customDuration").toString());
        }
        String operator = UserContext.getEmpName();
        ktvService.endKtv(guestId, operator, customDuration);
        return Result.success();
    }

    @GetMapping("/prepare")
    public Result<Map<String, Object>> prepareEnd(@RequestParam Integer guestId) {
        Map<String, Object> data = ktvService.prepareEndKtv(guestId);
        return Result.success(data);
    }

    @PutMapping("/room/{ktvId}/status")
    public Result<Void> changeRoomStatus(@PathVariable Integer ktvId, @RequestParam String status) {
        ktvService.changeStatus(ktvId, status);
        return Result.success();
    }

    @GetMapping("/records")
    public Result<List<KtvRecord>> getKtvRecords(@RequestParam Integer guestId) {
        return Result.success(ktvService.getKtvRecordsByGuest(guestId));
    }

    @GetMapping("/allRecords")
    public Result<List<KtvRecord>> getAllKtvRecords(@RequestParam(required = false) Integer days) {
        return Result.success(ktvService.getAllKtvRecords(days));
    }

    @GetMapping("/getCurrentGuest/{ktvId}")
    public Result<Map<String,Object>> getCurrentGuest(@PathVariable Integer ktvId){
        Guest guest = ktvService.getRoomCurrentGuest(ktvId);
        Map<String,Object> map = new HashMap<>();
        map.put("guestId",guest.getGuestId());
        map.put("name",guest.getName());
        return Result.success(map);
    }
}