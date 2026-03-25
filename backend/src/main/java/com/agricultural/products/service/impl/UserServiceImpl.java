package com.agricultural.products.service.impl;

import com.agricultural.products.entity.User;
import com.agricultural.products.mapper.UserMapper;
import com.agricultural.products.service.UserService;
import com.agricultural.products.common.PageRequest;
import com.agricultural.products.common.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }
    
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Override
    public PageResult<User> findByPage(PageRequest request) {
        List<User> list = userMapper.findByPage(request);
        Long total = request.getKeyword() != null && !request.getKeyword().isEmpty() 
            ? userMapper.countByKeyword(request.getKeyword()) 
            : userMapper.count();
        return new PageResult<>(list, total, request.getPageNum(), request.getPageSize());
    }
    
    @Override
    public Long count() {
        return userMapper.count();
    }
    
    @Override
    public boolean register(User user) {
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            return false;
        }
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setRole(0);
        user.setStatus(1);
        return userMapper.insert(user) > 0;
    }
    
    @Override
    public boolean update(User user) {
        return userMapper.update(user) > 0;
    }
    
    @Override
    public boolean deleteById(Long id) {
        return userMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean updateStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        return userMapper.update(user) > 0;
    }
    
    @Override
    public boolean updateInfo(User user) {
        return userMapper.update(user) > 0;
    }
    
    @Override
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userMapper.findById(id);
        if (user == null) {
            return false;
        }
        String oldMd5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!user.getPassword().equals(oldMd5)) {
            return false;
        }
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        return userMapper.update(updateUser) > 0;
    }
}
