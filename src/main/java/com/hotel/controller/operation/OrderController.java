package com.hotel.controller.operation;

import com.hotel.common.Result;
import com.hotel.dto.OrderDTO;
import com.hotel.service.OrderMainService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Resource
    private OrderMainService orderMainService;

    @PostMapping("/create")
    public Result create(@RequestBody OrderDTO dto) {
        return orderMainService.createOrder(dto);
    }
}