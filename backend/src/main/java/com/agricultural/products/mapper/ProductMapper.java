package com.agricultural.products.mapper;

import com.agricultural.products.entity.Product;
import com.agricultural.products.common.PageRequest;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 产品Mapper接口
 */
@Mapper
public interface ProductMapper {
    
    @Select("SELECT p.*, c.name as category_name FROM product p LEFT JOIN category c ON p.category_id = c.id WHERE p.id = #{id}")
    Product findById(Long id);
    
    @Select("SELECT p.*, c.name as category_name FROM product p LEFT JOIN category c ON p.category_id = c.id WHERE p.status = 1 ORDER BY p.sales DESC LIMIT #{limit}")
    List<Product> findHotProducts(int limit);
    
    @Select("SELECT p.*, c.name as category_name FROM product p LEFT JOIN category c ON p.category_id = c.id WHERE p.category_id = #{categoryId} AND p.status = 1")
    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> findByPage(PageRequest request);
    
    @Select("SELECT COUNT(*) FROM product")
    Long count();
    
    @Select("SELECT COUNT(*) FROM product WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    Long countByKeyword(String keyword);
    
    @Insert("INSERT INTO product(name, category_id, description, price, stock, unit, origin, image, status, sales, create_time, update_time) VALUES(#{name}, #{categoryId}, #{description}, #{price}, #{stock}, #{unit}, #{origin}, #{image}, #{status}, #{sales}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);
    
    int update(Product product);
    
    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(Long id);
    
    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    @Update("UPDATE product SET sales = sales + #{quantity} WHERE id = #{productId}")
    int increaseSales(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    List<Product> searchProducts(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, 
                                  @Param("minPrice") java.math.BigDecimal minPrice, @Param("maxPrice") java.math.BigDecimal maxPrice,
                                  @Param("orderBy") String orderBy, @Param("pageSize") int pageSize, @Param("offset") int offset);
    
    Long countSearchProducts(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, 
                              @Param("minPrice") java.math.BigDecimal minPrice, @Param("maxPrice") java.math.BigDecimal maxPrice);
}
