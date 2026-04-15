package org.example.entity;

import lombok.ToString;

import java.util.List;

@ToString
public class Student {
    Teacher teacher;
    String name;
    List<Integer> list;

    public void setName(String name) {
        this.name = name;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    // public Student(String name, Teacher teacher) {
    //     this.name = name;
    //     this.teacher = teacher;
    //     System.out.println("我被创建了");
    // }
    Student() {
        System.out.println("我被创建了");
    }

    public void Hello() {
        System.out.println("helloworld");
    }

    public void init(){
        System.out.println("初始化了要");
    }
    public void destroy() {
        System.out.println("销毁了要");
    }

}
