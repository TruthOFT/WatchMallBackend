package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderAdminDetailVO implements Serializable {

    private Long id;

    private Long userId;

    private String orderNo;

    private Integer orderStatus;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private Long addressId;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private String remark;

    private Date payTime;

    private Date closeTime;

    private Date createTime;

    private Date expireTime;

    private List<OrderItemVO> itemList;

    private List<PayLogAdminPageVO> payLogList;

    private static final long serialVersionUID = 1L;
}
