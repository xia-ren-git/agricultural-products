package com.agricultural.products.controller;

import com.agricultural.products.common.PageRequest;
import com.agricultural.products.common.PageResult;
import com.agricultural.products.common.Result;
import com.agricultural.products.entity.User;
import com.agricultural.products.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器 - 用户管理
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/list")
    public Result<PageResult<User>> list(PageRequest request) {
        PageResult<User> result = userService.findByPage(request);
        result.getList().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
    
    @PostMapping
    public Result<String> save(@RequestBody User user) {
        boolean success = userService.register(user);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody User user) {
        boolean success = userService.update(user);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = userService.deleteById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    @PutMapping("/status/{id}/{status}")
    public Result<String> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        boolean success = userService.updateStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
    
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(userService.count());
    }
    
    @PutMapping("/info")
    public Result<String> updateInfo(@RequestBody User user) {
        boolean success = userService.updateInfo(user);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }
    
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody PasswordRequest request) {
        boolean success = userService.updatePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword());
        return success ? Result.success("密码修改成功") : Result.error("原密码错误");
    }
    
    public static class PasswordRequest {
        private Long userId;
        private String oldPassword;
        private String newPassword;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
