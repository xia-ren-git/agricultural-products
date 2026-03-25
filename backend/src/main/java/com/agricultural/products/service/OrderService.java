package com.agricultural.products.service;

import com.agricultural.products.entity.Order;
import com.agricultural.products.common.PageRequest;
import com.agricultural.products.common.PageResult;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    Order findById(Long id);
    
    Order findByOrderNo(String orderNo);
    
    PageResult<Order> findByPage(PageRequest request);
    
    Long count();
    
    boolean save(Order order);
    
    boolean update(Order order);
    
    boolean deleteById(Long id);
    
    boolean updateStatus(Long id, Integer status);
    
    Map<String, Object> getStatistics();
}
