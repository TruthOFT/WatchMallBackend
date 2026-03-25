package com.watch.watch_mall;

import com.watch.watch_mall.model.dto.product.AddProductRequest;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.service.ProductService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class WatchMallBackendApplicationTests {

    @Resource
    ProductService productService;

    @Test
    void contextLoads() {
        AddProductRequest addProductRequest = new AddProductRequest();
        addProductRequest.setName("TTT");
        addProductRequest.setDescription("TTTDesc");
        addProductRequest.setFeature("TTTFeature");
        addProductRequest.setTags("TTTTags");
        addProductRequest.setTitle("TTTTitle");
        addProductRequest.setCategoryNames(List.of("Test1", "Test2"));


        productService.addProduct(addProductRequest);
    }

}
