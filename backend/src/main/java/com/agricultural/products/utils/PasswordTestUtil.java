package com.agricultural.products.utils;

/**
 * 密码加密测试工具类
 * 用于生成测试密码和验证加密功能
 */
public class PasswordTestUtil {
    
    public static void main(String[] args) {
        System.out.println("========== 密码加密测试工具 ==========\n");
        
        String[] testPasswords = {"admin", "admin", "password", "test123"};
        
        System.out.println("【密码加密结果】");
        System.out.println("----------------------------------------");
        for (String password : testPasswords) {
            String encoded = PasswordUtils.encode(password);
            System.out.println("原始密码: " + password);
            System.out.println("加密结果: " + encoded);
            System.out.println("----------------------------------------");
        }
        
        System.out.println("\n【密码验证测试】");
        String testPwd = "admin";
        String encodedPwd = PasswordUtils.encode(testPwd);
        boolean matchResult = PasswordUtils.matches(testPwd, encodedPwd);
        boolean wrongResult = PasswordUtils.matches("wrongpassword", encodedPwd);
        
        System.out.println("测试密码: " + testPwd);
        System.out.println("加密结果: " + encodedPwd);
        System.out.println("正确密码验证: " + (matchResult ? "✓ 通过" : "✗ 失败"));
        System.out.println("错误密码验证: " + (!wrongResult ? "✓ 通过" : "✗ 失败"));
        
        System.out.println("\n【生成数据库初始化密码】");
        System.out.println("-- 管理员密码 admin 加密后:");
        System.out.println("UPDATE user SET password = '" + PasswordUtils.encode("admin") + "' WHERE username = 'admin';");
        System.out.println("\n-- 普通用户密码 123456 加密后:");
        System.out.println("UPDATE user SET password = '" + PasswordUtils.encode("123456") + "' WHERE username = 'user1';");
        
        System.out.println("\n========== 测试完成 ==========");
    }
}
