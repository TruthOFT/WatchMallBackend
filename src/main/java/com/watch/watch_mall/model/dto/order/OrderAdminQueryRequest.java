package com.watch.watch_mall.model.dto.order;

import com.watch.watch_mall.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderAdminQueryRequest extends PageRequest implements Serializable {

    private String keyword;

    private Integer orderStatus;

    private static final long serialVersionUID = 1L;
}
