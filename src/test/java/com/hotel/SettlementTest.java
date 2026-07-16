package com.hotel;

import com.hotel.entity.OrderDetail;
import com.hotel.entity.OrderMain;
import com.hotel.mapper.OrderDetailMapper;
import com.hotel.mapper.OrderMainMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SettlementTest {

    @Autowired
    private OrderMainMapper orderMainMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Test
    public void testSettlement_totalShouldMatch() {
        OrderMain order = new OrderMain();
        order.setGuestId(1); order.setRoomId(101);
        order.setStatus("未结算");
        order.setCreateTime(LocalDateTime.now());
        orderMainMapper.insert(order);

        OrderDetail d1 = new OrderDetail();
        d1.setGuestId(1); d1.setOrderId(order.getOrderId());
        d1.setItemType("餐饮"); d1.setItemName("宫保鸡丁");
        d1.setQuantity(2); d1.setPrice(new BigDecimal("38"));
        d1.setAmount(new BigDecimal("76")); d1.setIsSettled(0);
        d1.setCreateTime(LocalDateTime.now());
        orderDetailMapper.insert(d1);

        OrderDetail d2 = new OrderDetail();
        d2.setGuestId(1); d2.setOrderId(order.getOrderId());
        d2.setItemType("杂项"); d2.setItemName("矿泉水");
        d2.setQuantity(3); d2.setPrice(new BigDecimal("5"));
        d2.setAmount(new BigDecimal("15")); d2.setIsSettled(0);
        d2.setCreateTime(LocalDateTime.now());
        orderDetailMapper.insert(d2);

        BigDecimal total = orderDetailMapper.selectList(null).stream()
                .map(OrderDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, total.compareTo(new BigDecimal("91")), "76+15=91");
    }
}
