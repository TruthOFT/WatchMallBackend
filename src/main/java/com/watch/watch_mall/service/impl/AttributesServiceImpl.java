package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.mapper.AttributesMapper;
import com.watch.watch_mall.model.dto.attribute.AttributeQueryRequest;
import com.watch.watch_mall.model.dto.attribute.AttributeSaveRequest;
import com.watch.watch_mall.model.entity.AttributeValues;
import com.watch.watch_mall.model.entity.Attributes;
import com.watch.watch_mall.model.entity.Category;
import com.watch.watch_mall.model.vo.AttributeAdminDetailVO;
import com.watch.watch_mall.model.vo.AttributeAdminPageVO;
import com.watch.watch_mall.service.AttributeValuesService;
import com.watch.watch_mall.service.AttributesService;
import com.watch.watch_mall.service.CategoryService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class AttributesServiceImpl extends ServiceImpl<AttributesMapper, Attributes> implements AttributesService {

    @Resource
    private AttributesMapper attributesMapper;

    @Resource
    private AttributeValuesService attributeValuesService;

    @Resource
    private CategoryService categoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveAttribute(AttributeSaveRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        String name = StringUtils.trimToNull(request.getName());
        ThrowUtils.throwIf(name == null, ErrorCode.PARAMS_ERROR, "属性名称不能为空");
        validateCategory(request.getCategoryId());
        List<String> valueList = normalizeValueList(request.getValueList());
        ThrowUtils.throwIf(valueList.isEmpty(), ErrorCode.PARAMS_ERROR, "至少保留一个属性值");

        Attributes attributes = new Attributes();
        attributes.setName(name);
        attributes.setCategoryId(request.getCategoryId());
        boolean saved = this.save(attributes);
        ThrowUtils.throwIf(!saved || attributes.getId() == null, ErrorCode.OPERATION_ERROR, "属性保存失败");
        saveAttributeValues(attributes.getId(), valueList);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAttribute(AttributeSaveRequest request) {
        ThrowUtils.throwIf(request == null || request.getId() == null || request.getId() <= 0, ErrorCode.PARAMS_ERROR);
        Attributes existing = this.getById(request.getId());
        ThrowUtils.throwIf(existing == null, ErrorCode.NOT_FOUND_ERROR);
        String name = StringUtils.trimToNull(request.getName());
        ThrowUtils.throwIf(name == null, ErrorCode.PARAMS_ERROR, "属性名称不能为空");
        validateCategory(request.getCategoryId());
        List<String> valueList = normalizeValueList(request.getValueList());
        ThrowUtils.throwIf(valueList.isEmpty(), ErrorCode.PARAMS_ERROR, "至少保留一个属性值");

        LambdaUpdateWrapper<Attributes> updateWrapper = Wrappers.lambdaUpdate(Attributes.class)
                .eq(Attributes::getId, request.getId())
                .set(Attributes::getName, name)
                .set(Attributes::getCategoryId, request.getCategoryId());
        boolean updated = this.update(updateWrapper);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "属性更新失败");

        attributeValuesService.remove(Wrappers.lambdaQuery(AttributeValues.class)
                .eq(AttributeValues::getAttributeId, request.getId()));
        saveAttributeValues(request.getId(), valueList);
        return true;
    }

    @Override
    public Page<AttributeAdminPageVO> pageAdminAttributes(AttributeQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        ThrowUtils.throwIf(current <= 0 || pageSize <= 0 || pageSize > 100, ErrorCode.PARAMS_ERROR);
        return attributesMapper.pageAdminAttributes(new Page<>(current, pageSize), queryRequest);
    }

    @Override
    public AttributeAdminDetailVO getAdminAttributeDetail(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        AttributeAdminDetailVO detailVO = attributesMapper.getAdminAttributeDetail(id);
        ThrowUtils.throwIf(detailVO == null, ErrorCode.NOT_FOUND_ERROR);
        if (detailVO.getValueList() == null) {
            detailVO.setValueList(Collections.emptyList());
        }
        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAttribute(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return this.removeById(id);
    }

    private void validateCategory(Long categoryId) {
        if (categoryId == null) {
            return;
        }
        Category category = categoryService.getById(categoryId);
        ThrowUtils.throwIf(category == null, ErrorCode.PARAMS_ERROR, "分类不存在");
    }

    private List<String> normalizeValueList(List<String> valueList) {
        if (valueList == null || valueList.isEmpty()) {
            return Collections.emptyList();
        }
        return valueList.stream()
                .map(StringUtils::trimToNull)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private void saveAttributeValues(Long attributeId, List<String> valueList) {
        List<AttributeValues> entities = valueList.stream().map(value -> {
            AttributeValues attributeValues = new AttributeValues();
            attributeValues.setAttributeId(attributeId);
            attributeValues.setValue(value);
            return attributeValues;
        }).toList();
        if (!entities.isEmpty()) {
            attributeValuesService.saveBatch(entities);
        }
    }
}
