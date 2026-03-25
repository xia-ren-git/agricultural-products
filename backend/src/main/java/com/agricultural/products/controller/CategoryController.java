package com.agricultural.products.controller;

import com.agricultural.products.common.PageRequest;
import com.agricultural.products.common.PageResult;
import com.agricultural.products.common.Result;
import com.agricultural.products.entity.Category;
import com.agricultural.products.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 分类控制器 - 农产品分类管理
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.findAll());
    }
    
    @GetMapping("/page")
    public Result<PageResult<Category>> page(PageRequest request) {
        return Result.success(categoryService.findByPage(request));
    }
    
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        return Result.success(categoryService.findById(id));
    }
    
    @PostMapping
    public Result<String> save(@RequestBody Category category) {
        boolean success = categoryService.save(category);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody Category category) {
        boolean success = categoryService.update(category);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = categoryService.deleteById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(categoryService.count());
    }
}
