package com.agricultural.products.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品收藏实体类
 */
@Data
public class Favorite {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productImage;
    private java.math.BigDecimal price;
    private Integer productStatus;
    private LocalDateTime createTime;
}
