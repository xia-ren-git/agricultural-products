package com.agricultural.products.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品评价实体类
 */
@Data
public class Review {
    private Long id;
    private Long userId;
    private Long productId;
    private Long orderId;
    private Long parentId;
    private Integer rating;
    private String content;
    private String images;
    private Integer likeCount;
    private Integer status;
    private String userName;
    private String userAvatar;
    private String productName;
    private String productImage;
    private LocalDateTime createTime;
    private List<Review> replies;
    private Boolean isLiked;
}
