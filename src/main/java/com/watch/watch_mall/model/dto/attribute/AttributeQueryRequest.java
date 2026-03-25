package com.watch.watch_mall.model.dto.attribute;

import com.watch.watch_mall.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttributeQueryRequest extends PageRequest implements Serializable {

    private String name;

    private Long categoryId;

    private static final long serialVersionUID = 1L;
}
