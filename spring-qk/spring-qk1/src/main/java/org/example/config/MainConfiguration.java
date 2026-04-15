package org.example.config;

import org.example.entity.SportsTeacher;
import org.example.entity.Stone;
import org.example.entity.Student;
import org.example.entity.Teacher;
import org.springframework.context.annotation.*;

@Configuration
public class MainConfiguration {
    @Bean
    public Teacher teacher(){
        return new SportsTeacher();
    }
    @Bean("student")
    public Student student(Teacher teacher){
        Student student = new Student();
        // student.setTeacher(teacher);
        return student;
    }
    @Bean(name = "stone", initMethod = "init")
    @Scope("prototype")
    public Stone stone(){
        return new Stone();
    }
}
