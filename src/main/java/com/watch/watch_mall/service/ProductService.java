package com.watch.watch_mall.service;

import com.watch.watch_mall.model.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.watch.watch_mall.model.vo.HomeProductVO;
import org.springframework.web.multipart.MultipartFile;

/**
* @author Truth
* @description 针对表【product】的数据库操作Service
* @createDate 2026-01-11 16:19:33
*/
public interface ProductService extends IService<Product> {
    // 新增商品
    boolean addProduct(Product product, MultipartFile file);

    String uploadFile(MultipartFile file);

    HomeProductVO getHomeProductVO();
}
