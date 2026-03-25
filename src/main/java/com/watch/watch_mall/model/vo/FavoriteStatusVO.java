package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FavoriteStatusVO implements Serializable {

    private Boolean hasFavorite;

    private static final long serialVersionUID = 1L;
}
