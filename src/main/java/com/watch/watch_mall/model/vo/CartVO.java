package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVO implements Serializable {

    private Long cartId;

    private List<CartItemVO> itemList;

    private BigDecimal checkedAmount;

    private Integer checkedCount;

    private Integer totalCount;

    private static final long serialVersionUID = 1L;
}
