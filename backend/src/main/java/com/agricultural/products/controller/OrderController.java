package com.agricultural.products.controller;

import com.agricultural.products.common.PageRequest;
import com.agricultural.products.common.PageResult;
import com.agricultural.products.common.Result;
import com.agricultural.products.entity.Cart;
import com.agricultural.products.entity.Order;
import com.agricultural.products.entity.OrderItem;
import com.agricultural.products.mapper.CartMapper;
import com.agricultural.products.mapper.OrderItemMapper;
import com.agricultural.products.mapper.OrderMapper;
import com.agricultural.products.mapper.ProductMapper;
import com.agricultural.products.service.OrderService;
import com.agricultural.products.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单控制器 - 订单管理
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private JwtUtils jwtUtils;

    private Long getUserIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = jwtUtils.parseToken(token);
        return claims.get("userId", Long.class);
    }
    
    @GetMapping("/page")
    public Result<PageResult<Order>> page(PageRequest request) {
        return Result.success(orderService.findByPage(request));
    }
    
    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        List<OrderItem> items = orderItemMapper.findByOrderId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        return Result.success(order);
    }
    
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        Order order = orderService.findById(id);
        List<OrderItem> items = orderItemMapper.findByOrderId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        return Result.success(result);
    }
    
    @PostMapping
    public Result<String> save(@RequestBody Order order) {
        boolean success = orderService.save(order);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody Order order) {
        boolean success = orderService.update(order);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = orderService.deleteById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    @PutMapping("/status/{id}/{status}")
    public Result<String> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        boolean success = orderService.updateStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
    
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(orderService.count());
    }
    
    @GetMapping("/user")
    public Result<List<Order>> getUserOrders(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        List<Order> orders = orderMapper.findByUserId(userId);
        return Result.success(orders);
    }
    
    @PostMapping("/create")
    @Transactional
    public Result<Order> createOrder(@RequestHeader("Authorization") String token, @RequestBody Order order) {
        Long userId = getUserIdFromToken(token);
        
        List<Cart> cartList = cartMapper.findByUserId(userId);
        List<Cart> selectedCart = cartList.stream().filter(c -> c.getSelected() != null && c.getSelected() == 1).toList();
        
        if (selectedCart.isEmpty()) {
            return Result.error("请选择要购买的商品");
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : selectedCart) {
            if (cart.getProductStatus() != 1) {
                return Result.error("商品 " + cart.getProductName() + " 已下架");
            }
            if (cart.getQuantity() > cart.getProductStock()) {
                return Result.error("商品 " + cart.getProductName() + " 库存不足");
            }
            totalAmount = totalAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getQuantity())));
        }
        
        String orderNo = "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        
        orderMapper.insert(order);
        
        for (Cart cart : selectedCart) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(cart.getProductId());
            item.setProductName(cart.getProductName());
            item.setProductImage(cart.getProductImage());
            item.setPrice(cart.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(cart.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            orderItemMapper.insert(item);
            
            productMapper.decreaseStock(cart.getProductId(), cart.getQuantity());
            productMapper.increaseSales(cart.getProductId(), cart.getQuantity());
        }
        
        cartMapper.deleteSelectedByUserId(userId);
        
        return Result.success(order);
    }
    
    @PutMapping("/pay/{id}")
    public Result<String> payOrder(@PathVariable Long id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("订单状态不正确");
        }
        orderMapper.updateStatus(id, 1);
        return Result.success("支付成功");
    }
    
    @PutMapping("/cancel/{id}")
    public Result<String> cancelOrder(@PathVariable Long id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("只能取消待付款订单");
        }
        orderMapper.updateStatus(id, 4);
        
        List<OrderItem> items = orderItemMapper.findByOrderId(id);
        for (OrderItem item : items) {
            productMapper.decreaseStock(item.getProductId(), -item.getQuantity());
        }
        
        return Result.success("订单已取消");
    }
    
    @PutMapping("/confirm/{id}")
    public Result<String> confirmOrder(@PathVariable Long id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 2) {
            return Result.error("只能确认待收货订单");
        }
        orderMapper.updateStatus(id, 3);
        return Result.success("确认收货成功");
    }
}
