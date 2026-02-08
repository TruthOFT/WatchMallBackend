package com.watch.watch_mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.watch.watch_mall.model.entity.Category;
import com.watch.watch_mall.model.vo.CategoryVO;

import java.util.List;

/**
 * @author Truth
 * @description 针对表【category】的数据库操作Service
 * @createDate 2026-01-10 17:53:18
 */
public interface CategoryService extends IService<Category> {
    List<CategoryVO> getCategoryVOList();
}
