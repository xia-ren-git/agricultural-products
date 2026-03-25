package com.agricultural.products.service.impl;

import com.agricultural.products.entity.Order;
import com.agricultural.products.entity.OrderItem;
import com.agricultural.products.mapper.OrderMapper;
import com.agricultural.products.mapper.OrderItemMapper;
import com.agricultural.products.service.OrderService;
import com.agricultural.products.common.PageRequest;
import com.agricultural.products.common.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Override
    public Order findById(Long id) {
        return orderMapper.findById(id);
    }
    
    @Override
    public Order findByOrderNo(String orderNo) {
        return orderMapper.findByOrderNo(orderNo);
    }
    
    @Override
    public PageResult<Order> findByPage(PageRequest request) {
        List<Order> list = orderMapper.findByPage(request);
        Long total = request.getKeyword() != null && !request.getKeyword().isEmpty() 
            ? orderMapper.countByKeyword(request.getKeyword()) 
            : orderMapper.count();
        return new PageResult<>(list, total, request.getPageNum(), request.getPageSize());
    }
    
    @Override
    public Long count() {
        return orderMapper.count();
    }
    
    @Override
    public boolean save(Order order) {
        order.setOrderNo(generateOrderNo());
        order.setStatus(0);
        return orderMapper.insert(order) > 0;
    }
    
    @Override
    public boolean update(Order order) {
        return orderMapper.update(order) > 0;
    }
    
    @Override
    public boolean deleteById(Long id) {
        orderItemMapper.deleteByOrderId(id);
        return orderMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean updateStatus(Long id, Integer status) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(status);
        return orderMapper.update(order) > 0;
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orderMapper.count());
        return stats;
    }
    
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + new Random().nextInt(1000);
    }
}
