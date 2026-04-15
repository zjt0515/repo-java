package org.example.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestUser {
    public static void main(String[] args) {
        ApplicationContext context = new
                ClassPathXmlApplicationContext("bean.xml");
        //1.获取id获取bean对象
        User user1 = (User)context.getBean("user");
        System.out.println("id获取bean对象：" + user1);
        //2.根据类型获取bean
        User user2 = context.getBean(User.class);
        System.out.println("类型获取bean对象：" + user2);
        //3.根据id和类型
        User user3 = context.getBean("user", User.class);
        System.out.println("第三种：" + user3);

    }
}
