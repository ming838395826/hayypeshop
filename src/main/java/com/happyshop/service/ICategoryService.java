package com.happyshop.service;

import com.happyshop.common.EasyUiTreeResponse;
import com.happyshop.common.ServerResponse;
import com.happyshop.pojo.Category;

import java.util.List;

/**
 * Created by Administrator on 2017/12/19.
 */
public interface ICategoryService {

    ServerResponse<String> addCategory(String categoryName, Integer parentId);

    ServerResponse<String> updateCategoryName(Integer categoryId,String categoryName);

    List<EasyUiTreeResponse> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
