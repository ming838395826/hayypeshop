package com.happyshop.service;

import com.happyshop.common.ServerResponse;
import com.happyshop.vo.CartVo;

/**
 * Created by Administrator on 2017/12/26.
 */
public interface ICartService {

    ServerResponse<CartVo> list (Integer userId);

    ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count);

    ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count);

    ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);

    ServerResponse<CartVo> selectOrUnSelect (Integer userId,Integer productId,Integer checked);
}