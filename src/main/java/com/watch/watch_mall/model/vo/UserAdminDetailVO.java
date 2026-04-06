package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserAdminDetailVO implements Serializable {

    private Long id;

    private String username;

    private String userAccount;

    private String email;

    private String phone;

    private String avatarUrl;

    private Integer gender;

    private BigDecimal balance;

    private String userRole;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
