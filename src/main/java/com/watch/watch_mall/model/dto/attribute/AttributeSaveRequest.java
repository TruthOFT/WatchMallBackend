package com.watch.watch_mall.model.dto.attribute;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AttributeSaveRequest implements Serializable {

    private Long id;

    private String name;

    private Long categoryId;

    private List<String> valueList;

    private static final long serialVersionUID = 1L;
}
