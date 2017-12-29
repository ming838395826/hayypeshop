package com.happyshop.service;

import com.github.pagehelper.PageInfo;
import com.happyshop.common.ServerResponse;
import com.happyshop.pojo.Order;
import com.happyshop.vo.OrderVo;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/28.
 */
public interface IOrderService {

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse<String> cancel(Integer userId,Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    ServerResponse pay(Long orderNo,Integer userId,String path);

    ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);

    ServerResponse aliCallback(Map<String,String> params);
}
