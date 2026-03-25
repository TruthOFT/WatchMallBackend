package com.watch.watch_mall.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.watch.watch_mall.mapper.CartItemMapper;
import com.watch.watch_mall.mapper.ProductFavoriteMapper;
import com.watch.watch_mall.mapper.ProductMapper;
import com.watch.watch_mall.mapper.ProductReviewMapper;
import com.watch.watch_mall.mapper.ProductSimilarityMapper;
import com.watch.watch_mall.mapper.ProductViewLogMapper;
import com.watch.watch_mall.mapper.UserProductPreferenceMapper;
import com.watch.watch_mall.mapper.UserRecommendationMapper;
import com.watch.watch_mall.model.dto.product.ProductViewTrackRequest;
import com.watch.watch_mall.model.entity.CartItem;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.model.entity.ProductFavorite;
import com.watch.watch_mall.model.entity.ProductReview;
import com.watch.watch_mall.model.entity.ProductSimilarity;
import com.watch.watch_mall.model.entity.ProductViewLog;
import com.watch.watch_mall.model.entity.UserProductPreference;
import com.watch.watch_mall.model.entity.UserRecommendation;
import com.watch.watch_mall.model.inner_data.FeatureItem;
import com.watch.watch_mall.model.vo.ProductVO;
import com.watch.watch_mall.service.RecommendationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private static final int HOME_RECOMMEND_SIZE = 3;
    private static final int RELATED_RECOMMEND_SIZE = 5;
    private static final int PRODUCT_SIMILARITY_TOP_N = 20;
    private static final int USER_RECOMMEND_TOP_N = 12;
    private static final int FAVORITE_WEIGHT = 8;
    private static final int CART_WEIGHT = 10;
    private static final double USER_PREFERENCE_FACTOR = 0.10D;

    @Resource
    private ProductViewLogMapper productViewLogMapper;

    @Resource
    private ProductFavoriteMapper productFavoriteMapper;

    @Resource
    private CartItemMapper cartItemMapper;

    @Resource
    private ProductReviewMapper productReviewMapper;

    @Resource
    private UserProductPreferenceMapper userProductPreferenceMapper;

    @Resource
    private ProductSimilarityMapper productSimilarityMapper;

    @Resource
    private UserRecommendationMapper userRecommendationMapper;

    @Resource
    private ProductMapper productMapper;

    @Override
    public boolean trackProductView(Long userId, ProductViewTrackRequest request) {
        if (userId == null || request == null || request.getProductId() == null || request.getProductId() <= 0) {
            return true;
        }
        Product product = productMapper.selectById(request.getProductId());
        if (product == null || defaultInt(product.getIsDelete()) != 0 || defaultInt(product.getStatus()) != 1) {
            return true;
        }
        ProductViewLog log = new ProductViewLog();
        log.setUserId(userId);
        log.setProductId(request.getProductId());
        log.setViewSource(StringUtils.defaultIfBlank(request.getViewSource(), "detail"));
        log.setDeviceType(StringUtils.defaultIfBlank(request.getDeviceType(), "pc"));
        log.setViewDuration(0);
        log.setIsDelete(0);
        return productViewLogMapper.insert(log) > 0;
    }

    /**
     * 刷新用户偏好数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshPreferenceData() {
        markAllPreferenceDeleted();

        Map<String, PreferenceAccumulator> scoreMap = new LinkedHashMap<>();
        // 浏览记录
        List<ProductViewLog> viewLogs = productViewLogMapper.selectList(Wrappers.lambdaQuery(ProductViewLog.class)
                .eq(ProductViewLog::getIsDelete, 0));
        for (ProductViewLog viewLog : viewLogs) {
            if (viewLog.getUserId() == null || viewLog.getProductId() == null) {
                continue;
            }
            String key = buildUserProductKey(viewLog.getUserId(), viewLog.getProductId());
            PreferenceAccumulator accumulator = scoreMap.computeIfAbsent(key, item -> new PreferenceAccumulator());
            accumulator.viewCount++;
            accumulator.lastBehaviorTime = maxDate(accumulator.lastBehaviorTime, viewLog.getCreateTime());
        }
        // 收藏记录
        List<ProductFavorite> favorites = productFavoriteMapper.selectList(Wrappers.lambdaQuery(ProductFavorite.class)
                .eq(ProductFavorite::getIsDelete, 0));
        for (ProductFavorite favorite : favorites) {
            if (favorite.getUserId() == null || favorite.getProductId() == null) {
                continue;
            }
            String key = buildUserProductKey(favorite.getUserId(), favorite.getProductId());
            PreferenceAccumulator accumulator = scoreMap.computeIfAbsent(key, item -> new PreferenceAccumulator());
            accumulator.favoriteScore = FAVORITE_WEIGHT;
            accumulator.lastBehaviorTime = maxDate(accumulator.lastBehaviorTime, favorite.getCreateTime());
        }
        // 购物车记录
        List<CartItem> cartItems = cartItemMapper.selectList(Wrappers.lambdaQuery(CartItem.class)
                .eq(CartItem::getIsDelete, 0));
        for (CartItem cartItem : cartItems) {
            if (cartItem.getUserId() == null || cartItem.getProductId() == null) {
                continue;
            }
            String key = buildUserProductKey(cartItem.getUserId(), cartItem.getProductId());
            PreferenceAccumulator accumulator = scoreMap.computeIfAbsent(key, item -> new PreferenceAccumulator());
            accumulator.cartScore = CART_WEIGHT;
            accumulator.lastBehaviorTime = maxDate(accumulator.lastBehaviorTime, cartItem.getCreateTime());
        }

        List<ProductReview> reviews = productReviewMapper.selectList(Wrappers.lambdaQuery(ProductReview.class)
                .eq(ProductReview::getIsDelete, 0)
                .eq(ProductReview::getStatus, 1));
        for (ProductReview review : reviews) {
            if (review.getUserId() == null || review.getProductId() == null) {
                continue;
            }
            String key = buildUserProductKey(review.getUserId(), review.getProductId());
            PreferenceAccumulator accumulator = scoreMap.computeIfAbsent(key, item -> new PreferenceAccumulator());
            int reviewScore = Math.max(defaultInt(review.getScore()), 0) * 2;
            accumulator.reviewScore = Math.max(accumulator.reviewScore, reviewScore);
            accumulator.lastBehaviorTime = maxDate(accumulator.lastBehaviorTime, review.getCreateTime());
        }
        // 遍历偏好数据映射，构建用户-商品偏好数据，计算偏好分数，插入数据库
        for (Map.Entry<String, PreferenceAccumulator> entry : scoreMap.entrySet()) {
            String[] ids = entry.getKey().split("_");
            Long userId = Long.parseLong(ids[0]);
            Long productId = Long.parseLong(ids[1]);
            PreferenceAccumulator accumulator = entry.getValue();
            int viewScore = Math.min(accumulator.viewCount, 3);
            int preferenceScore = viewScore + accumulator.favoriteScore + accumulator.cartScore + accumulator.reviewScore;
            if (preferenceScore <= 0) {
                continue;
            }

            UserProductPreference entity = new UserProductPreference();
            entity.setUserId(userId);
            entity.setProductId(productId);
            entity.setViewCount(accumulator.viewCount);
            entity.setFavoriteScore(BigDecimal.valueOf(accumulator.favoriteScore));
            entity.setCartScore(BigDecimal.valueOf(accumulator.cartScore));
            entity.setReviewScore(BigDecimal.valueOf(accumulator.reviewScore));
            entity.setPreferenceScore(BigDecimal.valueOf(preferenceScore));
            entity.setLastBehaviorTime(accumulator.lastBehaviorTime);
            entity.setIsDelete(0);
            userProductPreferenceMapper.insert(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshProductSimilarity() {
        refreshProductSimilarity(buildVersion());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshUserRecommendation() {
        refreshUserRecommendation(buildVersion(), nextRefreshTime());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshAll() {
        String version = buildVersion();
        Date expireTime = nextRefreshTime();
        log.info("start rebuild recommendation cache, version={}", version);
        refreshPreferenceData();
        long preferenceCount = countPreferenceRows();
        log.info("finish preference rebuild, version={}, preferenceCount={}", version, preferenceCount);
        refreshProductSimilarity(version);
        long similarityCount = countSimilarityRows(version);
        log.info("finish similarity rebuild, version={}, similarityCount={}", version, similarityCount);
        refreshUserRecommendation(version, expireTime);
        long recommendationCount = countRecommendationRows(version);
        log.info(
                "finish recommendation rebuild, version={}, preferenceCount={}, similarityCount={}, recommendationCount={}",
                version,
                preferenceCount,
                similarityCount,
                recommendationCount
        );
    }

    @Override
    public List<ProductVO> listHomeRecommendations(Long userId, int size) {
        int limitedSize = size > 0 ? size : HOME_RECOMMEND_SIZE;
        List<Long> preferredIds = new ArrayList<>();
        if (userId != null) {
            String latestVersion = getLatestRecommendationVersion(userId);
            if (latestVersion != null) {
                List<UserRecommendation> rows = userRecommendationMapper.selectList(Wrappers.lambdaQuery(UserRecommendation.class)
                        .eq(UserRecommendation::getUserId, userId)
                        .eq(UserRecommendation::getSourceVersion, latestVersion)
                        .eq(UserRecommendation::getIsDelete, 0)
                        .and(wrapper -> wrapper.isNull(UserRecommendation::getExpireTime)
                                .or()
                                .gt(UserRecommendation::getExpireTime, new Date()))
                        .orderByAsc(UserRecommendation::getRankNum));
                // 这里的 stream 是把“当前用户最新一版推荐结果”按 rank 顺序转成商品 id 列表。
                preferredIds.addAll(rows.stream()
                        .map(UserRecommendation::getProductId)
                        .filter(Objects::nonNull)
                        .toList());
            }
        }
        return mergeWithHotFallback(preferredIds, limitedSize, Collections.emptySet());
    }

    @Override
    public List<ProductVO> listRelatedProducts(Long userId, Long productId, int size) {
        if (productId == null || productId <= 0) {
            return Collections.emptyList();
        }

        int limitedSize = size > 0 ? size : RELATED_RECOMMEND_SIZE;
        Set<Long> excludeIds = new HashSet<>();
        excludeIds.add(productId);
        Map<Long, Double> scoreMap = new HashMap<>();

        String latestSimilarityVersion = getLatestSimilarityVersion();
        if (latestSimilarityVersion != null) {
            // 先基于当前商品自身的相似商品做一轮召回。
            List<ProductSimilarity> currentProductRows = productSimilarityMapper.selectList(Wrappers.lambdaQuery(ProductSimilarity.class)
                    .eq(ProductSimilarity::getProductId, productId)
                    .eq(ProductSimilarity::getAlgorithmVersion, latestSimilarityVersion)
                    .eq(ProductSimilarity::getIsDelete, 0));
            for (ProductSimilarity row : currentProductRows) {
                if (row.getRelatedProductId() == null || row.getSimilarityScore() == null) {
                    continue;
                }
                scoreMap.merge(row.getRelatedProductId(), row.getSimilarityScore().doubleValue(), Double::sum);
            }

            if (userId != null) {
                List<UserProductPreference> userPreferences = userProductPreferenceMapper.selectList(Wrappers.lambdaQuery(UserProductPreference.class)
                        .eq(UserProductPreference::getUserId, userId)
                        .eq(UserProductPreference::getIsDelete, 0)
                        .gt(UserProductPreference::getPreferenceScore, BigDecimal.ZERO));
                // 这里的 stream 是把用户已经有过行为的商品收集成集合，后面用于排除已互动商品。
                Set<Long> interactedIds = userPreferences.stream()
                        .map(UserProductPreference::getProductId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                excludeIds.addAll(interactedIds);
                excludeIds.remove(productId);

                // 再叠加用户历史偏好，让“猜你喜欢”不只是看当前商品，还会带上用户长期兴趣。
                for (UserProductPreference preference : userPreferences) {
                    if (preference.getProductId() == null || preference.getPreferenceScore() == null) {
                        continue;
                    }
                    int preferenceScore = preference.getPreferenceScore().intValue();
                    double factor = preferenceScore * USER_PREFERENCE_FACTOR;
                    List<ProductSimilarity> rows = productSimilarityMapper.selectList(Wrappers.lambdaQuery(ProductSimilarity.class)
                            .eq(ProductSimilarity::getProductId, preference.getProductId())
                            .eq(ProductSimilarity::getAlgorithmVersion, latestSimilarityVersion)
                            .eq(ProductSimilarity::getIsDelete, 0));
                    for (ProductSimilarity row : rows) {
                        if (row.getRelatedProductId() == null || row.getSimilarityScore() == null) {
                            continue;
                        }
                        scoreMap.merge(row.getRelatedProductId(), row.getSimilarityScore().doubleValue() * factor, Double::sum);
                    }
                }
            }
        }

        // 这里的 stream 是把候选商品按累计分降序排列，并去掉当前详情页不该再次出现的商品。
        List<Long> orderedIds = scoreMap.entrySet().stream()
                .filter(entry -> !excludeIds.contains(entry.getKey()))
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
        return mergeWithHotFallback(orderedIds, limitedSize, excludeIds);
    }

    private void refreshProductSimilarity(String version) {
        markAllSimilarityDeleted();

        List<UserProductPreference> preferences = userProductPreferenceMapper.selectList(Wrappers.lambdaQuery(UserProductPreference.class)
                .eq(UserProductPreference::getIsDelete, 0)
                .gt(UserProductPreference::getPreferenceScore, BigDecimal.ZERO));
        if (preferences.isEmpty()) {
            return;
        }

        Map<Long, Map<Long, Integer>> userProductScoreMap = new HashMap<>();
        Map<Long, Double> normMap = new HashMap<>();
        for (UserProductPreference preference : preferences) {
            if (preference.getUserId() == null || preference.getProductId() == null || preference.getPreferenceScore() == null) {
                continue;
            }
            int score = preference.getPreferenceScore().intValue();
            if (score <= 0) {
                continue;
            }
            userProductScoreMap
                    .computeIfAbsent(preference.getUserId(), key -> new HashMap<>())
                    .put(preference.getProductId(), score);
            normMap.merge(preference.getProductId(), (double) score * score, Double::sum);
        }

        Map<String, Double> numeratorMap = new HashMap<>();
        Map<String, Integer> commonUserCountMap = new HashMap<>();
        // 同一用户交互过的商品，两两组成一个商品对，累加余弦相似度的分子部分。
        for (Map<Long, Integer> productScoreMap : userProductScoreMap.values()) {
            List<Map.Entry<Long, Integer>> items = new ArrayList<>(productScoreMap.entrySet());
            if (items.size() < 2) {
                continue;
            }
            for (int i = 0; i < items.size(); i++) {
                Long leftProductId = items.get(i).getKey();
                int leftScore = items.get(i).getValue();
                for (int j = i + 1; j < items.size(); j++) {
                    Long rightProductId = items.get(j).getKey();
                    int rightScore = items.get(j).getValue();
                    if (Objects.equals(leftProductId, rightProductId)) {
                        continue;
                    }
                    String pairKey = buildPairKey(leftProductId, rightProductId);
                    numeratorMap.merge(pairKey, (double) leftScore * rightScore, Double::sum);
                    commonUserCountMap.merge(pairKey, 1, Integer::sum);
                }
            }
        }

        Map<Long, List<ProductSimilarity>> similarityMap = new HashMap<>();
        for (Map.Entry<String, Double> entry : numeratorMap.entrySet()) {
            String[] ids = entry.getKey().split("_");
            Long leftProductId = Long.parseLong(ids[0]);
            Long rightProductId = Long.parseLong(ids[1]);
            int commonUserCount = commonUserCountMap.getOrDefault(entry.getKey(), 0);
            if (commonUserCount < 1) {
                continue;
            }

            // similarity = 分子 / (两个商品向量长度乘积)
            // 使用公式 相似度 = 分子 / (商品 A 向量模 × 商品 B 向量模)
            double leftNorm = normMap.getOrDefault(leftProductId, 0D);
            double rightNorm = normMap.getOrDefault(rightProductId, 0D);
            double denominator = Math.sqrt(leftNorm) * Math.sqrt(rightNorm);
            if (denominator <= 0) {
                continue;
            }
            double similarityScore = entry.getValue() / denominator;
            addSimilarity(similarityMap, leftProductId, rightProductId, similarityScore, commonUserCount, version);
            addSimilarity(similarityMap, rightProductId, leftProductId, similarityScore, commonUserCount, version);
        }

        for (List<ProductSimilarity> rows : similarityMap.values()) {
            for (ProductSimilarity row : rows) {
                productSimilarityMapper.insert(row);
            }
        }
    }

    private void refreshUserRecommendation(String version, Date expireTime) {
        markAllRecommendationDeleted();

        List<UserProductPreference> preferences = userProductPreferenceMapper.selectList(Wrappers.lambdaQuery(UserProductPreference.class)
                .eq(UserProductPreference::getIsDelete, 0)
                .gt(UserProductPreference::getPreferenceScore, BigDecimal.ZERO));
        if (preferences.isEmpty()) {
            return;
        }

        Set<Long> activeProductIds = productMapper.selectList(Wrappers.lambdaQuery(Product.class)
                        .select(Product::getId)
                        .eq(Product::getIsDelete, 0)
                        .eq(Product::getStatus, 1))
                // 这里的 stream 是把当前仍在线的商品提取成 id 集合，后面推荐时只允许这些商品入选。
                .stream()
                .map(Product::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Map<Long, Integer>> userProductScoreMap = new HashMap<>();
        for (UserProductPreference preference : preferences) {
            if (preference.getUserId() == null || preference.getProductId() == null || preference.getPreferenceScore() == null) {
                continue;
            }
            int score = preference.getPreferenceScore().intValue();
            if (score <= 0) {
                continue;
            }
            userProductScoreMap
                    .computeIfAbsent(preference.getUserId(), key -> new HashMap<>())
                    .put(preference.getProductId(), score);
        }

        Map<Long, List<ProductSimilarity>> similarityByProduct = productSimilarityMapper.selectList(Wrappers.lambdaQuery(ProductSimilarity.class)
                        .eq(ProductSimilarity::getAlgorithmVersion, version)
                        .eq(ProductSimilarity::getIsDelete, 0))
                // 这里的 stream 是把相似度结果按“源商品 id”分组，后面取某个商品的相似商品时直接查 map。
                .stream()
                .collect(Collectors.groupingBy(ProductSimilarity::getProductId));

        for (Map.Entry<Long, Map<Long, Integer>> entry : userProductScoreMap.entrySet()) {
            Long userId = entry.getKey();
            Map<Long, Integer> userScoreMap = entry.getValue();
            Set<Long> interactedProductIds = new HashSet<>(userScoreMap.keySet());
            Map<Long, Double> recommendScoreMap = new HashMap<>();

            // 以“用户已经喜欢的商品”为起点，把相似商品的分数按用户偏好强度累加起来。
            for (Map.Entry<Long, Integer> productScoreEntry : userScoreMap.entrySet()) {
                Long productId = productScoreEntry.getKey();
                int preferenceScore = productScoreEntry.getValue();
                List<ProductSimilarity> similarityRows = similarityByProduct.getOrDefault(productId, Collections.emptyList());
                for (ProductSimilarity similarity : similarityRows) {
                    if (similarity.getRelatedProductId() == null || similarity.getSimilarityScore() == null) {
                        continue;
                    }
                    recommendScoreMap.merge(
                            similarity.getRelatedProductId(),
                            similarity.getSimilarityScore().doubleValue() * preferenceScore,
                            Double::sum
                    );
                }
            }

            // 排除用户已经互动过的商品，只保留还在线的候选商品，再截取 topN。
            // 这里的 stream 就是在做推荐结果的最终裁剪和排序。
            List<Map.Entry<Long, Double>> rankedRows = recommendScoreMap.entrySet().stream()
                    .filter(candidate -> !interactedProductIds.contains(candidate.getKey()))
                    .filter(candidate -> activeProductIds.contains(candidate.getKey()))
                    .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                    .limit(USER_RECOMMEND_TOP_N)
                    .toList();

            int rankNum = 1;
            for (Map.Entry<Long, Double> rankedRow : rankedRows) {
                UserRecommendation entity = new UserRecommendation();
                entity.setUserId(userId);
                entity.setProductId(rankedRow.getKey());
                entity.setRecommendScore(BigDecimal.valueOf(rankedRow.getValue()).setScale(4, RoundingMode.HALF_UP));
                entity.setRecommendReason("基于你的浏览和加购偏好");
                entity.setSourceVersion(version);
                entity.setRankNum(rankNum++);
                entity.setExpireTime(expireTime);
                entity.setIsDelete(0);
                userRecommendationMapper.insert(entity);
            }
        }
    }

    private void addSimilarity(Map<Long, List<ProductSimilarity>> similarityMap,
                               Long productId,
                               Long relatedProductId,
                               double similarityScore,
                               int commonUserCount,
                               String version) {
        ProductSimilarity entity = new ProductSimilarity();
        entity.setProductId(productId);
        entity.setRelatedProductId(relatedProductId);
        entity.setSimilarityScore(BigDecimal.valueOf(similarityScore).setScale(8, RoundingMode.HALF_UP));
        entity.setCommonUserCount(commonUserCount);
        entity.setAlgorithmVersion(version);
        entity.setIsDelete(0);
        similarityMap.computeIfAbsent(productId, key -> new ArrayList<>()).add(entity);
        similarityMap.get(productId).sort(Comparator.comparing(ProductSimilarity::getSimilarityScore).reversed());
        if (similarityMap.get(productId).size() > PRODUCT_SIMILARITY_TOP_N) {
            similarityMap.get(productId).subList(PRODUCT_SIMILARITY_TOP_N, similarityMap.get(productId).size()).clear();
        }
    }

    private List<ProductVO> mergeWithHotFallback(List<Long> preferredIds, int size, Set<Long> excludeIds) {
        LinkedHashSet<Long> mergedIds = new LinkedHashSet<>();
        for (Long productId : preferredIds) {
            if (productId == null || excludeIds.contains(productId)) {
                continue;
            }
            mergedIds.add(productId);
            if (mergedIds.size() >= size) {
                break;
            }
        }
        // 协同过滤结果不够时，用热门商品补齐，避免首页或详情页出现空推荐位。
        if (mergedIds.size() < size) {
            Set<Long> hotExcludeIds = new HashSet<>(excludeIds);
            hotExcludeIds.addAll(mergedIds);
            mergedIds.addAll(getHotFallbackProductIds(size - mergedIds.size(), hotExcludeIds));
        }
        return loadProductVOsByIds(new ArrayList<>(mergedIds), size);
    }

    private List<Long> getHotFallbackProductIds(int size, Set<Long> excludeIds) {
        if (size <= 0) {
            return Collections.emptyList();
        }
        List<Product> activeProducts = productMapper.selectList(Wrappers.lambdaQuery(Product.class)
                .eq(Product::getIsDelete, 0)
                .eq(Product::getStatus, 1));
        if (activeProducts.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Integer> hotScoreMap = new HashMap<>();
        // 热门兜底是一个轻量的行为热度分，不参与协同过滤训练，只在推荐不够时补位。
        for (ProductViewLog viewLog : productViewLogMapper.selectList(Wrappers.lambdaQuery(ProductViewLog.class)
                .eq(ProductViewLog::getIsDelete, 0))) {
            hotScoreMap.merge(viewLog.getProductId(), 1, Integer::sum);
        }
        for (CartItem cartItem : cartItemMapper.selectList(Wrappers.lambdaQuery(CartItem.class)
                .eq(CartItem::getIsDelete, 0))) {
            hotScoreMap.merge(cartItem.getProductId(), 3, Integer::sum);
        }
        for (ProductFavorite favorite : productFavoriteMapper.selectList(Wrappers.lambdaQuery(ProductFavorite.class)
                .eq(ProductFavorite::getIsDelete, 0))) {
            hotScoreMap.merge(favorite.getProductId(), 4, Integer::sum);
        }
        for (ProductReview review : productReviewMapper.selectList(Wrappers.lambdaQuery(ProductReview.class)
                .eq(ProductReview::getIsDelete, 0)
                .eq(ProductReview::getStatus, 1))) {
            hotScoreMap.merge(review.getProductId(), 2, Integer::sum);
        }

        // 这里的 stream 是对兜底商品做过滤、热度排序、截断，再转成最终需要加载的商品 id。
        return activeProducts.stream()
                .filter(product -> product.getId() != null && !excludeIds.contains(product.getId()))
                .sorted(Comparator
                        .comparing((Product product) -> hotScoreMap.getOrDefault(product.getId(), 0)).reversed()
                        .thenComparing(product -> defaultInt(product.getIsRec()), Comparator.reverseOrder())
                        .thenComparing(product -> defaultInt(product.getIsBanner()), Comparator.reverseOrder())
                        .thenComparing(Product::getUpdateTime, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Product::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(size)
                .map(Product::getId)
                .toList();
    }

    private List<ProductVO> loadProductVOsByIds(List<Long> productIds, int limit) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<ProductVO> result = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        for (Long productId : productIds) {
            if (productId == null || !seen.add(productId)) {
                continue;
            }
            ProductVO productVO = productMapper.getProductBaseById(productId);
            if (productVO == null) {
                continue;
            }
            result.add(buildProductVO(productVO));
            if (result.size() >= limit) {
                break;
            }
        }
        return result;
    }

    private ProductVO buildProductVO(ProductVO source) {
        ProductVO productVO = new ProductVO();
        if (source == null) {
            productVO.setFeatureLst(Collections.emptyList());
            return productVO;
        }
        BeanUtils.copyProperties(source, productVO);
        productVO.setFeatureLst(parseFeatureList(source.getFeature()));
        return productVO;
    }

    private List<FeatureItem> parseFeatureList(String feature) {
        if (StringUtils.isBlank(feature)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(feature, FeatureItem.class);
    }

    private void markAllPreferenceDeleted() {
        // 这三张表本质上都是推荐缓存表，每次刷新都全量重建。
        // 这里直接物理删除旧数据，避免和唯一索引冲突，也避免缓存版本不断堆积。
        userProductPreferenceMapper.deleteAllRows();
    }

    private void markAllSimilarityDeleted() {
        productSimilarityMapper.deleteAllRows();
    }

    private void markAllRecommendationDeleted() {
        userRecommendationMapper.deleteAllRows();
    }

    private String getLatestRecommendationVersion(Long userId) {
        UserRecommendation row = userRecommendationMapper.selectOne(Wrappers.lambdaQuery(UserRecommendation.class)
                .select(UserRecommendation::getSourceVersion)
                .eq(UserRecommendation::getUserId, userId)
                .eq(UserRecommendation::getIsDelete, 0)
                .orderByDesc(UserRecommendation::getCreateTime)
                .last("limit 1"));
        return row == null ? null : row.getSourceVersion();
    }

    private String getLatestSimilarityVersion() {
        ProductSimilarity row = productSimilarityMapper.selectOne(Wrappers.lambdaQuery(ProductSimilarity.class)
                .select(ProductSimilarity::getAlgorithmVersion)
                .eq(ProductSimilarity::getIsDelete, 0)
                .orderByDesc(ProductSimilarity::getCreateTime)
                .last("limit 1"));
        return row == null ? null : row.getAlgorithmVersion();
    }

    private String buildVersion() {
        return "cf_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    private Date nextRefreshTime() {
        return new Date(System.currentTimeMillis() + 24L * 60 * 60 * 1000);
    }

    private String buildPairKey(Long leftProductId, Long rightProductId) {
        Long min = Math.min(leftProductId, rightProductId);
        Long max = Math.max(leftProductId, rightProductId);
        return min + "_" + max;
    }

    private String buildUserProductKey(Long userId, Long productId) {
        return userId + "_" + productId;
    }

    private Date maxDate(Date left, Date right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        return left.after(right) ? left : right;
    }

    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private long countPreferenceRows() {
        Long count = userProductPreferenceMapper.selectCount(Wrappers.lambdaQuery(UserProductPreference.class)
                .eq(UserProductPreference::getIsDelete, 0));
        return count == null ? 0L : count;
    }

    private long countSimilarityRows(String version) {
        Long count = productSimilarityMapper.selectCount(Wrappers.lambdaQuery(ProductSimilarity.class)
                .eq(ProductSimilarity::getAlgorithmVersion, version)
                .eq(ProductSimilarity::getIsDelete, 0));
        return count == null ? 0L : count;
    }

    private long countRecommendationRows(String version) {
        Long count = userRecommendationMapper.selectCount(Wrappers.lambdaQuery(UserRecommendation.class)
                .eq(UserRecommendation::getSourceVersion, version)
                .eq(UserRecommendation::getIsDelete, 0));
        return count == null ? 0L : count;
    }

    private static final class PreferenceAccumulator {
        private int viewCount;
        private int favoriteScore;
        private int cartScore;
        private int reviewScore;
        private Date lastBehaviorTime;
    }
}
