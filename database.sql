-- 创建数据库
CREATE DATABASE IF NOT EXISTS agricultural_products DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE agricultural_products;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像',
    `role` TINYINT DEFAULT 0 COMMENT '角色：0普通用户，1管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用，1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(255) COMMENT '分类描述',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用，1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农产品分类表';

-- 产品表
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '产品ID',
    `name` VARCHAR(100) NOT NULL COMMENT '产品名称',
    `category_id` BIGINT COMMENT '分类ID',
    `description` TEXT COMMENT '产品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `unit` VARCHAR(20) COMMENT '单位',
    `origin` VARCHAR(100) COMMENT '产地',
    `image` VARCHAR(255) COMMENT '产品图片',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0下架，1上架',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农产品表';

-- 订单表
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    `user_id` BIGINT COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) COMMENT '订单总金额',
    `status` TINYINT DEFAULT 0 COMMENT '订单状态：0待付款，1待发货，2待收货，3已完成，4已取消',
    `receiver_name` VARCHAR(50) COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) COMMENT '收货人电话',
    `receiver_address` VARCHAR(255) COMMENT '收货地址',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单详情表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单详情ID',
    `order_id` BIGINT COMMENT '订单ID',
    `product_id` BIGINT COMMENT '产品ID',
    `product_name` VARCHAR(100) COMMENT '产品名称',
    `product_image` VARCHAR(255) COMMENT '产品图片',
    `price` DECIMAL(10,2) COMMENT '单价',
    `quantity` INT COMMENT '数量',
    `subtotal` DECIMAL(10,2) COMMENT '小计',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表';

-- 购物车表
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` BIGINT COMMENT '用户ID',
    `product_id` BIGINT COMMENT '产品ID',
    `quantity` INT DEFAULT 1 COMMENT '数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 插入用户测试数据
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `email`, `avatar`, `role`, `status`) VALUES
('admin', '21232f297a57a5a743894a0e4a801fc3', '系统管理员', '13800138000', 'admin@example.com', NULL, 1, 1),
('user1', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138001', 'zhangsan@example.com', NULL, 0, 1),
('user2', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13800138002', 'lisi@example.com', NULL, 0, 1),
('user3', 'e10adc3949ba59abbe56e057f20f883e', '王五', '13800138003', 'wangwu@example.com', NULL, 0, 1),
('user4', 'e10adc3949ba59abbe56e057f20f883e', '赵六', '13800138004', 'zhaoliu@example.com', NULL, 0, 1),
('user5', 'e10adc3949ba59abbe56e057f20f883e', '孙七', '13800138005', 'sunqi@example.com', NULL, 0, 1),
('user6', 'e10adc3949ba59abbe56e057f20f883e', '周八', '13800138006', 'zhouba@example.com', NULL, 0, 1),
('user7', 'e10adc3949ba59abbe56e057f20f883e', '吴九', '13800138007', 'wujiu@example.com', NULL, 0, 1),
('user8', 'e10adc3949ba59abbe56e057f20f883e', '郑十', '13800138008', 'zhengshi@example.com', NULL, 0, 1),
('user9', 'e10adc3949ba59abbe56e057f20f883e', '钱十一', '13800138009', 'qianshiyi@example.com', NULL, 0, 1),
('user10', 'e10adc3949ba59abbe56e057f20f883e', '陈十二', '13800138010', 'chenshier@example.com', NULL, 0, 1),
('user11', 'e10adc3949ba59abbe56e057f20f883e', '林十三', '13800138011', 'linshisan@example.com', NULL, 0, 1),
('user12', 'e10adc3949ba59abbe56e057f20f883e', '黄十四', '13800138012', 'huangshisi@example.com', NULL, 0, 1),
('user13', 'e10adc3949ba59abbe56e057f20f883e', '杨十五', '13800138013', 'yangshiwu@example.com', NULL, 0, 1),
('user14', 'e10adc3949ba59abbe56e057f20f883e', '刘十六', '13800138014', 'liushiliu@example.com', NULL, 0, 1),
('user15', 'e10adc3949ba59abbe56e057f20f883e', '何十七', '13800138015', 'heshiqi@example.com', NULL, 0, 1);

-- 插入分类测试数据
INSERT INTO `category` (`name`, `description`, `sort`, `status`) VALUES
('蔬菜类', '新鲜蔬菜，绿色健康', 1, 1),
('水果类', '时令水果，甜美可口', 2, 1),
('粮食类', '优质粮食，营养健康', 3, 1),
('禽蛋类', '农家禽蛋，新鲜营养', 4, 1),
('水产类', '鲜活水产，品质保证', 5, 1),
('肉类', '优质肉类，安全放心', 6, 1),
('干货类', '精选干货，方便储存', 7, 1),
('调味品类', '天然调味，美味健康', 8, 1);

-- 插入产品测试数据
INSERT INTO `product` (`name`, `category_id`, `description`, `price`, `stock`, `unit`, `origin`, `image`, `status`, `sales`) VALUES
('有机西红柿', 1, '自然成熟，口感鲜美，富含维生素C', 8.50, 500, '斤', '山东寿光', NULL, 1, 328),
('新鲜黄瓜', 1, '清脆爽口，适合凉拌或炒菜', 5.80, 300, '斤', '河北承德', NULL, 1, 256),
('紫甘蓝', 1, '营养丰富，色彩鲜艳，适合沙拉', 6.50, 200, '斤', '北京顺义', NULL, 1, 189),
('有机胡萝卜', 1, '甜脆可口，富含胡萝卜素', 4.50, 400, '斤', '内蒙古', NULL, 1, 312),
('新鲜菠菜', 1, '嫩绿新鲜，营养丰富', 4.80, 250, '斤', '山东青岛', NULL, 1, 198),
('大白菜', 1, '清甜爽口，适合炖煮', 2.50, 600, '斤', '东北', NULL, 1, 456),
('新鲜青椒', 1, '清脆爽口，适合炒菜', 5.50, 350, '斤', '山东潍坊', NULL, 1, 267),
('有机茄子', 1, '紫皮鲜嫩，口感细腻', 6.80, 280, '斤', '河南郑州', NULL, 1, 234),
('新鲜草莓', 2, '香甜多汁，果肉饱满', 28.00, 150, '盒', '辽宁丹东', NULL, 1, 567),
('红富士苹果', 2, '脆甜可口，果香浓郁', 6.50, 800, '斤', '陕西延安', NULL, 1, 789),
('新疆哈密瓜', 2, '香甜爽口，汁多味美', 12.00, 200, '个', '新疆哈密', NULL, 1, 345),
('新鲜葡萄', 2, '皮薄肉厚，甜度适中', 15.00, 300, '斤', '新疆吐鲁番', NULL, 1, 423),
('水蜜桃', 2, '香甜多汁，入口即化', 10.00, 250, '斤', '浙江奉化', NULL, 1, 356),
('新鲜橙子', 2, '酸甜可口，富含维C', 5.50, 500, '斤', '江西赣州', NULL, 1, 467),
('海南香蕉', 2, '香甜软糯，营养丰富', 4.00, 400, '斤', '海南', NULL, 1, 534),
('东北大米', 3, '颗粒饱满，香糯可口', 4.50, 1000, '斤', '黑龙江五常', NULL, 1, 876),
('优质小米', 3, '金黄饱满，熬粥佳品', 6.00, 500, '斤', '山西沁县', NULL, 1, 432),
('东北玉米', 3, '香甜软糯，营养丰富', 2.00, 800, '根', '吉林', NULL, 1, 654),
('红豆', 3, '颗粒饱满，适合煮粥', 8.00, 300, '斤', '黑龙江', NULL, 1, 234),
('绿豆', 3, '清热解暑，营养丰富', 7.50, 350, '斤', '吉林', NULL, 1, 287),
('土鸡蛋', 4, '农家散养，营养丰富', 1.50, 2000, '个', '河北保定', NULL, 1, 1234),
('新鲜鸭蛋', 4, '个大饱满，适合腌制', 1.80, 1500, '个', '江苏高邮', NULL, 1, 567),
('鹅蛋', 4, '营养丰富，口感细腻', 5.00, 500, '个', '山东', NULL, 1, 234),
('鹌鹑蛋', 4, '小巧营养，适合煲汤', 0.80, 3000, '个', '河南', NULL, 1, 456),
('皮蛋', 4, '传统工艺，口感独特', 2.00, 800, '个', '湖北', NULL, 1, 345),
('淡水鲤鱼', 5, '鲜活肥美，适合红烧', 12.00, 200, '斤', '江苏洪泽湖', NULL, 1, 234),
('大闸蟹', 5, '蟹黄饱满，肉质鲜美', 88.00, 100, '只', '江苏阳澄湖', NULL, 1, 567),
('基围虾', 5, '鲜活肥美，肉质Q弹', 45.00, 150, '斤', '广东', NULL, 1, 345),
('黄花鱼', 5, '肉质鲜嫩，营养丰富', 35.00, 120, '斤', '浙江舟山', NULL, 1, 234),
('扇贝', 5, '肉质鲜美，营养丰富', 25.00, 200, '斤', '山东烟台', NULL, 1, 312),
('黑猪肉', 6, '肉质鲜美，口感细腻', 35.00, 150, '斤', '浙江金华', NULL, 1, 456),
('土鸡', 6, '农家散养，肉质紧实', 28.00, 100, '只', '安徽', NULL, 1, 345),
('牛肉', 6, '新鲜牛肉，肉质鲜嫩', 45.00, 80, '斤', '内蒙古', NULL, 1, 234),
('羊肉', 6, '新鲜羊肉，无膻味', 48.00, 60, '斤', '新疆', NULL, 1, 189),
('排骨', 6, '新鲜排骨，适合炖汤', 32.00, 100, '斤', '河南', NULL, 1, 267),
('干香菇', 7, '香味浓郁，适合煲汤', 45.00, 100, '斤', '福建古田', NULL, 1, 234),
('干木耳', 7, '肉质厚实，营养丰富', 35.00, 150, '斤', '黑龙江', NULL, 1, 312),
('干银耳', 7, '胶质丰富，美容养颜', 40.00, 120, '斤', '福建', NULL, 1, 256),
('红枣', 7, '个大饱满，甜度适中', 25.00, 200, '斤', '新疆', NULL, 1, 432),
('枸杞', 7, '色泽红润，营养丰富', 50.00, 100, '斤', '宁夏', NULL, 1, 345),
('酱油', 8, '传统酿造，味道鲜美', 12.00, 300, '瓶', '广东', NULL, 1, 567),
('陈醋', 8, '酸香浓郁，口感醇厚', 10.00, 250, '瓶', '山西', NULL, 1, 432),
('豆瓣酱', 8, '香辣可口，适合炒菜', 8.00, 400, '瓶', '四川', NULL, 1, 345),
('蚝油', 8, '鲜美提味，营养丰富', 15.00, 200, '瓶', '广东', NULL, 1, 289),
('花椒', 8, '麻香浓郁，适合调味', 30.00, 150, '斤', '四川', NULL, 1, 234);

-- 插入订单测试数据
INSERT INTO `order` (`order_no`, `user_id`, `total_amount`, `status`, `receiver_name`, `receiver_phone`, `receiver_address`, `remark`) VALUES
('ORD202403150001', 2, 156.50, 3, '张三', '13800138001', '北京市朝阳区建国路88号', '请尽快发货'),
('ORD202403150002', 3, 89.00, 2, '李四', '13800138002', '上海市浦东新区陆家嘴金融中心', '周末配送'),
('ORD202403150003', 4, 234.00, 1, '王五', '13800138003', '广州市天河区珠江新城', NULL),
('ORD202403150004', 5, 67.50, 0, '赵六', '13800138004', '深圳市南山区科技园', '需要发票'),
('ORD202403160001', 6, 345.00, 3, '孙七', '13800138005', '杭州市西湖区文三路', NULL),
('ORD202403160002', 7, 128.00, 2, '周八', '13800138006', '成都市武侯区天府大道', '小心轻放'),
('ORD202403160003', 8, 456.00, 1, '吴九', '13800138007', '武汉市江汉区解放大道', NULL),
('ORD202403160004', 9, 78.50, 0, '郑十', '13800138008', '南京市鼓楼区中山路', '下午配送'),
('ORD202403170001', 10, 267.00, 3, '钱十一', '13800138009', '西安市雁塔区高新路', NULL),
('ORD202403170002', 11, 189.00, 2, '陈十二', '13800138010', '重庆市渝中区解放碑', '保鲜配送'),
('ORD202403170003', 12, 312.00, 1, '林十三', '13800138011', '天津市和平区南京路', NULL),
('ORD202403170004', 13, 95.00, 0, '黄十四', '13800138012', '苏州市姑苏区观前街', '需要礼盒包装'),
('ORD202403180001', 14, 423.00, 3, '杨十五', '13800138013', '青岛市市南区香港中路', NULL),
('ORD202403180002', 15, 156.00, 2, '刘十六', '13800138014', '厦门市思明区中山路', '尽快配送'),
('ORD202403180003', 2, 234.50, 1, '张三', '13800138001', '北京市朝阳区建国路88号', NULL),
('ORD202403180004', 3, 89.50, 0, '李四', '13800138002', '上海市浦东新区陆家嘴金融中心', '周末配送'),
('ORD202403190001', 4, 345.00, 3, '王五', '13800138003', '广州市天河区珠江新城', NULL),
('ORD202403190002', 5, 178.00, 2, '赵六', '13800138004', '深圳市南山区科技园', '小心轻放'),
('ORD202403190003', 6, 267.50, 1, '孙七', '13800138005', '杭州市西湖区文三路', NULL),
('ORD202403190004', 7, 456.00, 0, '周八', '13800138006', '成都市武侯区天府大道', '需要发票');

-- 插入订单详情测试数据
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `subtotal`) VALUES
(1, 1, '有机西红柿', NULL, 8.50, 5, 42.50),
(1, 9, '新鲜草莓', NULL, 28.00, 4, 112.00),
(2, 16, '东北大米', NULL, 4.50, 10, 45.00),
(2, 21, '土鸡蛋', NULL, 1.50, 20, 30.00),
(3, 27, '大闸蟹', NULL, 88.00, 2, 176.00),
(3, 28, '基围虾', NULL, 45.00, 1, 45.00),
(4, 10, '红富士苹果', NULL, 6.50, 5, 32.50),
(4, 11, '新疆哈密瓜', NULL, 12.00, 2, 24.00),
(5, 31, '黑猪肉', NULL, 35.00, 5, 175.00),
(5, 32, '土鸡', NULL, 28.00, 3, 84.00),
(6, 36, '干香菇', NULL, 45.00, 2, 90.00),
(6, 39, '红枣', NULL, 25.00, 3, 75.00),
(7, 16, '东北大米', NULL, 4.50, 20, 90.00),
(7, 17, '优质小米', NULL, 6.00, 15, 90.00),
(8, 1, '有机西红柿', NULL, 8.50, 3, 25.50),
(8, 2, '新鲜黄瓜', NULL, 5.80, 5, 29.00),
(9, 9, '新鲜草莓', NULL, 28.00, 6, 168.00),
(9, 12, '新鲜葡萄', NULL, 15.00, 5, 75.00),
(10, 21, '土鸡蛋', NULL, 1.50, 30, 45.00),
(10, 22, '新鲜鸭蛋', NULL, 1.80, 25, 45.00),
(11, 26, '淡水鲤鱼', NULL, 12.00, 8, 96.00),
(11, 29, '扇贝', NULL, 25.00, 5, 125.00),
(12, 41, '酱油', NULL, 12.00, 3, 36.00),
(12, 42, '陈醋', NULL, 10.00, 2, 20.00),
(13, 33, '牛肉', NULL, 45.00, 6, 270.00),
(13, 34, '羊肉', NULL, 48.00, 2, 96.00),
(14, 13, '水蜜桃', NULL, 10.00, 8, 80.00),
(14, 14, '新鲜橙子', NULL, 5.50, 10, 55.00),
(15, 3, '紫甘蓝', NULL, 6.50, 10, 65.00),
(15, 4, '有机胡萝卜', NULL, 4.50, 15, 67.50),
(16, 7, '新鲜青椒', NULL, 5.50, 8, 44.00),
(16, 8, '有机茄子', NULL, 6.80, 5, 34.00),
(17, 27, '大闸蟹', NULL, 88.00, 3, 264.00),
(17, 28, '基围虾', NULL, 45.00, 2, 90.00),
(18, 16, '东北大米', NULL, 4.50, 15, 67.50),
(18, 18, '东北玉米', NULL, 2.00, 30, 60.00),
(19, 31, '黑猪肉', NULL, 35.00, 4, 140.00),
(19, 35, '排骨', NULL, 32.00, 3, 96.00),
(20, 36, '干香菇', NULL, 45.00, 5, 225.00),
(20, 37, '干木耳', NULL, 35.00, 4, 140.00);

-- 插入购物车测试数据
INSERT INTO `cart` (`user_id`, `product_id`, `quantity`) VALUES
(2, 1, 3),
(2, 9, 2),
(3, 16, 5),
(3, 21, 10),
(4, 27, 1),
(5, 10, 3),
(6, 31, 2),
(7, 36, 1);
