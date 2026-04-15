package org.example.entity;

import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

@ToString
public class Student {
    @Autowired
    Teacher teacher;
    public Student() {

        System.out.println("Student构造方法");

    }

    // public void setTeacher(Teacher teacher) {
    //     this.teacher = teacher;
    // }
}
