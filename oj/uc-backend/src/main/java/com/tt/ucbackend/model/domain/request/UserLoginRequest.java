package com.tt.ucbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 5435233989292815834L;

    private String userAccount;

    private String userPassword;

}
