package com.watch.watch_mall.service;

import com.watch.watch_mall.model.dto.product.ProductViewTrackRequest;
import com.watch.watch_mall.model.vo.ProductVO;

import java.util.List;

public interface RecommendationService {

    boolean trackProductView(Long userId, ProductViewTrackRequest request);

    void refreshPreferenceData();

    void refreshProductSimilarity();

    void refreshUserRecommendation();

    void refreshAll();

    List<ProductVO> listHomeRecommendations(Long userId, int size);

    List<ProductVO> listRelatedProducts(Long userId, Long productId, int size);
}
