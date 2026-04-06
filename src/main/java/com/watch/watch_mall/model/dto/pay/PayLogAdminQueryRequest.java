package com.watch.watch_mall.model.dto.pay;

import com.watch.watch_mall.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class PayLogAdminQueryRequest extends PageRequest implements Serializable {

    private String keyword;

    private Long userId;

    private Integer payStatus;

    private String payTimeStart;

    private String payTimeEnd;

    private static final long serialVersionUID = 1L;
}
