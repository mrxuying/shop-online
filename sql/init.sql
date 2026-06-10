-- =============================================
-- Shop-Online 数据库初始化脚本
-- MySQL 8.0+
-- =============================================

CREATE DATABASE IF NOT EXISTS `shop_online` DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `shop_online`;

-- =============================================
-- 1. 用户模块
-- =============================================

-- 用户表
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(32) COMMENT '昵称',
    `phone` VARCHAR(16) COMMENT '手机号',
    `avatar` VARCHAR(256) COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别: 0-未知 1-男 2-女',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-正常 1-已删除',
    INDEX `idx_phone` (`phone`),
    INDEX `idx_status` (`status`)
) COMMENT '用户表';

-- 收货地址表
CREATE TABLE `user_address` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(32) NOT NULL COMMENT '收件人姓名',
    `receiver_phone` VARCHAR(16) NOT NULL COMMENT '收件人电话',
    `province` VARCHAR(32) NOT NULL COMMENT '省份',
    `city` VARCHAR(32) NOT NULL COMMENT '城市',
    `district` VARCHAR(32) NOT NULL COMMENT '区/县',
    `detail_address` VARCHAR(256) NOT NULL COMMENT '详细地址',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认: 0-否 1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`)
) COMMENT '收货地址表';

-- =============================================
-- 2. 商品模块
-- =============================================

-- 商品分类表
CREATE TABLE `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID, 0代表一级分类',
    `name` VARCHAR(64) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(256) COMMENT '分类图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序权重',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_parent_id` (`parent_id`)
) COMMENT '商品分类表';

-- 商品表
CREATE TABLE `product` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(128) NOT NULL COMMENT '商品名称',
    `subtitle` VARCHAR(256) COMMENT '副标题/卖点',
    `main_image` VARCHAR(256) COMMENT '主图URL',
    `detail` LONGTEXT COMMENT '商品详情(富文本)',
    `price` DECIMAL(10,2) NOT NULL COMMENT '最低价格(展示用)',
    `sales` INT DEFAULT 0 COMMENT '累计销量',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_status` (`status`)
) COMMENT '商品表';

-- 商品SKU表 (规格库存)
CREATE TABLE `product_sku` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `sku_code` VARCHAR(64) NOT NULL UNIQUE COMMENT 'SKU编码',
    `spec_info` JSON COMMENT '规格信息, 如 {"颜色":"红色","尺寸":"XL"}',
    `price` DECIMAL(10,2) NOT NULL COMMENT 'SKU价格',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    `image` VARCHAR(256) COMMENT 'SKU图片',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_sku_code` (`sku_code`)
) COMMENT '商品SKU表';

-- 商品图片表
CREATE TABLE `product_image` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `image_url` VARCHAR(256) NOT NULL COMMENT '图片URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_product_id` (`product_id`)
) COMMENT '商品图片表';

-- 商品收藏表
CREATE TABLE `product_favorite` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    INDEX `idx_user_id` (`user_id`)
) COMMENT '商品收藏表';

-- 商品评价表
CREATE TABLE `product_review` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `sku_id` BIGINT COMMENT 'SKU ID',
    `rating` TINYINT NOT NULL COMMENT '评分: 1-5',
    `content` VARCHAR(1024) COMMENT '评价内容',
    `images` JSON COMMENT '评价图片',
    `is_hidden` TINYINT DEFAULT 0 COMMENT '是否隐藏: 0-否 1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_user_id` (`user_id`)
) COMMENT '商品评价表';

-- =============================================
-- 3. 购物车模块
-- =============================================

CREATE TABLE `cart` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `selected` TINYINT DEFAULT 1 COMMENT '是否选中: 0-否 1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_user_sku` (`user_id`, `sku_id`),
    INDEX `idx_user_id` (`user_id`)
) COMMENT '购物车表';

-- =============================================
-- 4. 订单模块
-- =============================================

-- 订单表
CREATE TABLE `order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '商品总金额',
    `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    `freight_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `pay_type` TINYINT COMMENT '支付方式: 1-支付宝 2-微信',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '订单状态: 1-待付款 2-待发货 3-待收货 4-已完成 5-已取消 6-退款中',
    `receiver_name` VARCHAR(32) COMMENT '收货人',
    `receiver_phone` VARCHAR(16) COMMENT '收货电话',
    `receiver_address` VARCHAR(256) COMMENT '收货地址',
    `delivery_company` VARCHAR(32) COMMENT '快递公司',
    `delivery_no` VARCHAR(64) COMMENT '快递单号',
    `delivery_time` DATETIME COMMENT '发货时间',
    `receive_time` DATETIME COMMENT '收货时间',
    `payment_time` DATETIME COMMENT '支付时间',
    `cancel_time` DATETIME COMMENT '取消时间',
    `remark` VARCHAR(512) COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
) COMMENT '订单表';

-- 订单商品项表
CREATE TABLE `order_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
    `product_name` VARCHAR(128) NOT NULL COMMENT '商品名称(快照)',
    `product_image` VARCHAR(256) COMMENT '商品图片(快照)',
    `spec_info` JSON COMMENT '规格信息(快照)',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL COMMENT '数量',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_order_no` (`order_no`)
) COMMENT '订单商品项表';

-- =============================================
-- 5. 支付模块
-- =============================================

CREATE TABLE `payment_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `pay_no` VARCHAR(64) NOT NULL UNIQUE COMMENT '支付流水号',
    `pay_type` TINYINT NOT NULL COMMENT '支付方式',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `status` TINYINT NOT NULL COMMENT '支付状态: 0-待支付 1-支付成功 2-支付失败 3-已退款',
    `third_party_no` VARCHAR(64) COMMENT '第三方交易号',
    `pay_time` DATETIME COMMENT '支付时间',
    `refund_time` DATETIME COMMENT '退款时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_pay_no` (`pay_no`)
) COMMENT '支付记录表';

-- =============================================
-- 6. 管理后台
-- =============================================

CREATE TABLE `admin_user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(32) COMMENT '昵称',
    `avatar` VARCHAR(256) COMMENT '头像URL',
    `role` VARCHAR(32) NOT NULL DEFAULT 'ADMIN' COMMENT '角色: ADMIN/SUPER_ADMIN',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '管理员表';

-- 轮播图表
CREATE TABLE `banner` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title` VARCHAR(64) COMMENT '标题',
    `image_url` VARCHAR(256) NOT NULL COMMENT '图片URL',
    `link_url` VARCHAR(256) COMMENT '跳转链接',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '轮播图表';

-- =============================================
-- 初始化数据
-- =============================================

-- 插入默认管理员 (密码: admin123, BCrypt加密)
INSERT INTO `admin_user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 'SUPER_ADMIN', 1);

-- 插入默认商品分类
INSERT INTO `category` (`id`, `parent_id`, `name`, `sort_order`) VALUES
(1, 0, '服装', 1),
(2, 0, '数码电子', 2),
(3, 0, '食品饮料', 3),
(4, 0, '家居用品', 4),
(5, 1, '男装', 1),
(6, 1, '女装', 2),
(7, 2, '手机', 1),
(8, 2, '电脑', 2),
(9, 2, '智能穿戴', 3),
(10, 3, '零食', 1),
(11, 3, '饮料', 2),
(12, 4, '家纺', 1),
(13, 4, '厨具', 2);

-- =============================================
-- 测试商品数据
-- =============================================

-- 手机类商品
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `price`, `sales`, `status`) VALUES
(1, 7, 'iPhone 15 Pro Max', 'A17 Pro 芯片 | 钛金属设计 | 4800万像素', 8999.00, 2580, 1),
(2, 7, 'iPhone 15', 'A16 仿生芯片 | 灵动岛 | 4800万双摄', 5999.00, 4200, 1),
(3, 7, '华为 Mate 60 Pro', '麒麟 9000S | 卫星通话 | 鸿蒙OS', 6999.00, 3100, 1),
(4, 7, '小米 14 Pro', '骁龙 8 Gen 3 | 徕卡光学 | 120W快充', 4999.00, 1800, 1),
(5, 7, 'OPPO Find X7 Ultra', '骁龙 8 Gen 3 | 哈苏影像 | 100W快充', 5999.00, 950, 1),
(6, 7, 'vivo X100 Pro', '天玑 9300 | 蔡司影像 | 120W快充', 4999.00, 1200, 1);

-- 电脑类商品
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `price`, `sales`, `status`) VALUES
(7, 8, 'MacBook Pro 16"', 'M3 Max 芯片 | 32GB内存 | 1TB存储', 19999.00, 890, 1),
(8, 8, 'MacBook Air 15"', 'M3 芯片 | 8GB内存 | 256GB存储', 10499.00, 1500, 1),
(9, 8, 'ThinkPad X1 Carbon', 'Ultra 7 处理器 | 14英寸 | 轻薄商务', 9999.00, 680, 1),
(10, 8, '华为 MateBook X Pro', 'Ultra 9 处理器 | 3.1K屏 | 触控全面屏', 11999.00, 430, 1),
(11, 8, '小米笔记本 Pro 16', 'Ultra 7 处理器 | 16英寸 | 3.1K屏', 6999.00, 750, 1);

-- 智能穿戴类商品
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `price`, `sales`, `status`) VALUES
(12, 9, 'Apple Watch Series 9', 'S9 芯片 | 全天候显示 | 血氧检测', 3999.00, 2100, 1),
(13, 9, '华为 Watch GT 4', '46mm | 14天续航 | 心率监测', 1588.00, 1600, 1),
(14, 9, '小米手环 8 Pro', '1.74"屏幕 | 14天续航 | 150+运动', 399.00, 5200, 1),
(15, 9, 'AirPods Pro 2', 'H2 芯片 | 自适应降噪 | USB-C', 1899.00, 3500, 1);

-- 男装类商品
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `price`, `sales`, `status`) VALUES
(16, 5, '商务修身西装套装', '羊毛混纺 | 免烫面料 | 四季通用', 899.00, 1200, 1),
(17, 5, '纯棉免烫衬衫', '长绒棉 | 抗皱易干 | 商务休闲', 259.00, 2500, 1),
(18, 5, '弹力修身牛仔裤', '棉弹面料 | 修身版型 | 四季百搭', 299.00, 1800, 1),
(19, 5, '轻薄羽绒服', '90%白鹅绒 | 轻薄保暖 | 可收纳', 699.00, 980, 1);

-- 女装类商品
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `price`, `sales`, `status`) VALUES
(20, 6, '法式碎花连衣裙', '雪纺面料 | 复古碎花 | 夏季新款', 329.00, 2100, 1),
(21, 6, '高腰阔腿裤', '垂感面料 | 显瘦设计 | 通勤百搭', 259.00, 1600, 1),
(22, 6, '针织开衫毛衣', '羊毛混纺 | 宽松版型 | 秋冬保暖', 399.00, 870, 1),
(23, 6, '真丝睡衣套装', '桑蚕丝 | 柔软亲肤 | 精致礼盒', 599.00, 560, 1);

-- 零食类商品
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `price`, `sales`, `status`) VALUES
(24, 10, '坚果大礼包', '每日坚果 | 7种混合 | 30袋装', 139.00, 4300, 1),
(25, 10, '手工牛肉干', '内蒙古风干牛肉 | 真空包装 | 500g', 89.00, 2800, 1),
(26, 10, '进口巧克力礼盒', '比利时进口 | 72%可可 | 精美礼盒', 168.00, 1900, 1),
(27, 10, '日式抹茶饼干', '宇治抹茶 | 独立小包装 | 300g', 49.00, 3500, 1);

-- 饮料类商品
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `price`, `sales`, `status`) VALUES
(28, 11, '冷萃咖啡液', '哥伦比亚咖啡 | 即溶冷萃 | 30条装', 79.00, 4200, 1),
(29, 11, '有机绿茶礼盒', '明前龙井 | 高山茶园 | 200g礼盒', 268.00, 890, 1),
(30, 11, 'NFC鲜榨橙汁', '鲜果压榨 | 无添加 | 1L装', 29.90, 6800, 1),
(31, 11, '进口牛奶', '新西兰全脂 | 高钙高蛋白 | 1L×12盒', 89.00, 3200, 1);

-- 家纺类商品
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `price`, `sales`, `status`) VALUES
(32, 12, '全棉四件套', '长绒棉 | 亲肤透气 | 简约纯色', 399.00, 1500, 1),
(33, 12, '乳胶枕', '泰国天然乳胶 | 护颈设计 | 60×40cm', 259.00, 2100, 1),
(34, 12, '冰丝凉席三件套', '凉感面料 | 可水洗 | 1.8米床', 199.00, 1800, 1);

-- 厨具类商品
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `price`, `sales`, `status`) VALUES
(35, 13, '不粘炒锅', '麦饭石涂层 | 电磁炉通用 | 32cm', 259.00, 2400, 1),
(36, 13, '刀具套装', '德国不锈钢 | 5件套装 | 含刀架', 399.00, 1600, 1),
(37, 13, '破壁机', '1200W大功率 | 8叶刀头 | 无渣免滤', 599.00, 980, 1);

-- =============================================
-- 测试商品SKU数据
-- =============================================

-- iPhone 15 Pro Max SKU
INSERT INTO `product_sku` (`product_id`, `sku_code`, `spec_info`, `price`, `stock`) VALUES
(1, 'IP15PM-256-BLK', '{"颜色":"黑色钛金属","容量":"256GB"}', 8999.00, 100),
(1, 'IP15PM-512-BLK', '{"颜色":"黑色钛金属","容量":"512GB"}', 9999.00, 50),
(1, 'IP15PM-256-NAT', '{"颜色":"原色钛金属","容量":"256GB"}', 8999.00, 80),
(1, 'IP15PM-512-NAT', '{"颜色":"原色钛金属","容量":"512GB"}', 9999.00, 40);

-- iPhone 15 SKU
INSERT INTO `product_sku` (`product_id`, `sku_code`, `spec_info`, `price`, `stock`) VALUES
(2, 'IP15-128-BLK', '{"颜色":"黑色","容量":"128GB"}', 5999.00, 150),
(2, 'IP15-256-BLK', '{"颜色":"黑色","容量":"256GB"}', 6999.00, 100),
(2, 'IP15-128-BLU', '{"颜色":"蓝色","容量":"128GB"}', 5999.00, 120);

-- 华为 Mate 60 Pro SKU
INSERT INTO `product_sku` (`product_id`, `sku_code`, `spec_info`, `price`, `stock`) VALUES
(3, 'HW-M60P-12-512', '{"颜色":"雅丹黑","容量":"512GB","内存":"12GB"}', 6999.00, 60),
(3, 'HW-M60P-12-256', '{"颜色":"白沙银","容量":"256GB","内存":"12GB"}', 6499.00, 80);

-- 小米 14 Pro SKU
INSERT INTO `product_sku` (`product_id`, `sku_code`, `spec_info`, `price`, `stock`) VALUES
(4, 'MI14P-12-256', '{"颜色":"黑色","内存":"12GB","容量":"256GB"}', 4999.00, 100),
(4, 'MI14P-16-512', '{"颜色":"白色","内存":"16GB","容量":"512GB"}', 5499.00, 60);

-- 商务西装 SKU
INSERT INTO `product_sku` (`product_id`, `sku_code`, `spec_info`, `price`, `stock`) VALUES
(16, 'SUIT-BLK-M', '{"颜色":"黑色","尺码":"M"}', 899.00, 50),
(16, 'SUIT-BLK-L', '{"颜色":"黑色","尺码":"L"}', 899.00, 80),
(16, 'SUIT-BLK-XL', '{"颜色":"黑色","尺码":"XL"}', 899.00, 40),
(16, 'SUIT-NAV-M', '{"颜色":"藏青色","尺码":"M"}', 899.00, 45);

-- 纯棉衬衫 SKU
INSERT INTO `product_sku` (`product_id`, `sku_code`, `spec_info`, `price`, `stock`) VALUES
(17, 'SHIRT-WHT-39', '{"颜色":"白色","尺码":"39"}', 259.00, 100),
(17, 'SHIRT-WHT-40', '{"颜色":"白色","尺码":"40"}', 259.00, 120),
(17, 'SHIRT-BLU-40', '{"颜色":"浅蓝","尺码":"40"}', 259.00, 80);

-- 坚果大礼包 SKU
INSERT INTO `product_sku` (`product_id`, `sku_code`, `spec_info`, `price`, `stock`) VALUES
(24, 'NUTS-30P', '{"规格":"30袋装","口味":"混合"}', 139.00, 500),
(24, 'NUTS-7P', '{"规格":"7袋装","口味":"混合"}', 39.90, 800);

-- 全棉四件套 SKU
INSERT INTO `product_sku` (`product_id`, `sku_code`, `spec_info`, `price`, `stock`) VALUES
(32, 'BED-1.5-GRY', '{"规格":"1.5米床","颜色":"灰色"}', 399.00, 60),
(32, 'BED-1.8-GRY', '{"规格":"1.8米床","颜色":"灰色"}', 459.00, 80),
(32, 'BED-1.5-BLU', '{"规格":"1.5米床","颜色":"蓝色"}', 399.00, 50);

-- 不粘炒锅 SKU
INSERT INTO `product_sku` (`product_id`, `sku_code`, `spec_info`, `price`, `stock`) VALUES
(35, 'PAN-32-RED', '{"规格":"32cm","颜色":"红色"}', 259.00, 120),
(35, 'PAN-34-RED', '{"规格":"34cm","颜色":"红色"}', 299.00, 80);

-- 删除线效果：有下架商品
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `price`, `sales`, `status`) VALUES
(38, 7, '三星 Galaxy S24 Ultra', '骁龙 8 Gen 3 | 钛金属 | S Pen', 9699.00, 0, 0);

-- 删除线效果：逻辑删除商品
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `price`, `sales`, `status`, `is_deleted`) VALUES
(39, 7, '已删除的测试商品', '该商品已被逻辑删除', 100.00, 0, 1, 1);
