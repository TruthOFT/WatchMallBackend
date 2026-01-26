package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.model.vo.HomeProductVO;
import com.watch.watch_mall.model.vo.ProductVO;
import com.watch.watch_mall.service.CategoryService;
import com.watch.watch_mall.service.FileService;
import com.watch.watch_mall.service.ProductService;
import com.watch.watch_mall.mapper.ProductMapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private final String BIZ = "product";

    @Resource
    FileService fileService;

    @Override
    public boolean addProduct(Product product, MultipartFile file) {
        ThrowUtils.throwIf(product == null, ErrorCode.PARAMS_ERROR);
        String filePath = null;
        if (file != null) {
            filePath = fileService.uploadFile(file, BIZ);
        }
        product.setImageUrl(filePath);
        return this.save(product);
    }

    @Override
    public String uploadFile(MultipartFile file) {
        ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.PARAMS_ERROR);
        return fileService.uploadFile(file, BIZ);
    }

    @Override
    public HomeProductVO getHomeProductVO() {
        HomeProductVO homeProductVO = new HomeProductVO();
        homeProductVO.setCategoryVOList(categoryService.getCategoryVOList());
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isHero", 1);
        Product one = this.getOne(queryWrapper);
        if (one != null) {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(one, productVO);
            homeProductVO.setProductVO(productVO);
        }
        List<ProductVO> bannerLst = this.list().stream().filter(product -> product.getIsBanner() == 1).map(product -> {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(product, productVO);
            return productVO;
        }).toList();
        homeProductVO.setBannerList(bannerLst);
        List<ProductVO> choiceLst = this.list().stream().filter(product -> product.getIsChoice() == 1).map(product -> {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(product, productVO);
            return productVO;
        }).toList();
        homeProductVO.setChoiceList(choiceLst);
        // 推荐
        List<ProductVO> recLst = this.list().stream().filter(product -> product.getIsRec() == 1).map(product -> {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(product, productVO);
            List<String> featureList = Arrays.asList(product.getFeature().split(", +"));
            productVO.setFeature(featureList);
            return productVO;
        }).toList();
        homeProductVO.setRecommendList(recLst);
        return homeProductVO;
    }
}




