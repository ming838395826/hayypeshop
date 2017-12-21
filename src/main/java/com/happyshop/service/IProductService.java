package com.happyshop.service;

import com.happyshop.common.ServerResponse;
import com.happyshop.pojo.Product;

/**
 * Created by Administrator on 2017/12/21.
 */
public interface IProductService {

    ServerResponse<String> saveOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(Integer productId,Integer status);

    ServerResponse manageProductDetail(Integer productId);
}
