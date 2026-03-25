package com.watch.watch_mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.watch.watch_mall.model.entity.AttributeValues;
import com.watch.watch_mall.model.vo.AttributesVO;

import java.util.List;

/**
* @author Ginger
* @description 针对表【attribute_values】的数据库操作Service
* @createDate 2026-02-20 13:04:58
*/
public interface AttributeValuesService extends IService<AttributeValues> {

    List<AttributesVO> listAttributeValues();
}
