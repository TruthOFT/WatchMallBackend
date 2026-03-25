package com.watch.watch_mall.service;

import com.watch.watch_mall.model.dto.order.MockPayRequest;

public interface PaymentService {

    boolean mockPay(Long userId, MockPayRequest request);
}
