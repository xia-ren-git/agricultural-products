package com.agricultural.products.controller;

import com.agricultural.products.common.Result;
import com.agricultural.products.mapper.UserMapper;
import com.agricultural.products.mapper.ProductMapper;
import com.agricultural.products.mapper.OrderMapper;
import com.agricultural.products.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 统计控制器 - 首页数据统计
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userMapper.count());
        data.put("productCount", productMapper.count());
        data.put("orderCount", orderMapper.count());
        data.put("categoryCount", categoryMapper.count());
        return Result.success(data);
    }
    
    @GetMapping("/sales-trend")
    public Result<List<Map<String, Object>>> salesTrend() {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        Random random = new Random();
        for (String day : days) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", day);
            item.put("value", random.nextInt(1000) + 500);
            list.add(item);
        }
        return Result.success(list);
    }
    
    @GetMapping("/category-distribution")
    public Result<List<Map<String, Object>>> categoryDistribution() {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] categories = {"蔬菜类", "水果类", "粮食类", "禽蛋类", "水产类", "肉类"};
        Random random = new Random();
        for (String category : categories) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", category);
            item.put("value", random.nextInt(300) + 100);
            list.add(item);
        }
        return Result.success(list);
    }
    
    @GetMapping("/order-status")
    public Result<List<Map<String, Object>>> orderStatus() {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] statuses = {"待付款", "待发货", "待收货", "已完成", "已取消"};
        String[] colors = {"#409EFF", "#E6A23C", "#67C23A", "#909399", "#F56C6C"};
        Random random = new Random();
        for (int i = 0; i < statuses.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", statuses[i]);
            item.put("value", random.nextInt(50) + 10);
            item.put("color", colors[i]);
            list.add(item);
        }
        return Result.success(list);
    }
    
    @GetMapping("/user-growth")
    public Result<List<Map<String, Object>>> userGrowth() {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
        Random random = new Random();
        int cumulative = 100;
        for (String month : months) {
            int newUsers = random.nextInt(50) + 20;
            cumulative += newUsers;
            Map<String, Object> item = new HashMap<>();
            item.put("month", month);
            item.put("newUsers", newUsers);
            item.put("totalUsers", cumulative);
            list.add(item);
        }
        return Result.success(list);
    }
    
    @GetMapping("/hot-products")
    public Result<List<Map<String, Object>>> hotProducts() {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] products = {"有机西红柿", "新鲜草莓", "东北大米", "土鸡蛋", "淡水鱼", "黑猪肉"};
        Random random = new Random();
        for (String product : products) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", product);
            item.put("sales", random.nextInt(500) + 100);
            list.add(item);
        }
        return Result.success(list);
    }
    
    @GetMapping("/revenue")
    public Result<Map<String, Object>> revenue() {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random();
        data.put("todayRevenue", random.nextInt(10000) + 5000);
        data.put("weekRevenue", random.nextInt(50000) + 30000);
        data.put("monthRevenue", random.nextInt(200000) + 100000);
        data.put("yearRevenue", random.nextInt(2000000) + 1000000);
        return Result.success(data);
    }
}
