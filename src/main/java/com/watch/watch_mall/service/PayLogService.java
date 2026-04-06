package com.watch.watch_mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.watch.watch_mall.model.dto.pay.PayLogAdminQueryRequest;
import com.watch.watch_mall.model.entity.PayLog;
import com.watch.watch_mall.model.vo.PayLogAdminDetailVO;
import com.watch.watch_mall.model.vo.PayLogAdminPageVO;

import java.util.List;

public interface PayLogService extends IService<PayLog> {

    Page<PayLogAdminPageVO> pageAdminPayLogs(PayLogAdminQueryRequest queryRequest);

    PayLogAdminDetailVO getAdminPayLogDetail(Long payLogId);

    List<PayLogAdminPageVO> listAdminPayLogsByOrderId(Long orderId);
}
