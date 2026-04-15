package com.genshinya.usercenter.model.domain.login;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    // 防止序列化过程中的冲突
    private static final long serialVersionUID = 3004487075121603219L;
    private String userAccount;
    private String userPassword;
}
