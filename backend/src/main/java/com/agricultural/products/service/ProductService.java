package com.agricultural.products.service;

import com.agricultural.products.entity.Product;
import com.agricultural.products.common.PageRequest;
import com.agricultural.products.common.PageResult;
import java.util.List;

/**
 * 产品服务接口
 */
public interface ProductService {
    
    Product findById(Long id);
    
    List<Product> findHotProducts(int limit);
    
    List<Product> findByCategoryId(Long categoryId);
    
    PageResult<Product> findByPage(PageRequest request);
    
    Long count();
    
    boolean save(Product product);
    
    boolean update(Product product);
    
    boolean deleteById(Long id);
}
