package com.happymall.service;

import com.happymall.common.ServiceResponse;

public interface ICategoryService {
    ServiceResponse addCategory(String categoryName, Integer parentId);
}
