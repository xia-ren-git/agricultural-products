package com.agricultural.products.controller;

import com.agricultural.products.common.Result;
import com.agricultural.products.entity.Favorite;
import com.agricultural.products.mapper.FavoriteMapper;
import com.agricultural.products.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Claims;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private JwtUtils jwtUtils;

    private Long getUserIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = jwtUtils.parseToken(token);
        return claims.get("userId", Long.class);
    }

    @GetMapping("/list")
    public Result<List<Favorite>> list(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        List<Favorite> favoriteList = favoriteMapper.findByUserId(userId);
        return Result.success(favoriteList);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestHeader("Authorization") String token, @RequestBody Favorite favorite) {
        Long userId = getUserIdFromToken(token);
        Favorite existFavorite = favoriteMapper.findByUserIdAndProductId(userId, favorite.getProductId());
        
        if (existFavorite == null) {
            favorite.setUserId(userId);
            favoriteMapper.insert(favorite);
        }
        return Result.success();
    }

    @DeleteMapping("/{productId}")
    public Result<Void> delete(@RequestHeader("Authorization") String token, @PathVariable Long productId) {
        Long userId = getUserIdFromToken(token);
        favoriteMapper.deleteByUserIdAndProductId(userId, productId);
        return Result.success();
    }

    @GetMapping("/check/{productId}")
    public Result<Map<String, Object>> check(@RequestHeader("Authorization") String token, @PathVariable Long productId) {
        Long userId = getUserIdFromToken(token);
        Favorite favorite = favoriteMapper.findByUserIdAndProductId(userId, productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isFavorite", favorite != null);
        if (favorite != null) {
            result.put("favoriteId", favorite.getId());
        }
        return Result.success(result);
    }

    @GetMapping("/count/{productId}")
    public Result<Integer> count(@PathVariable Long productId) {
        int count = favoriteMapper.countByProductId(productId);
        return Result.success(count);
    }
}
