package com.happyshop.service;

import com.github.pagehelper.PageInfo;
import com.happyshop.common.ServerResponse;
import com.happyshop.pojo.Shipping;

/**
 * Created by Administrator on 2017/12/27.
 */
public interface IShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse<String> del(Integer userId,Integer shippingId);

    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse<Shipping> select(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
