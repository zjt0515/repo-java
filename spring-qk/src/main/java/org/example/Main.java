package org.example;

import org.example.entity.Student;
import org.example.service.Service;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        // 创建IOC容器，根据xml路径

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
        System.out.println("IOC容器创建完毕");
        // 从IOC容器中获取对象
        Student student1 = context.getBean(Student.class);
        Student student2 = (Student) context.getBean("student");
        student2.Hello();
        System.out.println(student1);
        // 获取service接口的实现类
        Service service = context.getBean(Service.class);
        System.out.println(service);
        System.out.println(student1 == student2);

        context.close();
    }
}
