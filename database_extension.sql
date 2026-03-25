-- 用户购物模块数据库扩展
-- 商品收藏表
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';

-- 商品评价表
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `order_id` BIGINT COMMENT '订单ID',
    `rating` TINYINT NOT NULL DEFAULT 5 COMMENT '评分：1-5星',
    `content` TEXT COMMENT '评价内容',
    `images` VARCHAR(1000) COMMENT '评价图片，逗号分隔',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0隐藏，1显示',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- 为购物车表添加选中状态字段
ALTER TABLE `cart` ADD COLUMN `selected` TINYINT DEFAULT 1 COMMENT '是否选中：0否，1是';

-- 插入收藏测试数据
INSERT INTO `favorite` (`user_id`, `product_id`) VALUES
(2, 1),
(2, 9),
(2, 16),
(3, 10),
(3, 27),
(4, 31),
(5, 36),
(6, 21);

-- 插入评价测试数据
INSERT INTO `review` (`user_id`, `product_id`, `order_id`, `rating`, `content`) VALUES
(2, 1, 1, 5, '非常新鲜的西红柿，口感很好，下次还会购买！'),
(2, 9, 1, 4, '草莓很甜，就是有点贵'),
(3, 16, 2, 5, '大米质量很好，煮出来的饭很香'),
(4, 27, 3, 5, '大闸蟹很肥美，蟹黄很多，非常满意！'),
(5, 10, 4, 4, '苹果很脆很甜，包装也很好'),
(6, 31, 5, 5, '黑猪肉肉质鲜美，值得推荐'),
(7, 36, 6, 4, '干香菇香味浓郁，煲汤很好喝');
