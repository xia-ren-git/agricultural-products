package com.agricultural.products.controller;

import com.agricultural.products.common.PageRequest;
import com.agricultural.products.common.PageResult;
import com.agricultural.products.common.Result;
import com.agricultural.products.entity.Product;
import com.agricultural.products.mapper.ProductMapper;
import com.agricultural.products.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品控制器 - 农产品管理
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductMapper productMapper;
    
    @GetMapping("/hot")
    public Result<List<Product>> hotProducts(@RequestParam(defaultValue = "8") int limit) {
        return Result.success(productService.findHotProducts(limit));
    }
    
    @GetMapping("/category/{categoryId}")
    public Result<List<Product>> byCategory(@PathVariable Long categoryId) {
        return Result.success(productService.findByCategoryId(categoryId));
    }
    
    @GetMapping("/page")
    public Result<PageResult<Product>> page(PageRequest request) {
        return Result.success(productService.findByPage(request));
    }
    
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        return Result.success(productService.findById(id));
    }
    
    @PostMapping
    public Result<String> save(@RequestBody Product product) {
        boolean success = productService.save(product);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody Product product) {
        boolean success = productService.update(product);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = productService.deleteById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(productService.count());
    }
    
    @GetMapping("/search")
    public Result<Map<String, Object>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "create_time") String orderBy,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "12") int pageSize) {
        
        int offset = (pageNum - 1) * pageSize;
        List<Product> products = productMapper.searchProducts(keyword, categoryId, minPrice, maxPrice, orderBy, pageSize, offset);
        Long total = productMapper.countSearchProducts(keyword, categoryId, minPrice, maxPrice);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", products);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return Result.success(result);
    }
}
