package com.genshinya.spring6;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestUser {
    //创建logger对象
    private Logger logger = LoggerFactory.getLogger(TestUser.class);
    @Test
    public void testUserObject() {
        // 加载spring配置文件, 对象创建
        ApplicationContext context
                = new ClassPathXmlApplicationContext("bean.xml");
        // 获取创建对象
        User user = (User) context.getBean("user");
        System.out.println("1:" + user);
        //使用对象调用方法进行测试
        System.out.print("2:");
        user.add();

        // 手动写入日志
        logger.info("logger.info执行调用");
    }
}
