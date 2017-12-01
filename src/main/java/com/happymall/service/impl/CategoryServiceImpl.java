package com.happymall.service.impl;

import com.happymall.common.ServiceResponse;
import com.happymall.dao.CategoryMapper;
import com.happymall.pojo.Category;
import com.happymall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ICategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    public ServiceResponse addCategory(String categoryName,Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServiceResponse.creatByErrorMessage("添加品类参数错误!");
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        int row = categoryMapper.insert(category);
        if(row > 0){
         return ServiceResponse.creatBysuccessMessage("添加品类成功");
        }
        return ServiceResponse.creatBysuccessMessage("添加品类失败");
    }

    public ServiceResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName) ){
            return ServiceResponse.creatByErrorMessage("更新品类参数错误!");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int row = categoryMapper.updateByPrimaryKeySelective(category);
        if(row > 0){
            return ServiceResponse.creatBysuccessMessage("更新品类成功");
        }
        return ServiceResponse.creatByErrorMessage("更新品类失败");
    }
}
