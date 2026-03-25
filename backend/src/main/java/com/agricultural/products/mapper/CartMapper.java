package com.agricultural.products.mapper;

import com.agricultural.products.entity.Cart;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 购物车Mapper接口
 */
@Mapper
public interface CartMapper {
    
    @Select("SELECT c.*, p.name as product_name, p.image as product_image, p.price, p.status as product_status, p.stock as product_stock FROM cart c LEFT JOIN product p ON c.product_id = p.id WHERE c.id = #{id}")
    Cart findById(Long id);
    
    @Select("SELECT c.*, p.name as product_name, p.image as product_image, p.price, p.status as product_status, p.stock as product_stock FROM cart c LEFT JOIN product p ON c.product_id = p.id WHERE c.user_id = #{userId} ORDER BY c.create_time DESC")
    List<Cart> findByUserId(Long userId);
    
    @Select("SELECT c.*, p.name as product_name, p.image as product_image, p.price, p.status as product_status, p.stock as product_stock FROM cart c LEFT JOIN product p ON c.product_id = p.id WHERE c.user_id = #{userId} AND c.product_id = #{productId}")
    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    @Insert("INSERT INTO cart(user_id, product_id, quantity, selected, create_time) VALUES(#{userId}, #{productId}, #{quantity}, 1, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Cart cart);
    
    @Update("UPDATE cart SET quantity = #{quantity} WHERE id = #{id}")
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    @Update("UPDATE cart SET selected = #{selected} WHERE id = #{id}")
    int updateSelected(@Param("id") Long id, @Param("selected") Integer selected);
    
    @Update("UPDATE cart SET selected = #{selected} WHERE user_id = #{userId}")
    int updateAllSelected(@Param("userId") Long userId, @Param("selected") Integer selected);
    
    @Delete("DELETE FROM cart WHERE id = #{id}")
    int deleteById(Long id);
    
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
    
    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND selected = 1")
    int deleteSelectedByUserId(Long userId);
}
