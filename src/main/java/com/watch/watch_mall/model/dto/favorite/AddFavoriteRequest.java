package com.watch.watch_mall.model.dto.favorite;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddFavoriteRequest implements Serializable {

    private Long productId;

    private static final long serialVersionUID = 1L;
}
