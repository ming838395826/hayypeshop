package com.happyshop.service;

import com.happyshop.common.ServerResponse;

/**
 * Created by Administrator on 2017/12/28.
 */
public interface IOrderService {

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse<String> cancel(Integer userId,Long orderNo);
}
