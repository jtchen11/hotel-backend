package com.hotel;

import com.hotel.entity.Stock;
import com.hotel.mapper.StockMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StockAtomicTest {

    @Autowired
    private StockMapper stockMapper;

    @BeforeEach
    public void setup() {
        Stock s = new Stock();
        s.setStockId(999);
        s.setItemName("测试物品");
        s.setCurrentQuantity(0);
        stockMapper.insert(s);
    }

    @Test
    public void testSubStock_atomicDeduction() {
        int affected = stockMapper.subStock(999, 3);
        assertEquals(0, affected, "不存在的库存ID应返回0");

        stockMapper.addStock(999, 5);
        affected = stockMapper.subStock(999, 3);
        assertEquals(1, affected, "库存充足时应扣减成功");

        affected = stockMapper.subStock(999, 3);
        assertEquals(0, affected, "库存不足时应返回0（不扣成负数）");
    }
}