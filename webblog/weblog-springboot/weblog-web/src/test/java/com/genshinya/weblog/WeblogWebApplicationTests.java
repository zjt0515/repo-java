package com.genshinya.weblog;

import com.genshinya.weblog.common.domain.dos.UserDO;
import com.genshinya.weblog.common.domain.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Slf4j // 自动生成日志实例
class WeblogWebApplicationTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void testLog(){
        log.info("Info 级别日志");
        log.warn("Warn 级别日志");
        log.error("Error 级别日志");

        String author = "genshinya";
        log.info("作者：{}", author);
    }


    @Test
    void userMapperTest() {
        // 构建数据库实体类
        UserDO userDO = UserDO.builder()
                .username("testp6spy")
                .password("123456")
                .createTime(new Date())
                .updateTime(new Date())
                .isDeleted(false)
                .build();
        userMapper.insert(userDO);
    }

}
