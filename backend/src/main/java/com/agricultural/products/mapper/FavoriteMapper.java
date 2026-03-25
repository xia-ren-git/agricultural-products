package com.agricultural.products.mapper;

import com.agricultural.products.entity.Favorite;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface FavoriteMapper {
    
    @Select("SELECT f.*, p.name as product_name, p.image as product_image, p.price, p.status as product_status FROM favorite f LEFT JOIN product p ON f.product_id = p.id WHERE f.id = #{id}")
    Favorite findById(Long id);
    
    @Select("SELECT f.*, p.name as product_name, p.image as product_image, p.price, p.status as product_status FROM favorite f LEFT JOIN product p ON f.product_id = p.id WHERE f.user_id = #{userId} ORDER BY f.create_time DESC")
    List<Favorite> findByUserId(Long userId);
    
    @Select("SELECT f.*, p.name as product_name, p.image as product_image, p.price, p.status as product_status FROM favorite f LEFT JOIN product p ON f.product_id = p.id WHERE f.user_id = #{userId} AND f.product_id = #{productId}")
    Favorite findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    @Select("SELECT COUNT(*) FROM favorite WHERE product_id = #{productId}")
    int countByProductId(Long productId);
    
    @Insert("INSERT INTO favorite(user_id, product_id, create_time) VALUES(#{userId}, #{productId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Favorite favorite);
    
    @Delete("DELETE FROM favorite WHERE id = #{id}")
    int deleteById(Long id);
    
    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
