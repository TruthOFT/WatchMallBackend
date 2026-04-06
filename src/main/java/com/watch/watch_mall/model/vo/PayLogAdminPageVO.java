package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayLogAdminPageVO implements Serializable {

    private Long id;

    private Long orderId;

    private String orderNo;

    private Long userId;

    private String payNo;

    private Integer payType;

    private Integer payStatus;

    private BigDecimal payAmount;

    private Date payTime;

    private String remark;

    private static final long serialVersionUID = 1L;
}
