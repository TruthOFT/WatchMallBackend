package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DashboardSummaryVO implements Serializable {

    private List<OrderAdminPageVO> recentOrderList;

    private static final long serialVersionUID = 1L;
}
