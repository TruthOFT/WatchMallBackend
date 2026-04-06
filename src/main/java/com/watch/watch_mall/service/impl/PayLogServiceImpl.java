package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.exception.BusinessException;
import com.watch.watch_mall.mapper.OrderMapper;
import com.watch.watch_mall.mapper.PayLogMapper;
import com.watch.watch_mall.model.dto.pay.PayLogAdminQueryRequest;
import com.watch.watch_mall.model.entity.Order;
import com.watch.watch_mall.model.entity.PayLog;
import com.watch.watch_mall.model.vo.PayLogAdminDetailVO;
import com.watch.watch_mall.model.vo.PayLogAdminPageVO;
import com.watch.watch_mall.service.PayLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements PayLogService {

    @Resource
    private PayLogMapper payLogMapper;

    @Resource
    private OrderMapper orderMapper;

    @Override
    public Page<PayLogAdminPageVO> pageAdminPayLogs(PayLogAdminQueryRequest queryRequest) {
        PayLogAdminQueryRequest validQuery = queryRequest == null ? new PayLogAdminQueryRequest() : queryRequest;
        String keyword = StringUtils.trimToNull(validQuery.getKeyword());
        Date payTimeStart = parseDateTime(validQuery.getPayTimeStart());
        Date payTimeEnd = parseDateTime(validQuery.getPayTimeEnd());
        List<Long> orderIds = findOrderIdsByKeyword(keyword);
        Page<PayLog> page = new Page<>(validQuery.getCurrent(), validQuery.getPageSize());
        Page<PayLog> payLogPage = payLogMapper.selectPage(page, Wrappers.lambdaQuery(PayLog.class)
                .and(keyword != null, wrapper -> wrapper
                        .like(PayLog::getPayNo, keyword)
                        .or(!orderIds.isEmpty())
                        .in(!orderIds.isEmpty(), PayLog::getOrderId, orderIds))
                .eq(validQuery.getUserId() != null, PayLog::getUserId, validQuery.getUserId())
                .eq(validQuery.getPayStatus() != null, PayLog::getPayStatus, validQuery.getPayStatus())
                .ge(payTimeStart != null, PayLog::getPayTime, payTimeStart)
                .le(payTimeEnd != null, PayLog::getPayTime, payTimeEnd)
                .eq(PayLog::getIsDelete, 0)
                .orderByDesc(PayLog::getPayTime)
                .orderByDesc(PayLog::getId));
        Page<PayLogAdminPageVO> resultPage = new Page<>(payLogPage.getCurrent(), payLogPage.getSize(), payLogPage.getTotal());
        resultPage.setRecords(buildPayLogPageVOList(payLogPage.getRecords()));
        return resultPage;
    }

    @Override
    public PayLogAdminDetailVO getAdminPayLogDetail(Long payLogId) {
        if (payLogId == null || payLogId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PayLog payLog = payLogMapper.selectOne(Wrappers.lambdaQuery(PayLog.class)
                .eq(PayLog::getId, payLogId)
                .eq(PayLog::getIsDelete, 0)
                .last("limit 1"));
        if (payLog == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "pay log not found");
        }
        Map<Long, String> orderNoMap = getOrderNoMap(Collections.singletonList(payLog.getOrderId()));
        PayLogAdminDetailVO detailVO = new PayLogAdminDetailVO();
        BeanUtils.copyProperties(payLog, detailVO);
        detailVO.setOrderNo(orderNoMap.get(payLog.getOrderId()));
        return detailVO;
    }

    @Override
    public List<PayLogAdminPageVO> listAdminPayLogsByOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return Collections.emptyList();
        }
        List<PayLog> payLogs = payLogMapper.selectList(Wrappers.lambdaQuery(PayLog.class)
                .eq(PayLog::getOrderId, orderId)
                .eq(PayLog::getIsDelete, 0)
                .orderByDesc(PayLog::getPayTime)
                .orderByDesc(PayLog::getId));
        return buildPayLogPageVOList(payLogs);
    }

    private List<PayLogAdminPageVO> buildPayLogPageVOList(List<PayLog> payLogs) {
        if (payLogs == null || payLogs.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, String> orderNoMap = getOrderNoMap(payLogs.stream().map(PayLog::getOrderId).distinct().toList());
        return payLogs.stream().map(payLog -> {
            PayLogAdminPageVO pageVO = new PayLogAdminPageVO();
            BeanUtils.copyProperties(payLog, pageVO);
            pageVO.setOrderNo(orderNoMap.get(payLog.getOrderId()));
            return pageVO;
        }).toList();
    }

    private Map<Long, String> getOrderNoMap(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return orderMapper.selectList(Wrappers.lambdaQuery(Order.class)
                        .in(Order::getId, orderIds)
                        .eq(Order::getIsDelete, 0))
                .stream()
                .collect(Collectors.toMap(Order::getId, Order::getOrderNo, (left, right) -> left));
    }

    private List<Long> findOrderIdsByKeyword(String keyword) {
        if (keyword == null) {
            return Collections.emptyList();
        }
        return orderMapper.selectList(Wrappers.lambdaQuery(Order.class)
                        .like(Order::getOrderNo, keyword)
                        .eq(Order::getIsDelete, 0)
                        .select(Order::getId))
                .stream()
                .map(Order::getId)
                .toList();
    }

    private Date parseDateTime(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.trim());
        } catch (ParseException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "invalid payTime range");
        }
    }
}
