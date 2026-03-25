package com.watch.watch_mall.model.dto.product;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductViewTrackRequest implements Serializable {

    private Long productId;

    private String viewSource;

    private String deviceType;

    private static final long serialVersionUID = 1L;
}
