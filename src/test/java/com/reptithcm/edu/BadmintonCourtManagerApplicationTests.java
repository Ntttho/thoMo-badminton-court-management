package com.reptithcm.edu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.reptithcm.edu.service.redis.RedisService;

@SpringBootTest
class BadmintonCourtManagerApplicationTests {

    @MockitoBean
    private RedisService redisService;

    @Test
    void contextLoads() {
    }

}
