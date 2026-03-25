-- 评论功能扩展

-- 添加评论回复和点赞字段
ALTER TABLE `review` ADD COLUMN `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID，用于回复' AFTER `order_id`;
ALTER TABLE `review` ADD COLUMN `like_count` INT DEFAULT 0 COMMENT '点赞数' AFTER `images`;

-- 创建评论点赞表
DROP TABLE IF EXISTS `review_like`;
CREATE TABLE `review_like` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    `review_id` BIGINT NOT NULL COMMENT '评论ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY `uk_review_user` (`review_id`, `user_id`),
    KEY `idx_review_id` (`review_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';

-- 为parent_id添加索引
ALTER TABLE `review` ADD INDEX `idx_parent_id` (`parent_id`);
