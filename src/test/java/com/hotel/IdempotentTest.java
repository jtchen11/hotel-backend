package com.hotel;

import com.hotel.entity.IdempotentRecord;
import com.hotel.mapper.IdempotentRecordMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class IdempotentTest {

    @Autowired
    private IdempotentRecordMapper idempotentRecordMapper;

    @Test
    public void testIdempotentKey_duplicateShouldBeRejected() {
        IdempotentRecord r1 = new IdempotentRecord();
        r1.setIdempotentKey("checkin:101:A001:2026-07-20");
        r1.setStatus("SUCCESS");
        r1.setCreatedAt(LocalDateTime.now());
        idempotentRecordMapper.insert(r1);

        IdempotentRecord r2 = new IdempotentRecord();
        r2.setIdempotentKey("checkin:101:A001:2026-07-20");
        r2.setStatus("SUCCESS");
        r2.setCreatedAt(LocalDateTime.now());
        assertThrows(Exception.class, () -> idempotentRecordMapper.insert(r2),
                "相同幂等key应抛出唯一键冲突异常");

        long count = idempotentRecordMapper.selectCount(null);
        assertEquals(1, count, "重复key不应插入成功，表中应只有1条记录");
    }
}