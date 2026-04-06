package com.watch.watch_mall.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.mapper.ProductMapper;
import com.watch.watch_mall.model.dto.product.AddProductRequest;
import com.watch.watch_mall.model.dto.product.ProductAdminQueryRequest;
import com.watch.watch_mall.model.dto.product.ProductViewTrackRequest;
import com.watch.watch_mall.model.dto.product.UpdateProductRequest;
import com.watch.watch_mall.model.entity.Category;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.model.entity.ProductCategory;
import com.watch.watch_mall.model.entity.ProductImages;
import com.watch.watch_mall.model.entity.ProductSkus;
import com.watch.watch_mall.model.inner_data.FeatureItem;
import com.watch.watch_mall.model.vo.HomeProductVO;
import com.watch.watch_mall.model.vo.ProductAdminDetailVO;
import com.watch.watch_mall.model.vo.ProductAdminPageVO;
import com.watch.watch_mall.model.vo.ProductDetailVO;
import com.watch.watch_mall.model.vo.ProductImageVO;
import com.watch.watch_mall.model.vo.ProductSkuVO;
import com.watch.watch_mall.model.vo.ProductVO;
import com.watch.watch_mall.service.CategoryService;
import com.watch.watch_mall.service.ProductCategoryService;
import com.watch.watch_mall.service.ProductImagesService;
import com.watch.watch_mall.service.ProductSearchService;
import com.watch.watch_mall.service.ProductService;
import com.watch.watch_mall.service.ProductSkusService;
import com.watch.watch_mall.service.RecommendationService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Resource
    private CategoryService categoryService;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductImagesService productImagesService;

    @Resource
    private ProductCategoryService productCategoryService;

    @Resource
    private ProductSkusService productSkusService;

    @Resource
    private RecommendationService recommendationService;

    @Resource
    private ProductSearchService productSearchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addProduct(AddProductRequest addProductRequest) {
        ThrowUtils.throwIf(addProductRequest == null, ErrorCode.PARAMS_ERROR);
        Product product = buildProduct(addProductRequest);
        boolean saved = this.save(product);
        ThrowUtils.throwIf(!saved || product.getId() == null, ErrorCode.OPERATION_ERROR, "商品保存失败");
        saveProductRelations(product.getId(), addProductRequest);
        productSearchService.syncProductById(product.getId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProduct(Long productId) {
        ThrowUtils.throwIf(productId == null || productId <= 0, ErrorCode.PARAMS_ERROR);
        boolean removed = this.removeById(productId);
        ThrowUtils.throwIf(!removed, ErrorCode.OPERATION_ERROR, "鍟嗗搧鍒犻櫎澶辫触");
        productSearchService.deleteProductById(productId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProduct(UpdateProductRequest updateProductRequest) {
        ThrowUtils.throwIf(updateProductRequest == null || updateProductRequest.getId() == null || updateProductRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        Product existing = this.getById(updateProductRequest.getId());
        ThrowUtils.throwIf(existing == null, ErrorCode.NOT_FOUND_ERROR);

        LambdaUpdateWrapper<Product> updateWrapper = Wrappers.lambdaUpdate(Product.class)
                .eq(Product::getId, updateProductRequest.getId())
                .set(Product::getName, StringUtils.trimToNull(updateProductRequest.getName()))
                .set(Product::getTitle, StringUtils.trimToNull(updateProductRequest.getTitle()))
                .set(Product::getBrandId, null)
                .set(Product::getDescription, StringUtils.trimToNull(updateProductRequest.getDescription()))
                .set(Product::getFeature, normalizeFeature(updateProductRequest.getFeature()))
                .set(Product::getTags, StringUtils.trimToNull(updateProductRequest.getTags()))
                .set(Product::getPrice, defaultPrice(updateProductRequest.getPrice()))
                .set(Product::getIsHero, defaultFlag(updateProductRequest.getIsHero()))
                .set(Product::getIsBanner, defaultFlag(updateProductRequest.getIsBanner()))
                .set(Product::getIsRec, defaultFlag(updateProductRequest.getIsRec()))
                .set(Product::getStatus, defaultStatus(updateProductRequest.getStatus()))
                .set(Product::getVersion, Optional.ofNullable(existing.getVersion()).orElse(1) + 1);
        boolean updated = this.update(updateWrapper);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "商品更新失败");

        clearProductRelations(updateProductRequest.getId());
        saveProductRelations(updateProductRequest.getId(), updateProductRequest);
        productSearchService.syncProductById(updateProductRequest.getId());
        return true;
    }

    @Override
    public HomeProductVO getHomeProductVO(Long userId) {
        HomeProductVO homeProductVO = new HomeProductVO();
        homeProductVO.setCategoryVOList(categoryService.getCategoryVOList());
        homeProductVO.setRecommendList(recommendationService.listHomeRecommendations(userId, 8));
        homeProductVO.setBannerList(productMapper.getBannerProducts().stream().map(this::buildProductVO).toList());
        homeProductVO.setProductVO(buildProductVO(productMapper.getHeroProduct()));
        return homeProductVO;
    }

    @Override
    public ProductDetailVO getProductDetail(Long productId) {
        ThrowUtils.throwIf(productId == null || productId <= 0, ErrorCode.PARAMS_ERROR);
        ProductVO product = productMapper.getProductBaseById(productId);
        ThrowUtils.throwIf(product == null, ErrorCode.NOT_FOUND_ERROR);

        ProductDetailVO productDetailVO = new ProductDetailVO();
        BeanUtils.copyProperties(product, productDetailVO);
        productDetailVO.setFeatureLst(parseFeatureList(product.getFeature()));

        List<ProductImages> productImages = productMapper.getProductImagesByProductId(productId);
        List<ProductImageVO> imageList = productImages.stream().map(productImage -> {
            ProductImageVO productImageVO = new ProductImageVO();
            BeanUtils.copyProperties(productImage, productImageVO);
            return productImageVO;
        }).toList();
        productDetailVO.setImageList(imageList);
        productDetailVO.setMainUrl(imageList.isEmpty() ? null : imageList.get(0).getUrl());
        productDetailVO.setCategoryList(productMapper.getCategoryListByProductId(productId));

        List<ProductSkuVO> skuList = productMapper.getSkuListByProductId(productId).stream().map(productSku -> {
            ProductSkuVO productSkuVO = new ProductSkuVO();
            BeanUtils.copyProperties(productSku, productSkuVO);
            return productSkuVO;
        }).toList();
        productDetailVO.setSkuList(skuList);
        return productDetailVO;
    }

    @Override
    public boolean trackProductView(Long userId, ProductViewTrackRequest request) {
        return recommendationService.trackProductView(userId, request);
    }

    @Override
    public List<ProductVO> listRelatedProducts(Long userId, Long productId, Integer size) {
        int limitedSize = size == null || size <= 0 ? 6 : Math.min(size, 20);
        return recommendationService.listRelatedProducts(userId, productId, limitedSize);
    }

    @Override
    public Page<ProductVO> listProductByCategory(Long categoryId, long current, long pageSize) {
        ThrowUtils.throwIf(categoryId == null || categoryId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(current <= 0 || pageSize <= 0 || pageSize > 100, ErrorCode.PARAMS_ERROR);
        Category category = categoryService.getById(categoryId);
        ThrowUtils.throwIf(category == null, ErrorCode.NOT_FOUND_ERROR);

        Page<ProductVO> page = productMapper.listProductByCategory(new Page<>(current, pageSize), categoryId);
        if (page.getRecords() == null || page.getRecords().isEmpty()) {
            return page;
        }
        page.setRecords(page.getRecords().stream().map(this::buildProductVO).toList());
        return page;
    }

    @Override
    public Page<ProductAdminPageVO> pageAdminProducts(ProductAdminQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        ThrowUtils.throwIf(current <= 0 || pageSize <= 0 || pageSize > 100, ErrorCode.PARAMS_ERROR);
        return productMapper.pageAdminProducts(new Page<>(current, pageSize), queryRequest);
    }

    @Override
    public ProductAdminDetailVO getAdminProductDetail(Long productId) {
        ThrowUtils.throwIf(productId == null || productId <= 0, ErrorCode.PARAMS_ERROR);
        Product product = this.getById(productId);
        ThrowUtils.throwIf(product == null, ErrorCode.NOT_FOUND_ERROR);

        ProductAdminDetailVO detailVO = new ProductAdminDetailVO();
        BeanUtils.copyProperties(product, detailVO);
        detailVO.setCategoryIds(productMapper.getCategoryIdListByProductId(productId));

        List<ProductAdminDetailVO.ImageItemVO> imageList = productMapper.getProductImagesByProductId(productId).stream().map(item -> {
            ProductAdminDetailVO.ImageItemVO imageItemVO = new ProductAdminDetailVO.ImageItemVO();
            BeanUtils.copyProperties(item, imageItemVO);
            return imageItemVO;
        }).toList();
        detailVO.setImages(imageList);

        List<ProductAdminDetailVO.SkuItemVO> skuList = productMapper.getSkuListByProductId(productId).stream().map(item -> {
            ProductAdminDetailVO.SkuItemVO skuItemVO = new ProductAdminDetailVO.SkuItemVO();
            BeanUtils.copyProperties(item, skuItemVO);
            return skuItemVO;
        }).toList();
        detailVO.setSkus(skuList);
        return detailVO;
    }

    private Product buildProduct(AddProductRequest request) {
        Product product = new Product();
        product.setName(StringUtils.trimToNull(request.getName()));
        product.setTitle(StringUtils.trimToNull(request.getTitle()));
        product.setDescription(StringUtils.trimToNull(request.getDescription()));
        product.setFeature(normalizeFeature(request.getFeature()));
        product.setTags(StringUtils.trimToNull(request.getTags()));
        product.setPrice(defaultPrice(request.getPrice()));
        product.setIsHero(defaultFlag(request.getIsHero()));
        product.setIsBanner(defaultFlag(request.getIsBanner()));
        product.setIsRec(defaultFlag(request.getIsRec()));
        product.setStatus(defaultStatus(request.getStatus()));
        product.setVersion(1);
        ThrowUtils.throwIf(StringUtils.isBlank(product.getName()), ErrorCode.PARAMS_ERROR, "商品名称不能为空");
        return product;
    }

    private void saveProductRelations(Long productId, AddProductRequest request) {
        validateCategoryIds(request.getCategoryIds());
        saveProductCategories(productId, request.getCategoryIds());
        saveProductImages(productId, request.getImages());
        saveProductSkus(productId, request.getSkus());
    }

    private void clearProductRelations(Long productId) {
        productSkusService.remove(Wrappers.lambdaQuery(ProductSkus.class).eq(ProductSkus::getProductId, productId));
        productImagesService.remove(Wrappers.lambdaQuery(ProductImages.class).eq(ProductImages::getProductId, productId));
        productCategoryService.remove(Wrappers.lambdaQuery(ProductCategory.class).eq(ProductCategory::getProductId, productId));
    }

    private void validateCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        List<Long> normalizedIds = categoryIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (normalizedIds.isEmpty()) {
            return;
        }
        long count = categoryService.count(Wrappers.lambdaQuery(Category.class).in(Category::getId, normalizedIds));
        ThrowUtils.throwIf(count != normalizedIds.size(), ErrorCode.PARAMS_ERROR, "分类不存在");
    }

    private void saveProductCategories(Long productId, List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        List<ProductCategory> relations = categoryIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(categoryId -> new ProductCategory(null, productId, categoryId))
                .toList();
        if (!relations.isEmpty()) {
            productCategoryService.saveBatch(relations);
        }
    }

    private void saveProductImages(Long productId, List<AddProductRequest.ImageItem> images) {
        List<AddProductRequest.ImageItem> normalizedImages = normalizeImages(images);
        if (normalizedImages.isEmpty()) {
            return;
        }
        List<ProductImages> entities = normalizedImages.stream().map(item -> {
            ProductImages image = new ProductImages();
            image.setProductId(productId);
            image.setUrl(item.getUrl());
            image.setIsMain(item.getIsMain());
            image.setSortOrder(Optional.ofNullable(item.getSortOrder()).orElse(0));
            return image;
        }).toList();
        productImagesService.saveBatch(entities);
    }

    private void saveProductSkus(Long productId, List<AddProductRequest.SkuItem> skuItems) {
        List<AddProductRequest.SkuItem> normalizedSkus = normalizeSkus(skuItems);
        if (normalizedSkus.isEmpty()) {
            return;
        }

        List<ProductSkus> skuEntities = normalizedSkus.stream().map(item -> {
            ProductSkus sku = new ProductSkus();
            sku.setProductId(productId);
            sku.setSkuCode(StringUtils.trimToNull(item.getSkuCode()));
            sku.setSkuName(StringUtils.trimToNull(item.getSkuName()));
            sku.setImage(StringUtils.trimToNull(item.getImage()));
            sku.setPrice(defaultPrice(item.getPrice()));
            sku.setMarketPrice(defaultPrice(item.getMarketPrice()));
            sku.setStock(Optional.ofNullable(item.getStock()).orElse(0));
            sku.setLockStock(Optional.ofNullable(item.getLockStock()).orElse(0));
            sku.setVersion(1);
            return sku;
        }).toList();

        productSkusService.saveBatch(skuEntities);
    }

    private List<AddProductRequest.ImageItem> normalizeImages(List<AddProductRequest.ImageItem> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }
        List<AddProductRequest.ImageItem> result = new ArrayList<>();
        for (AddProductRequest.ImageItem item : images) {
            String url = item == null ? null : StringUtils.trimToNull(item.getUrl());
            if (url == null) {
                continue;
            }
            AddProductRequest.ImageItem normalized = new AddProductRequest.ImageItem();
            normalized.setUrl(url);
            normalized.setIsMain(defaultFlag(item.getIsMain()));
            normalized.setSortOrder(Optional.ofNullable(item.getSortOrder()).orElse(0));
            result.add(normalized);
        }
        long mainCount = result.stream().filter(item -> item.getIsMain() == 1).count();
        ThrowUtils.throwIf(mainCount > 1, ErrorCode.PARAMS_ERROR, "只能设置一张主图");
        if (!result.isEmpty() && mainCount == 0) {
            result.get(0).setIsMain(1);
        }
        return result;
    }

    private List<AddProductRequest.SkuItem> normalizeSkus(List<AddProductRequest.SkuItem> skuItems) {
        if (skuItems == null || skuItems.isEmpty()) {
            return Collections.emptyList();
        }
        List<AddProductRequest.SkuItem> result = new ArrayList<>();
        for (AddProductRequest.SkuItem item : skuItems) {
            if (item == null) {
                continue;
            }
            boolean empty = StringUtils.isBlank(item.getSkuCode())
                    && StringUtils.isBlank(item.getSkuName())
                    && StringUtils.isBlank(item.getImage())
                    && item.getPrice() == null
                    && item.getMarketPrice() == null
                    && item.getStock() == null
                    && item.getLockStock() == null;
            if (empty) {
                continue;
            }
            AddProductRequest.SkuItem normalized = new AddProductRequest.SkuItem();
            normalized.setSkuCode(StringUtils.trimToNull(item.getSkuCode()));
            normalized.setSkuName(StringUtils.trimToNull(item.getSkuName()));
            normalized.setImage(StringUtils.trimToNull(item.getImage()));
            normalized.setPrice(defaultPrice(item.getPrice()));
            normalized.setMarketPrice(defaultPrice(item.getMarketPrice()));
            normalized.setStock(Optional.ofNullable(item.getStock()).orElse(0));
            normalized.setLockStock(Optional.ofNullable(item.getLockStock()).orElse(0));
            result.add(normalized);
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

    private String normalizeFeature(String feature) {
        String normalizedFeature = StringUtils.trimToNull(feature);
        if (normalizedFeature == null) {
            return null;
        }
        List<FeatureItem> featureItems = JSON.parseArray(normalizedFeature, FeatureItem.class);
        return JSON.toJSONString(featureItems);
    }

    private BigDecimal defaultPrice(BigDecimal price) {
        return price == null ? BigDecimal.ZERO : price;
    }

    private Integer defaultFlag(Integer value) {
        return value == null ? 0 : value;
    }

    private Integer defaultStatus(Integer value) {
        return value == null ? 1 : value;
    }
}
