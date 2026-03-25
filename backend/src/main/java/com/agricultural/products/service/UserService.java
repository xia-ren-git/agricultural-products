package com.agricultural.products.service;

import com.agricultural.products.entity.User;
import com.agricultural.products.common.PageRequest;
import com.agricultural.products.common.PageResult;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    User findById(Long id);
    
    User findByUsername(String username);
    
    PageResult<User> findByPage(PageRequest request);
    
    Long count();
    
    boolean register(User user);
    
    boolean update(User user);
    
    boolean deleteById(Long id);
    
    boolean updateStatus(Long id, Integer status);
    
    boolean updateInfo(User user);
    
    boolean updatePassword(Long id, String oldPassword, String newPassword);
}
