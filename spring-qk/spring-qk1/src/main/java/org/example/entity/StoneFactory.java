package org.example.entity;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class StoneFactory implements FactoryBean<Stone> {

    @Override
    public Stone getObject() throws Exception {
        return new Stone();
    }

    @Override
    public Class<?> getObjectType() {
        return Stone.class;
    }

    @Override
    public boolean isSingleton() {
        // 生产的bean是否采用单例模式
        return false;
    }
}
