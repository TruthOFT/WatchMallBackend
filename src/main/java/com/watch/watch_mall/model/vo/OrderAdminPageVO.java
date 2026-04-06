package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderAdminPageVO implements Serializable {

    private Long id;

    private String orderNo;

    private Long userId;

    private Integer orderStatus;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private String receiverName;

    private String receiverPhone;

    private String productSummary;

    private Integer itemCount;

    private Date payTime;

    private Date closeTime;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}
