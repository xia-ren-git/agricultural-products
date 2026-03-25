package com.agricultural.products;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 农产品销售管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.agricultural.products.mapper")
public class AgriculturalProductsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AgriculturalProductsApplication.class, args);
    }
}
