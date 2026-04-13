package com.watch.watch_mall.model.dto.support;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SupportAdminQueryRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private long current = 1;

    private long pageSize = 10;

    private String keyword;

    private String status;

    private String source;
}
