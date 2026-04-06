package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserAdminPageVO implements Serializable {

    private Long id;

    private String username;

    private String userAccount;

    private String email;

    private String phone;

    private String avatarUrl;

    private String userRole;

    private BigDecimal balance;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
