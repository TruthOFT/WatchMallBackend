package com.watch.watch_mall.model.dto.product;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateProductRequest extends AddProductRequest implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;
}
