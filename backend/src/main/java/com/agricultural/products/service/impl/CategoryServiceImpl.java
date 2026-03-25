package com.agricultural.products.service.impl;

import com.agricultural.products.entity.Category;
import com.agricultural.products.mapper.CategoryMapper;
import com.agricultural.products.service.CategoryService;
import com.agricultural.products.common.PageRequest;
import com.agricultural.products.common.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Override
    public Category findById(Long id) {
        return categoryMapper.findById(id);
    }
    
    @Override
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }
    
    @Override
    public PageResult<Category> findByPage(PageRequest request) {
        List<Category> list = categoryMapper.findByPage(request);
        Long total = request.getKeyword() != null && !request.getKeyword().isEmpty() 
            ? categoryMapper.countByKeyword(request.getKeyword()) 
            : categoryMapper.count();
        return new PageResult<>(list, total, request.getPageNum(), request.getPageSize());
    }
    
    @Override
    public Long count() {
        return categoryMapper.count();
    }
    
    @Override
    public boolean save(Category category) {
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        return categoryMapper.insert(category) > 0;
    }
    
    @Override
    public boolean update(Category category) {
        return categoryMapper.update(category) > 0;
    }
    
    @Override
    public boolean deleteById(Long id) {
        return categoryMapper.deleteById(id) > 0;
    }
}
