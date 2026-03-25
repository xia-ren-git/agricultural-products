package com.agricultural.products.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车实体类
 */
@Data
public class Cart {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private Integer selected;
    private Integer productStatus;
    private Integer productStock;
    private LocalDateTime createTime;
}
