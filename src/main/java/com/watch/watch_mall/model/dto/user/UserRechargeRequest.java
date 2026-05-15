package com.watch.watch_mall.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UserRechargeRequest implements Serializable {

    private BigDecimal amount;

    private static final long serialVersionUID = 1L;
}
