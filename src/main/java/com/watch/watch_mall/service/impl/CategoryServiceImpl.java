package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watch.watch_mall.mapper.CategoryMapper;
import com.watch.watch_mall.model.entity.Category;
import com.watch.watch_mall.model.vo.CategoryVO;
import com.watch.watch_mall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Truth
 * @description 针对表【category】的数据库操作Service实现
 * @createDate 2026-01-10 17:53:18
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {
    @Override
    public List<CategoryVO> getCategoryVOList() {
        return this.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getIsShow, 1)
                .orderByAsc(Category::getSortOrder)
                .orderByDesc(Category::getCreateTime)).stream().map(category -> {
            CategoryVO categoryVO = new CategoryVO();
            BeanUtils.copyProperties(category, categoryVO);
            return categoryVO;
        }).toList();
    }
}




