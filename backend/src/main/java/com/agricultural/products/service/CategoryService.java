package com.agricultural.products.service;

import com.agricultural.products.entity.Category;
import com.agricultural.products.common.PageRequest;
import com.agricultural.products.common.PageResult;
import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {
    
    Category findById(Long id);
    
    List<Category> findAll();
    
    PageResult<Category> findByPage(PageRequest request);
    
    Long count();
    
    boolean save(Category category);
    
    boolean update(Category category);
    
    boolean deleteById(Long id);
}
