package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class FavoriteProductVO implements Serializable {

    private Long productId;

    private String name;

    private String title;

    private BigDecimal price;

    private String url;

    private Date favoriteTime;

    private static final long serialVersionUID = 1L;
}
