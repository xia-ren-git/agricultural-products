package com.agricultural.products.mapper;

import com.agricultural.products.entity.OrderItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 订单详情Mapper接口
 */
@Mapper
public interface OrderItemMapper {
    
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(Long orderId);
    
    @Insert("INSERT INTO order_item(order_id, product_id, product_name, product_image, price, quantity, subtotal, create_time) VALUES(#{orderId}, #{productId}, #{productName}, #{productImage}, #{price}, #{quantity}, #{subtotal}, NOW())")
    int insert(OrderItem orderItem);
    
    @Delete("DELETE FROM order_item WHERE order_id = #{orderId}")
    int deleteByOrderId(Long orderId);
}
