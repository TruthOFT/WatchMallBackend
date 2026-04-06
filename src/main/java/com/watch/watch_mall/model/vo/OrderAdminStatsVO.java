package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderAdminStatsVO implements Serializable {

    private Long totalCount;

    private Long pendingCount;

    private Long paidCount;

    private Long closedCount;

    private Long overduePendingCount;

    private Long todayCount;

    private static final long serialVersionUID = 1L;
}
