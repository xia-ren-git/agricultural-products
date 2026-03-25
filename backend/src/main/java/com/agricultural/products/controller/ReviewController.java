package com.agricultural.products.controller;

import com.agricultural.products.common.PageResult;
import com.agricultural.products.common.Result;
import com.agricultural.products.entity.Review;
import com.agricultural.products.mapper.ReviewMapper;
import com.agricultural.products.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Claims;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private JwtUtils jwtUtils;

    private Long getUserIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = jwtUtils.parseToken(token);
        return claims.get("userId", Long.class);
    }

    @GetMapping("/product/{productId}")
    public Result<List<Review>> listByProduct(
            @PathVariable Long productId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        List<Review> reviewList = reviewMapper.findByProductId(productId);
        
        Long userId = null;
        if (token != null && !token.isEmpty()) {
            try {
                userId = getUserIdFromToken(token);
            } catch (Exception e) {
                // ignore
            }
        }
        
        final Long finalUserId = userId;
        for (Review review : reviewList) {
            List<Review> replies = reviewMapper.findRepliesByParentId(review.getId());
            review.setReplies(replies);
            
            if (finalUserId != null) {
                review.setIsLiked(reviewMapper.checkUserLiked(review.getId(), finalUserId) > 0);
            } else {
                review.setIsLiked(false);
            }
            
            for (Review reply : replies) {
                if (finalUserId != null) {
                    reply.setIsLiked(reviewMapper.checkUserLiked(reply.getId(), finalUserId) > 0);
                } else {
                    reply.setIsLiked(false);
                }
            }
        }
        
        return Result.success(reviewList);
    }

    @GetMapping("/user")
    public Result<List<Review>> listByUser(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        List<Review> reviewList = reviewMapper.findByUserId(userId);
        return Result.success(reviewList);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestHeader("Authorization") String token, @RequestBody Review review) {
        Long userId = getUserIdFromToken(token);
        review.setUserId(userId);
        
        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            return Result.error("评价内容不能为空");
        }
        if (review.getContent().length() > 500) {
            return Result.error("评价内容不能超过500字");
        }
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            return Result.error("评分必须在1-5之间");
        }
        
        reviewMapper.insert(review);
        return Result.success();
    }

    @PostMapping("/reply")
    public Result<Void> reply(@RequestHeader("Authorization") String token, @RequestBody Review review) {
        Long userId = getUserIdFromToken(token);
        
        if (review.getParentId() == null) {
            return Result.error("回复的评论ID不能为空");
        }
        
        Review parentReview = reviewMapper.findById(review.getParentId());
        if (parentReview == null) {
            return Result.error("原评论不存在");
        }
        
        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            return Result.error("回复内容不能为空");
        }
        if (review.getContent().length() > 500) {
            return Result.error("回复内容不能超过500字");
        }
        
        review.setUserId(userId);
        review.setProductId(parentReview.getProductId());
        review.setRating(0);
        reviewMapper.insert(review);
        
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Review review) {
        Long userId = getUserIdFromToken(token);
        Review existReview = reviewMapper.findById(id);
        if (existReview == null) {
            return Result.error("评论不存在");
        }
        if (!existReview.getUserId().equals(userId)) {
            return Result.error("无权修改此评论");
        }
        
        if (review.getContent() != null && review.getContent().length() > 500) {
            return Result.error("评价内容不能超过500字");
        }
        if (review.getRating() != null && (review.getRating() < 1 || review.getRating() > 5)) {
            return Result.error("评分必须在1-5之间");
        }
        
        reviewMapper.updateContent(id, review.getRating(), review.getContent(), review.getImages());
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        reviewMapper.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = getUserIdFromToken(token);
        Review existReview = reviewMapper.findById(id);
        if (existReview == null) {
            return Result.error("评论不存在");
        }
        if (!existReview.getUserId().equals(userId)) {
            return Result.error("无权删除此评论");
        }
        reviewMapper.deleteById(id);
        return Result.success();
    }

    @PostMapping("/like/{id}")
    @Transactional
    public Result<Map<String, Object>> toggleLike(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = getUserIdFromToken(token);
        
        Review review = reviewMapper.findById(id);
        if (review == null) {
            return Result.error("评论不存在");
        }
        
        int isLiked = reviewMapper.checkUserLiked(id, userId);
        Map<String, Object> result = new HashMap<>();
        
        if (isLiked > 0) {
            reviewMapper.deleteLike(id, userId);
            reviewMapper.updateLikeCount(id, -1);
            result.put("isLiked", false);
            result.put("likeCount", review.getLikeCount() - 1);
        } else {
            reviewMapper.insertLike(id, userId);
            reviewMapper.updateLikeCount(id, 1);
            result.put("isLiked", true);
            result.put("likeCount", review.getLikeCount() + 1);
        }
        
        return Result.success(result);
    }

    @GetMapping("/stats/{productId}")
    public Result<Map<String, Object>> getStats(@PathVariable Long productId) {
        Double avgRating = reviewMapper.getAverageRating(productId);
        int count = reviewMapper.countByProductId(productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("avgRating", avgRating != null ? avgRating : 0.0);
        result.put("count", count);
        return Result.success(result);
    }
}
