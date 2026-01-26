package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class HomeProductVO {
    List<CategoryVO> categoryVOList;
    ProductVO productVO;
    List<ProductVO> bannerList;
    List<ProductVO> choiceList;
    List<ProductVO> recommendList;

}
