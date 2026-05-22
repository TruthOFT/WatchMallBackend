package com.watch.watch_mall.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserForgotPasswordRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userAccount;

    private String oldPassword;

    private String newPassword;

    private String checkPassword;
}
