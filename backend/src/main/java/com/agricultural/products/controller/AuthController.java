package com.agricultural.products.controller;

import com.agricultural.products.common.Result;
import com.agricultural.products.entity.User;
import com.agricultural.products.service.UserService;
import com.agricultural.products.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器 - 处理登录注册
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        User existUser = userService.findByUsername(user.getUsername());
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        if (!existUser.getPassword().equals(md5Password)) {
            return Result.error("密码错误");
        }
        if (existUser.getStatus() != 1) {
            return Result.error("账号已被禁用");
        }
        String token = jwtUtils.generateToken(existUser.getId(), existUser.getUsername(), existUser.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", existUser);
        return Result.success("登录成功", data);
    }
    
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        }
        return Result.error("用户名已存在");
    }
    
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!jwtUtils.validateToken(token)) {
            return Result.error(401, "token无效");
        }
        Long userId = jwtUtils.getUserId(token);
        User user = userService.findById(userId);
        user.setPassword(null);
        return Result.success(user);
    }
}
