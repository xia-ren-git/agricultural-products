package com.agricultural.products.mapper;

import com.agricultural.products.entity.Order;
import com.agricultural.products.common.PageRequest;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {
    
    @Select("SELECT o.*, u.username as user_name FROM `order` o LEFT JOIN user u ON o.user_id = u.id WHERE o.id = #{id}")
    Order findById(Long id);
    
    @Select("SELECT o.*, u.username as user_name FROM `order` o LEFT JOIN user u ON o.user_id = u.id WHERE o.order_no = #{orderNo}")
    Order findByOrderNo(String orderNo);
    
    List<Order> findByPage(PageRequest request);
    
    @Select("SELECT COUNT(*) FROM `order`")
    Long count();
    
    @Select("SELECT COUNT(*) FROM `order` WHERE order_no LIKE CONCAT('%', #{keyword}, '%')")
    Long countByKeyword(String keyword);
    
    @Insert("INSERT INTO `order`(order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address, remark, create_time, update_time) VALUES(#{orderNo}, #{userId}, #{totalAmount}, #{status}, #{receiverName}, #{receiverPhone}, #{receiverAddress}, #{remark}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);
    
    int update(Order order);
    
    @Delete("DELETE FROM `order` WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT o.*, u.username as user_name FROM `order` o LEFT JOIN user u ON o.user_id = u.id WHERE o.user_id = #{userId} ORDER BY o.create_time DESC")
    List<Order> findByUserId(Long userId);
    
    @Update("UPDATE `order` SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
