package com.hotel.controller.operation;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.common.Result;
import com.hotel.common.UserContext;
import com.hotel.dto.OrderItemDTO;
import com.hotel.entity.Guest;
import com.hotel.entity.Menu;
import com.hotel.entity.OrderDetail;
import com.hotel.entity.OrderMain;
import com.hotel.mapper.OrderMainMapper;
import com.hotel.service.GuestService;
import com.hotel.service.MenuService;
import com.hotel.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dining")
public class DiningController {

    @Autowired
    private GuestService guestService;

    @Autowired
    private MenuService menuService;
    @Autowired
    private OrderMainMapper orderMainMapper;
    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/menu")
    public Result listMenu(@RequestParam(required = false) String category) {
        QueryWrapper<Menu> qw = new QueryWrapper<>();
        if (StrUtil.isNotBlank(category)) {
            qw.eq("category", category);
        }
        List<Menu> list = menuService.list(qw);
        for (Menu menu : list) {
            if (StrUtil.isNotBlank(menu.getImage())) {
                menu.setImage("http://localhost:8080" + menu.getImage());
            }
        }
        return Result.success(list);
    }

    @PostMapping("/order")
    @Transactional
    public Result orderDish(@RequestBody List<OrderItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return Result.error("点菜列表不能为空");
        }
        Integer guestId = items.get(0).getGuestId();
        Guest guest = guestService.getById(guestId);
        if (guest == null || !"在住".equals(guest.getStatus())) {
            return Result.error("该客人当前不在住，无法挂账点菜");
        }

        // 1. 创建订单主表
        OrderMain orderMain = new OrderMain();
        orderMain.setGuestId(guestId);
        orderMain.setRoomId(guest.getRoomId());
        orderMain.setStatus("未结算");
        orderMain.setCreateTime(LocalDateTime.now());
        orderMainMapper.insert(orderMain);  // 获取自增 orderId

        // 2. 插入明细，关联订单ID
        for (OrderItemDTO item : items) {
            // 查询菜品信息（包括库存）
            Menu menu = menuService.getOne(new QueryWrapper<Menu>().eq("name", item.getItemName()));
            if (menu == null) {
                return Result.error("菜品 " + item.getItemName() + " 不存在");
            }
            // 检查库存
            if (menu.getStockQuantity() < item.getQuantity()) {
                return Result.error("菜品 " + item.getItemName() + " 库存不足，当前仅剩 " + menu.getStockQuantity());
            }
            // 扣减库存
            menu.setStockQuantity(menu.getStockQuantity() - item.getQuantity());
            if (menu.getStockQuantity() <= 0) {
                menu.setStatus("售罄");
            } else {
                menu.setStatus("在售");
            }
            menuService.updateById(menu);

            BigDecimal amount = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            OrderDetail detail = new OrderDetail();
            detail.setGuestId(item.getGuestId());
            detail.setOrderId(orderMain.getOrderId());   // 关键：关联订单
            detail.setItemType("餐饮");
            detail.setItemName(item.getItemName());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            detail.setAmount(amount);
            detail.setIsSettled(0);
            detail.setOperator(UserContext.getEmpName());
            detail.setCreateTime(LocalDateTime.now());
            orderDetailService.save(detail);
        }
        return Result.success("点菜成功");
    }

    @GetMapping("/unsettled")
    public Result<List<OrderDetail>> getUnsettled(@RequestParam Integer guestId) {
        QueryWrapper<OrderDetail> qw = new QueryWrapper<>();
        qw.eq("guest_id", guestId).eq("is_settled", 0);
        List<OrderDetail> list = orderDetailService.list(qw);
        Guest guest = guestService.getById(guestId);
        if (guest != null) {
            for (OrderDetail detail : list) {
                detail.setGuestName(guest.getName());
            }
        }
        return Result.success(list);
    }

    @GetMapping("/allUnsettled")
    public Result<List<OrderDetail>> getAllUnsettled(@RequestParam(required = false) Integer days) {
        int range = days == null ? 30 : days;
        LocalDateTime startTime = LocalDateTime.now().minusDays(range);
        QueryWrapper<OrderDetail> qw = new QueryWrapper<>();
        qw.eq("is_settled", 0)
                .eq("item_type", "餐饮")
                .ge("create_time", startTime)
                .orderByDesc("create_time");
        List<OrderDetail> list = orderDetailService.list(qw);
        for (OrderDetail detail : list) {
            Guest guest = guestService.getById(detail.getGuestId());
            if (guest != null) detail.setGuestName(guest.getName());
        }
        return Result.success(list);
    }
    @GetMapping("/settled")
    public Result<List<OrderDetail>> getSettled(@RequestParam Integer guestId) {
        QueryWrapper<OrderDetail> qw = new QueryWrapper<>();
        qw.eq("guest_id", guestId)
                .eq("is_settled", 1)
                .eq("item_type", "餐饮")
                .orderByDesc("create_time");
        List<OrderDetail> list = orderDetailService.list(qw);
        return Result.success(list);
    }
}