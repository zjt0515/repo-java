package org.example.entity;

import org.springframework.beans.factory.FactoryBean;

public class StoneFactory implements FactoryBean<Stone> {
    public Stone getStone(){
        return new Stone();
    }
    @Override
    public Stone getObject() throws Exception {
        return getStone();
    }

    @Override
    public Class<?> getObjectType() {
        return Stone.class;
    }
}
