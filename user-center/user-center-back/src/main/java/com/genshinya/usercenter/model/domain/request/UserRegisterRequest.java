package com.genshinya.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    // 防止序列化过程中的冲突
    private static final long serialVersionUID = 3004487075121603219L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
