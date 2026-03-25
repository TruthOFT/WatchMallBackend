package com.watch.watch_mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.watch.watch_mall.model.entity.ProductFavorite;
import com.watch.watch_mall.model.vo.FavoriteProductVO;

import java.util.List;

public interface FavoriteService extends IService<ProductFavorite> {

    boolean addFavorite(Long userId, Long productId);

    boolean removeFavorite(Long userId, Long productId);

    boolean hasFavorite(Long userId, Long productId);

    List<FavoriteProductVO> listMyFavorites(Long userId);
}
