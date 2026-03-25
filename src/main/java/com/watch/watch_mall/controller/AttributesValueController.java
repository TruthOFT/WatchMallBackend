package com.watch.watch_mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.model.entity.Attributes;
import com.watch.watch_mall.model.vo.AttributesVO;
import com.watch.watch_mall.service.AttributeValuesService;
import com.watch.watch_mall.service.AttributesService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attrValue")
public class AttributesValueController {

    @Resource
    AttributeValuesService attributeValuesService;

    @GetMapping("/listAtrValue")
    public BaseResponse<List<AttributesVO>> listAtrValue() {
        return ResultUtils.success(attributeValuesService.listAttributeValues(), "查询成功");
    }
}
