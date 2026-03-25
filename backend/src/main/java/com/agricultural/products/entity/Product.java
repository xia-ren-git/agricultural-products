package com.agricultural.products.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 农产品实体类
 */
@Data
public class Product {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String unit;
    private String origin;
    private String image;
    private Integer status;
    private Integer sales;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
