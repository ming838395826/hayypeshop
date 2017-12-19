package com.happyshop.service;

import com.happyshop.common.ServerResponse;

/**
 * Created by Administrator on 2017/12/19.
 */
public interface ICategoryService {

    ServerResponse<String> addCategory(String categoryName, Integer parentId);

    ServerResponse<String> updateCategoryName(Integer categoryId,String categoryName);
}
