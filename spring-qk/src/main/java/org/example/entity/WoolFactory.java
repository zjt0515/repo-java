package org.example.entity;

public class WoolFactory {
    public Wool getWool(){
        System.out.println("工厂方法创建student对象");
        return new Wool();
    }
}
