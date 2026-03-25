package com.watch.watch_mall.model.dto.address;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAddressUpdateRequest implements Serializable {

    private Long id;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private String postalCode;

    private Integer isDefault;

    private static final long serialVersionUID = 1L;
}
