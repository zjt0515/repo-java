package org.example;

import org.example.config.MainConfiguration;
import org.example.entity.SportsTeacher;
import org.example.entity.Student;
import org.example.entity.Teacher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("org.example.entity")
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
        Teacher teacher = context.getBean(SportsTeacher.class);
        Student student = context.getBean(Student.class);
        System.out.println(teacher);
        System.out.println(student);
    }
}
