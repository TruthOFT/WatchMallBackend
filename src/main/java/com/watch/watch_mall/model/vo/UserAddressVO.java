package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserAddressVO implements Serializable {

    private Long id;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private String postalCode;

    private Integer isDefault;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
