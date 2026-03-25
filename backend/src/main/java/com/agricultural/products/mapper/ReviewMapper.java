package com.agricultural.products.mapper;

import com.agricultural.products.entity.Review;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ReviewMapper {
    
    @Select("SELECT r.*, u.nickname as user_name, u.avatar as user_avatar, p.name as product_name, p.image as product_image FROM review r LEFT JOIN user u ON r.user_id = u.id LEFT JOIN product p ON r.product_id = p.id WHERE r.id = #{id}")
    Review findById(Long id);
    
    @Select("SELECT r.*, u.nickname as user_name, u.avatar as user_avatar, p.name as product_name, p.image as product_image FROM review r LEFT JOIN user u ON r.user_id = u.id LEFT JOIN product p ON r.product_id = p.id WHERE r.product_id = #{productId} AND r.status = 1 AND r.parent_id IS NULL ORDER BY r.create_time DESC")
    List<Review> findByProductId(Long productId);
    
    @Select("SELECT r.*, u.nickname as user_name, u.avatar as user_avatar FROM review r LEFT JOIN user u ON r.user_id = u.id WHERE r.parent_id = #{parentId} AND r.status = 1 ORDER BY r.create_time ASC")
    List<Review> findRepliesByParentId(Long parentId);
    
    @Select("SELECT r.*, u.nickname as user_name, u.avatar as user_avatar, p.name as product_name, p.image as product_image FROM review r LEFT JOIN user u ON r.user_id = u.id LEFT JOIN product p ON r.product_id = p.id WHERE r.user_id = #{userId} ORDER BY r.create_time DESC")
    List<Review> findByUserId(Long userId);
    
    @Select("SELECT AVG(rating) FROM review WHERE product_id = #{productId} AND status = 1")
    Double getAverageRating(Long productId);
    
    @Select("SELECT COUNT(*) FROM review WHERE product_id = #{productId} AND status = 1")
    int countByProductId(Long productId);
    
    @Insert("INSERT INTO review(user_id, product_id, order_id, parent_id, rating, content, images, like_count, status, create_time) VALUES(#{userId}, #{productId}, #{orderId}, #{parentId}, #{rating}, #{content}, #{images}, 0, 1, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Review review);
    
    @Update("UPDATE review SET rating = #{rating}, content = #{content}, images = #{images} WHERE id = #{id}")
    int updateContent(@Param("id") Long id, @Param("rating") Integer rating, @Param("content") String content, @Param("images") String images);
    
    @Update("UPDATE review SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    @Update("UPDATE review SET like_count = like_count + #{delta} WHERE id = #{id}")
    int updateLikeCount(@Param("id") Long id, @Param("delta") int delta);
    
    @Delete("DELETE FROM review WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT COUNT(*) FROM review_like WHERE review_id = #{reviewId} AND user_id = #{userId}")
    int checkUserLiked(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
    
    @Insert("INSERT INTO review_like(review_id, user_id, create_time) VALUES(#{reviewId}, #{userId}, NOW())")
    int insertLike(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
    
    @Delete("DELETE FROM review_like WHERE review_id = #{reviewId} AND user_id = #{userId}")
    int deleteLike(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
}
