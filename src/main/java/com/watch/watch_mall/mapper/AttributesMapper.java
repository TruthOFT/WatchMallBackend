package com.watch.watch_mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.model.dto.attribute.AttributeQueryRequest;
import com.watch.watch_mall.model.entity.Attributes;
import com.watch.watch_mall.model.vo.AttributeAdminDetailVO;
import com.watch.watch_mall.model.vo.AttributeAdminPageVO;
import org.apache.ibatis.annotations.Param;

public interface AttributesMapper extends BaseMapper<Attributes> {

    Page<AttributeAdminPageVO> pageAdminAttributes(Page<AttributeAdminPageVO> page, @Param("query") AttributeQueryRequest query);

    AttributeAdminDetailVO getAdminAttributeDetail(@Param("id") Long id);
}
