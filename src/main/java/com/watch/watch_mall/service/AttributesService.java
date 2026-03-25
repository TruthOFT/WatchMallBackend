package com.watch.watch_mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.watch.watch_mall.model.dto.attribute.AttributeQueryRequest;
import com.watch.watch_mall.model.dto.attribute.AttributeSaveRequest;
import com.watch.watch_mall.model.entity.Attributes;
import com.watch.watch_mall.model.vo.AttributeAdminDetailVO;
import com.watch.watch_mall.model.vo.AttributeAdminPageVO;

public interface AttributesService extends IService<Attributes> {

    boolean saveAttribute(AttributeSaveRequest request);

    boolean updateAttribute(AttributeSaveRequest request);

    Page<AttributeAdminPageVO> pageAdminAttributes(AttributeQueryRequest queryRequest);

    AttributeAdminDetailVO getAdminAttributeDetail(Long id);

    boolean deleteAttribute(Long id);
}
