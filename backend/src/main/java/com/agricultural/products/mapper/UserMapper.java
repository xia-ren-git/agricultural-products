package com.agricultural.products.mapper;

import com.agricultural.products.entity.User;
import com.agricultural.products.common.PageRequest;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);
    
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);
    
    List<User> findByPage(PageRequest request);
    
    @Select("SELECT COUNT(*) FROM user")
    Long count();
    
    @Select("SELECT COUNT(*) FROM user WHERE username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%')")
    Long countByKeyword(String keyword);
    
    @Insert("INSERT INTO user(username, password, nickname, phone, email, avatar, role, status, create_time, update_time) VALUES(#{username}, #{password}, #{nickname}, #{phone}, #{email}, #{avatar}, #{role}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    int update(User user);
    
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);
}
