package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watch.watch_mall.mapper.AttributeValuesMapper;
import com.watch.watch_mall.model.entity.AttributeValues;
import com.watch.watch_mall.model.vo.AttributesVO;
import com.watch.watch_mall.service.AttributeValuesService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ginger
* @description 针对表【attribute_values】的数据库操作Service实现
* @createDate 2026-02-20 13:04:58
*/
@Service
public class AttributeValuesServiceImpl extends ServiceImpl<AttributeValuesMapper, AttributeValues>
    implements AttributeValuesService {

    @Resource
    private AttributeValuesMapper attributeValuesMapper;

    @Override
    public List<AttributesVO> listAttributeValues() {
        return attributeValuesMapper.listAttrValue();
    }
}




