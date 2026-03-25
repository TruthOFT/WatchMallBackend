package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.mapper.ProductFavoriteMapper;
import com.watch.watch_mall.mapper.ProductMapper;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.model.entity.ProductFavorite;
import com.watch.watch_mall.model.vo.FavoriteProductVO;
import com.watch.watch_mall.model.vo.ProductVO;
import com.watch.watch_mall.service.FavoriteService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class FavoriteServiceImpl extends ServiceImpl<ProductFavoriteMapper, ProductFavorite> implements FavoriteService {

    @Resource
    private ProductFavoriteMapper productFavoriteMapper;

    @Resource
    private ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFavorite(Long userId, Long productId) {
        validateUserId(userId);
        validateProduct(productId);

        ProductFavorite existing = productFavoriteMapper.selectAnyFavoriteByUserIdAndProductId(userId, productId);
        if (existing != null) {
            if (Objects.equals(existing.getIsDelete(), 0)) {
                return true;
            }
            existing.setIsDelete(0);
            existing.setCreateTime(new Date());
            boolean restored = productFavoriteMapper.updateById(existing) > 0;
            ThrowUtils.throwIf(!restored, ErrorCode.OPERATION_ERROR, "favorite restore failed");
            return true;
        }

        ProductFavorite favorite = new ProductFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setIsDelete(0);
        try {
            boolean saved = productFavoriteMapper.insert(favorite) > 0;
            ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR, "favorite add failed");
            return true;
        } catch (DuplicateKeyException duplicateKeyException) {
            ProductFavorite duplicated = productFavoriteMapper.selectAnyFavoriteByUserIdAndProductId(userId, productId);
            ThrowUtils.throwIf(duplicated == null, ErrorCode.OPERATION_ERROR, "favorite add failed");
            if (Objects.equals(duplicated.getIsDelete(), 1)) {
                duplicated.setIsDelete(0);
                duplicated.setCreateTime(new Date());
                boolean restored = productFavoriteMapper.updateById(duplicated) > 0;
                ThrowUtils.throwIf(!restored, ErrorCode.OPERATION_ERROR, "favorite restore failed");
            }
            return true;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFavorite(Long userId, Long productId) {
        validateUserId(userId);
        ThrowUtils.throwIf(productId == null || productId <= 0, ErrorCode.PARAMS_ERROR);

        ProductFavorite favorite = productFavoriteMapper.selectOne(Wrappers.lambdaQuery(ProductFavorite.class)
                .eq(ProductFavorite::getUserId, userId)
                .eq(ProductFavorite::getProductId, productId)
                .eq(ProductFavorite::getIsDelete, 0)
                .last("limit 1"));
        if (favorite == null) {
            return true;
        }
        favorite.setIsDelete(1);
        boolean updated = productFavoriteMapper.updateById(favorite) > 0;
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "favorite remove failed");
        return true;
    }

    @Override
    public boolean hasFavorite(Long userId, Long productId) {
        if (userId == null || userId <= 0 || productId == null || productId <= 0) {
            return false;
        }
        return productFavoriteMapper.selectCount(Wrappers.lambdaQuery(ProductFavorite.class)
                .eq(ProductFavorite::getUserId, userId)
                .eq(ProductFavorite::getProductId, productId)
                .eq(ProductFavorite::getIsDelete, 0)) > 0;
    }

    @Override
    public List<FavoriteProductVO> listMyFavorites(Long userId) {
        validateUserId(userId);

        List<ProductFavorite> favorites = productFavoriteMapper.selectList(Wrappers.lambdaQuery(ProductFavorite.class)
                .eq(ProductFavorite::getUserId, userId)
                .eq(ProductFavorite::getIsDelete, 0)
                .orderByDesc(ProductFavorite::getCreateTime)
                .orderByDesc(ProductFavorite::getId));
        if (favorites == null || favorites.isEmpty()) {
            return List.of();
        }

        List<FavoriteProductVO> result = new ArrayList<>();
        for (ProductFavorite favorite : favorites) {
            if (favorite.getProductId() == null) {
                continue;
            }
            ProductVO productVO = productMapper.getProductBaseById(favorite.getProductId());
            if (productVO == null) {
                continue;
            }
            FavoriteProductVO item = new FavoriteProductVO();
            BeanUtils.copyProperties(productVO, item);
            item.setProductId(favorite.getProductId());
            item.setFavoriteTime(favorite.getCreateTime());
            result.add(item);
        }
        return result;
    }

    private void validateUserId(Long userId) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.NOT_LOGIN_ERROR);
    }

    private void validateProduct(Long productId) {
        ThrowUtils.throwIf(productId == null || productId <= 0, ErrorCode.PARAMS_ERROR);
        Product product = productMapper.selectById(productId);
        ThrowUtils.throwIf(product == null || defaultInt(product.getIsDelete()) != 0 || defaultInt(product.getStatus()) != 1,
                ErrorCode.NOT_FOUND_ERROR, "product not found");
    }

    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }
}
