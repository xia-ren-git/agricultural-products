package com.agricultural.products.service.impl;

import com.agricultural.products.entity.Product;
import com.agricultural.products.mapper.ProductMapper;
import com.agricultural.products.service.ProductService;
import com.agricultural.products.common.PageRequest;
import com.agricultural.products.common.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 产品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public Product findById(Long id) {
        return productMapper.findById(id);
    }
    
    @Override
    public List<Product> findHotProducts(int limit) {
        return productMapper.findHotProducts(limit);
    }
    
    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }
    
    @Override
    public PageResult<Product> findByPage(PageRequest request) {
        List<Product> list = productMapper.findByPage(request);
        Long total = request.getKeyword() != null && !request.getKeyword().isEmpty() 
            ? productMapper.countByKeyword(request.getKeyword()) 
            : productMapper.count();
        return new PageResult<>(list, total, request.getPageNum(), request.getPageSize());
    }
    
    @Override
    public Long count() {
        return productMapper.count();
    }
    
    @Override
    public boolean save(Product product) {
        if (product.getSales() == null) {
            product.setSales(0);
        }
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        return productMapper.insert(product) > 0;
    }
    
    @Override
    public boolean update(Product product) {
        return productMapper.update(product) > 0;
    }
    
    @Override
    public boolean deleteById(Long id) {
        return productMapper.deleteById(id) > 0;
    }
}
