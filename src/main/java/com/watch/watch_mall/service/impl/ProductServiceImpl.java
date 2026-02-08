package com.watch.watch_mall.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.model.entity.ProductImages;
import com.watch.watch_mall.model.inner_data.FeatureItem;
import com.watch.watch_mall.model.vo.HomeProductVO;
import com.watch.watch_mall.model.vo.ProductVO;
import com.watch.watch_mall.service.CategoryService;
import com.watch.watch_mall.service.FileService;
import com.watch.watch_mall.service.ProductImagesService;
import com.watch.watch_mall.service.ProductService;
import com.watch.watch_mall.mapper.ProductMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.watch.watch_mall.constant.CommonConstant.*;

import java.util.List;

import static com.watch.watch_mall.constant.CommonConstant.BIZ_PRODUCT;

/**
 * @author Truth
 * @description 针对表【product】的数据库操作Service实现
 * @createDate 2026-01-11 16:19:33
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
        implements ProductService {

    @Resource
    CategoryService categoryService;

    @Resource
    FileService fileService;

    @Resource
    ProductMapper productMapper;

    @Resource
    ProductImagesService productImagesService;

    @Override
    public boolean addProduct(Product product, MultipartFile file) {
        ThrowUtils.throwIf(product == null, ErrorCode.PARAMS_ERROR);
        boolean save = this.save(product);
        if (!save) {
            return false;
        }
        String filePath = "";
        if (file != null) {
            filePath = uploadFile(file);
        }
        ProductImages productImages = new ProductImages();
        productImages.setProductId(product.getId());
        productImages.setUrl(filePath);
        return productImagesService.save(productImages);
    }

    @Override
    public String uploadFile(MultipartFile file) {
        ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.PARAMS_ERROR);
        return fileService.uploadFile(file, BIZ_PRODUCT);
    }

    @Override
    public HomeProductVO getHomeProductVO() {
        HomeProductVO homeProductVO = new HomeProductVO();
        homeProductVO.setCategoryVOList(categoryService.getCategoryVOList());
        // 推荐列表
        List<ProductVO> recLst = productMapper.getRecommendProducts().stream().map(product -> {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(product, productVO);
            List<FeatureItem> featureItems = JSON.parseArray(product.getFeature(), FeatureItem.class);
            productVO.setFeatureLst(featureItems);
            return productVO;
        }).toList();
        homeProductVO.setRecommendList(recLst);
        // banner
        List<ProductVO> bannerLst = productMapper.getBannerProducts().stream().map(product -> {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(product, productVO);
            List<FeatureItem> featureItems = JSON.parseArray(product.getFeature(), FeatureItem.class);
            productVO.setFeatureLst(featureItems);
            return productVO;
        }).toList();
        homeProductVO.setBannerList(bannerLst);
        ProductVO heroProduct = productMapper.getHeroProduct();
        heroProduct.setFeatureLst(JSON.parseArray(heroProduct.getFeature(), FeatureItem.class));
        homeProductVO.setProductVO(heroProduct);
        return homeProductVO;
    }
}




