package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVO implements Serializable {

    private Long id;

    private String orderNo;

    private Integer orderStatus;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private Date payTime;

    private Date closeTime;

    private Date createTime;

    private Date expireTime;

    private List<OrderItemVO> itemList;

    private static final long serialVersionUID = 1L;
}
