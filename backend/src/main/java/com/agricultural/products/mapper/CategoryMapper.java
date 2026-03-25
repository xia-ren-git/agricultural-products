package com.agricultural.products.mapper;

import com.agricultural.products.entity.Category;
import com.agricultural.products.common.PageRequest;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 分类Mapper接口
 */
@Mapper
public interface CategoryMapper {
    
    @Select("SELECT * FROM category WHERE id = #{id}")
    Category findById(Long id);
    
    @Select("SELECT * FROM category WHERE status = 1 ORDER BY sort ASC")
    List<Category> findAll();
    
    List<Category> findByPage(PageRequest request);
    
    @Select("SELECT COUNT(*) FROM category")
    Long count();
    
    @Select("SELECT COUNT(*) FROM category WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    Long countByKeyword(String keyword);
    
    @Insert("INSERT INTO category(name, description, sort, status, create_time, update_time) VALUES(#{name}, #{description}, #{sort}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);
    
    int update(Category category);
    
    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteById(Long id);
}
