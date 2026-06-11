# Shop-Online 电商网站 — 需求分析与技术方案

## 目录

1. [项目概述](#1-项目概述)
2. [需求分析](#2-需求分析)
3. [技术架构](#3-技术架构)
4. [数据库设计](#4-数据库设计)
5. [接口设计概要](#5-接口设计概要)
6. [开发计划](#6-开发计划)
7. [风险与应对](#7-风险与应对)
8. [附录](#8-附录)

---

## 1. 项目概述

### 1.1 项目目标

构建一个 B2C 模式的单体电商网站，面向消费者提供商品浏览、搜索、下单、支付一站式购物体验，同时为运营人员提供商品管理、订单管理的后台系统。

### 1.2 项目定位

- **类型**: 单体应用，前后端分离架构
- **用户群体**: C 端消费者 + B 端运营管理员
- **核心价值**: 完整的电商业务流程闭环
- **编码规范**: 严格遵循 [CLAUDE.md](./CLAUDE.md) 中的 Java 编码规范

---

## 2. 需求分析

### 2.1 用户端功能需求

#### 2.1.1 用户模块

| 编号 | 功能 | 优先级 | 描述 |
|------|------|--------|------|
| U-01 | 用户注册 | P0 | 支持用户名 + 密码 + 手机号注册，含验证码校验 |
| U-02 | 用户登录 | P0 | 用户名/手机号 + 密码登录，返回 JWT Token |
| U-03 | 第三方登录 | P2 | 微信扫码登录 |
| U-04 | 个人信息管理 | P1 | 查看/修改头像、昵称、收货地址 |
| U-05 | 密码修改/重置 | P1 | 通过原密码或手机验证码重置 |
| U-06 | 收货地址管理 | P0 | 新增/编辑/删除/设为默认，最多 20 个地址 |
| U-07 | 用户注销 | P2 | 账号注销，数据保留 30 天后清除 |

#### 2.1.2 商品模块

| 编号 | 功能 | 优先级 | 描述 |
|------|------|--------|------|
| P-01 | 商品分类展示 | P0 | 多级分类树，支持分类面包屑导航 |
| P-02 | 商品列表 | P0 | 分页展示，支持按价格/销量/上架时间排序 |
| P-03 | 商品搜索 | P0 | 关键词搜索（商品名称），支持搜索结果高亮 |
| P-04 | 商品详情 | P0 | 商品图片轮播、规格选择、库存展示、详情描述 |
| P-05 | 商品评价 | P1 | 好评/中评/差评，星级评分，支持晒图 |
| P-06 | 商品收藏 | P2 | 用户收藏商品，收藏列表查看 |
| P-07 | 商品推荐 | P3 | 基于浏览记录的热门推荐/相似推荐 |

#### 2.1.3 购物车模块

| 编号 | 功能 | 优先级 | 描述 |
|------|------|--------|------|
| C-01 | 添加商品到购物车 | P0 | 选择规格 + 数量后加入购物车 |
| C-02 | 修改购物车商品 | P0 | 修改数量、规格，删除商品 |
| C-03 | 购物车列表 | P0 | 展示勾选商品，显示小计金额 |
| C-04 | 购物车全选/取消 | P0 | 支持全选与批量操作 |
| C-05 | 合并购物车 | P2 | 未登录加入购物车后，登录时合并 |

#### 2.1.4 订单模块

| 编号 | 功能 | 优先级 | 描述 |
|------|------|--------|------|
| O-01 | 订单创建 | P0 | 从购物车勾选商品生成订单，锁定库存 |
| O-02 | 订单列表 | P0 | 按状态分类查看：待付款/待发货/待收货/已完成/已取消 |
| O-03 | 订单详情 | P0 | 订单商品、金额、物流信息、时间线 |
| O-04 | 订单取消 | P0 | 未付款订单可取消，释放库存 |
| O-05 | 订单退款/退货 | P1 | 已收货订单申请退货退款 |
| O-06 | 确认收货 | P0 | 用户确认收货，订单完成 |
| O-07 | 订单超时自动取消 | P1 | 未付款订单 30 分钟自动取消 |

#### 2.1.5 支付模块

| 编号 | 功能 | 优先级 | 描述 |
|------|------|--------|------|
| PAY-01 | 在线支付 | P0 | 模拟支付宝/微信支付（沙箱环境） |
| PAY-02 | 支付回调 | P0 | 支付结果异步回调，更新订单状态 |
| PAY-03 | 支付查询 | P1 | 主动查询支付结果 |
| PAY-04 | 退款处理 | P1 | 原路退回，记录退款流水 |

### 2.2 管理端功能需求

| 编号 | 功能 | 优先级 | 描述 |
|------|------|--------|------|
| A-01 | 管理员登录 | P0 | 独立后台登录，与用户端隔离 |
| A-02 | 商品管理 | P0 | 商品 CRUD、上下架、库存管理 |
| A-03 | 分类管理 | P0 | 分类树维护、批量操作 |
| A-04 | 订单管理 | P0 | 订单查询、发货、处理退款 |
| A-05 | 用户管理 | P1 | 用户查询、禁用/启用 |
| A-06 | 数据统计 | P1 | 销售额、订单量、用户增长趋势 |
| A-07 | 轮播图/Banner 管理 | P2 | 首页轮播图配置 |
| A-08 | 评价审核 | P2 | 评价内容审核，违规内容隐藏 |

### 2.3 非功能性需求

| 类型 | 指标 |
|------|------|
| 响应时间 | 接口 P95 响应 < 500ms，页面首屏 < 2s |
| 并发支持 | 支持 1000 QPS (核心接口) |
| 可用性 | 99.9% |
| 数据一致性 | 订单与库存强一致性，支付最终一致性 |
| 安全性 | JWT 鉴权、XSS/CSRF/SQL 注入防护、敏感数据加密 |
| 可维护性 | 模块化分包、统一异常/日志处理、代码规范 |
| 扩展性 | 模块解耦，预留分布式改造空间 |

---

## 3. 技术架构

### 3.1 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                       Nginx (反向代理)                        │
├──────────────────────┬──────────────────────────────────────┤
│    Frontend (Vue 3)  │    Admin Frontend (Vue 3)            │
│    用户端 SPA         │    管理端 SPA                         │
├──────────────────────┴──────────────────────────────────────┤
│                    Spring Boot API Server                     │
│  ┌──────────┬──────────┬──────────┬──────────┬──────────┐   │
│  │ 用户模块  │ 商品模块  │ 购物车    │ 订单模块  │ 支付模块  │   │
│  ├──────────┼──────────┼──────────┼──────────┼──────────┤   │
│  │ 安全层    │ 缓存层    │ 日志层    │ 异常处理   │ 参数校验  │   │
│  └──────────┴──────────┴──────────┴──────────┴──────────┘   │
├─────────────────────────────────────────────────────────────┤
│              MySQL 8.0               Redis 7.0               │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 技术选型明细

#### 后端

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|---------|
| Spring Boot | 3.2+ | 基础框架 | 生态成熟，自动配置，内嵌 Tomcat |
| Spring Security | 6.x | 安全框架 | 与 Spring Boot 深度集成 |
| MyBatis | 3.0+ | ORM 框架 | XML 映射，SQL 透明可控 |
| PageHelper | 2.1+ | 分页插件 | 轻量级分页，无侵入 |
| Spring Cache + Redis | — | 缓存方案 | 注解式缓存，Redis 高性能 |
| Redisson | 3.25+ | 分布式锁 | 订单库存扣减等并发场景 |
| Knife4j | 4.0+ | API 文档 | 自动生成 Swagger 文档 |
| Lombok | — | 代码简化 | 消除样板代码 |
| Hutool | 5.8+ | 工具库 | 丰富工具类，减少重复造轮子 |
| MapStruct | 1.5+ | 对象转换 | 编译期生成，性能优于 BeanUtils |
| EasyExcel | 3.3+ | Excel 操作 | 管理端数据导出 |
| Java JWT | 4.4+ | Token 管理 | JWT 生成与解析 |

#### 数据库

| 技术 | 用途 |
|------|------|
| MySQL 8.0 | 主数据存储 |
| Redis 7.0 | Token 缓存、购物车缓存、热点数据缓存、分布式锁 |

#### 前端 (不在 Java 代码范围内，仅架构参考)

| 技术 | 用途 |
|------|------|
| Vue 3 + TypeScript | 前端框架 |
| Element Plus | UI 组件库 |
| Axios | HTTP 请求 |
| Pinia | 状态管理 |

### 3.3 关键技术方案

#### 3.3.1 用户认证 (JWT + Redis)

```
登录流程:
1. 用户提交用户名+密码
2. 服务端校验，生成 Access Token (2h) + Refresh Token (7d)
3. Access Token 返回给前端，Refresh Token 存入 Redis
4. 请求携带 Access Token，过期后用 Refresh Token 刷新
5. 登出时清除 Redis 中的 Refresh Token
```

#### 3.3.2 库存扣减 (Redis 分布式锁 + DB 乐观锁)

```
下单流程:
1. 使用 Redisson 分布式锁锁定商品 SKU
2. 校验库存是否充足
3. 扣减库存 (UPDATE ... SET stock = stock - ? WHERE stock >= ?)
4. 库存变更通过 MQ 异步同步缓存
5. 释放分布式锁
```

#### 3.3.3 购物车存储策略

```
策略:
- 登录用户: 购物车数据存入 Redis (Hash 结构，key = cart:userId, field = skuId)
- 未登录用户: 购物车数据存前端 localStorage，登录后合并到 Redis
- 数据持久化: 定时将 Redis 购物车数据异步写入 MySQL
```

#### 3.3.4 订单超时取消 (延迟队列)

```
方案: Redis 过期回调 + 定时任务兜底
1. 订单创建时，Redis 存储订单超时标记 (TTL = 30min)
2. Redis 过期回调监听，触发订单取消逻辑
3. 定时任务每 5 分钟扫描一次超时未支付订单，兜底取消
```

---

## 4. 数据库设计

### 4.1 ER 图核心实体

```
用户 (user)
  │
  ├── 1:N ─── 收货地址 (user_address)
  ├── 1:N ─── 购物车 (cart)
  ├── 1:N ─── 订单 (order)
  ├── 1:N ─── 商品收藏 (product_favorite)
  └── 1:N ─── 商品评价 (product_review)
  
商品分类 (category)
  │
  └── 1:N ─── 商品 (product)
                │
                ├── 1:N ─── 商品SKU (product_sku)
                ├── 1:N ─── 商品图片 (product_image)
                └── 1:N ─── 商品评价 (product_review)

订单 (order)
  │
  ├── 1:N ─── 订单商品项 (order_item)
  └── 1:1 ─── 支付记录 (payment_record)
```

### 4.2 核心表设计 (约 20 张表)

#### 4.2.1 用户相关

```sql
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
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-正常 1-已删除'
) COMMENT '用户表';

-- 收货地址表
CREATE TABLE `user_address` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `receiver_name` VARCHAR(32) NOT NULL COMMENT '收件人姓名',
    `receiver_phone` VARCHAR(16) NOT NULL COMMENT '收件人电话',
    `province` VARCHAR(32) NOT NULL COMMENT '省份',
    `city` VARCHAR(32) NOT NULL COMMENT '城市',
    `district` VARCHAR(32) NOT NULL COMMENT '区/县',
    `detail_address` VARCHAR(256) NOT NULL COMMENT '详细地址',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认: 0-否 1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`)
) COMMENT '收货地址表';
```

#### 4.2.2 商品相关

```sql
-- 商品分类表
CREATE TABLE `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID, 0代表一级分类',
    `name` VARCHAR(64) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(256) COMMENT '分类图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序权重',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
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
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_name` (`name`)
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
    `status` TINYINT DEFAULT 1,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_product_id` (`product_id`)
) COMMENT '商品SKU表';

-- 商品图片表
CREATE TABLE `product_image` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `image_url` VARCHAR(256) NOT NULL COMMENT '图片URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_product_id` (`product_id`)
) COMMENT '商品图片表';
```

#### 4.2.3 订单相关

```sql
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
    `remark` VARCHAR(512) COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_status` (`status`)
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
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '小计',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_order_id` (`order_id`)
) COMMENT '订单商品项表';
```

#### 4.2.4 支付相关

```sql
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
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_order_no` (`order_no`)
) COMMENT '支付记录表';
```

---

## 5. 接口设计概要

### 5.1 用户端 API (前缀: `/api`)

| 模块 | Method | URL | 说明 |
|------|--------|-----|------|
| 用户 | POST | `/api/user/register` | 用户注册 |
| 用户 | POST | `/api/user/login` | 用户登录 |
| 用户 | GET | `/api/user/profile` | 获取个人信息 |
| 用户 | PUT | `/api/user/profile` | 修改个人信息 |
| 用户 | GET | `/api/user/address` | 地址列表 |
| 用户 | POST | `/api/user/address` | 新增地址 |
| 用户 | PUT | `/api/user/address/{id}` | 修改地址 |
| 用户 | DELETE | `/api/user/address/{id}` | 删除地址 |
| 商品 | GET | `/api/categories` | 分类列表 |
| 商品 | GET | `/api/products` | 商品列表 (分页+筛选) |
| 商品 | GET | `/api/products/{id}` | 商品详情 |
| 商品 | GET | `/api/products/{id}/reviews` | 商品评价列表 |
| 购物车 | GET | `/api/cart` | 购物车列表 |
| 购物车 | POST | `/api/cart` | 添加商品到购物车 |
| 购物车 | PUT | `/api/cart/{skuId}` | 修改购物车商品 |
| 购物车 | DELETE | `/api/cart/{skuId}` | 删除购物车商品 |
| 订单 | POST | `/api/orders` | 创建订单 |
| 订单 | GET | `/api/orders` | 订单列表 |
| 订单 | GET | `/api/orders/{id}` | 订单详情 |
| 订单 | PUT | `/api/orders/{id}/cancel` | 取消订单 |
| 订单 | PUT | `/api/orders/{id}/confirm` | 确认收货 |
| 支付 | POST | `/api/pay/{orderNo}` | 发起支付 |

### 5.2 管理端 API (前缀: `/admin`)

| 模块 | Method | URL | 说明 |
|------|--------|-----|------|
| 认证 | POST | `/admin/login` | 管理员登录 |
| 商品 | POST | `/admin/products` | 新增商品 |
| 商品 | PUT | `/admin/products/{id}` | 编辑商品 |
| 商品 | DELETE | `/admin/products/{id}` | 删除商品 |
| 商品 | PUT | `/admin/products/{id}/status` | 上下架 |
| 分类 | POST | `/admin/categories` | 新增分类 |
| 分类 | PUT | `/admin/categories/{id}` | 编辑分类 |
| 分类 | DELETE | `/admin/categories/{id}` | 删除分类 |
| 订单 | GET | `/admin/orders` | 订单列表 |
| 订单 | PUT | `/admin/orders/{id}/deliver` | 发货 |
| 订单 | PUT | `/admin/orders/{id}/refund` | 处理退款 |
| 用户 | GET | `/admin/users` | 用户列表 |
| 用户 | PUT | `/admin/users/{id}/status` | 用户状态变更 |
| 统计 | GET | `/admin/statistics/overview` | 数据概览 |

---

## 6. 开发计划

### 6.1 开发阶段总览

本项目分为 **5 个阶段**，采用迭代式开发，每阶段产出可独立验证的交付物。

```
Phase 1 (3天)      Phase 2 (4天)      Phase 3 (5天)     Phase 4 (3天)     Phase 5 (2天)
   基础框架            核心业务            订单支付           后台管理           优化上线
┌─────────────┐     ┌─────────────┐     ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ 项目初始化    │     │ 商品模块     │     │ 购物车模块   │    │ 商品管理     │    │ 性能优化    │
│ 公共组件      │────▶│ 用户模块     │────▶│ 订单模块     │───▶│ 订单管理     │───▶│ 安全加固    │
│ 数据库建表    │     │ 分类模块     │     │ 支付模块     │    │ 用户管理     │    │ 文档完善    │
└─────────────┘     └─────────────┘     └─────────────┘    └─────────────┘    └─────────────┘
```

### 6.2 Phase 1: 基础框架搭建 (3 天)

**目标**: 搭建项目骨架，建立公共组件，创建数据库表结构。

#### 任务清单

| # | 任务 | 产出 | 预计时间 |
|---|------|------|---------|
| 1.1 | Spring Boot 项目初始化 | Maven 项目，引入所有依赖 | 2h |
| 1.2 | 配置多环境 (dev/test/prod) | application.yml 配置文件 | 1h |
| 1.3 | 统一返回结果封装 | `Result<T>` 类 | 0.5h |
| 1.4 | 全局异常处理器 | `GlobalExceptionHandler` | 1h |
| 1.5 | 异常体系定义 | `BusinessException` 等 | 0.5h |
| 1.6 | 响应码枚举 | `ResultCode` 枚举 | 0.5h |
| 1.7 | MyBatis 配置 | MyBatis 配置类 + PageHelper 配置 | 1h |
| 1.8 | Redis 配置 + Redisson 配置 | Redis/Redisson 配置类 | 1h |
| 1.9 | Spring Security + JWT 集成 | Security 配置、JWT 工具类 | 2h |
| 1.10 | Knife4j 接口文档配置 | Swagger 配置 | 0.5h |
| 1.11 | 参数校验全局配置 | Validation 配置 | 0.5h |
| 1.12 | 日志配置 (Logback) | logback-spring.xml | 0.5h |
| 1.13 | 数据库表 DDL 脚本 | 所有建表 SQL | 2h |
| 1.14 | 包结构初始化 | 所有包目录 + 基础类 | 0.5h |

**检验标准**:
- 项目可成功启动，无报错
- Swagger 页面可正常访问
- 数据库表创建完成，可正常连接
- `/api/hello` 测试接口返回统一格式 Result

---

### 6.3 Phase 2: 核心业务模块 (4 天)

**目标**: 完成用户系统与商品系统的全部接口。

#### 2.1 用户模块 (1.5 天)

| # | 任务 | 产出 |
|---|------|------|
| 2.1.1 | 用户注册接口 | `POST /api/user/register` |
| 2.1.2 | 用户登录接口 (含验证码) | `POST /api/user/login` |
| 2.1.3 | Token 刷新机制 | `POST /api/user/refresh` |
| 2.1.4 | 用户个人信息查询/修改 | `GET/PUT /api/user/profile` |
| 2.1.5 | 收货地址 CRUD | 地址增删改查全套接口 |
| 2.1.6 | 密码修改/重置 | 密码相关接口 |

#### 2.2 商品模块 (1.5 天)

| # | 任务 | 产出 |
|---|------|------|
| 2.2.1 | 分类树查询 | `GET /api/categories` |
| 2.2.2 | 商品列表 (分页+排序+筛选) | `GET /api/products` |
| 2.2.3 | 商品详情 (含SKU+图片) | `GET /api/products/{id}` |
| 2.2.4 | 商品搜索 (关键词) | `GET /api/products/search` |
| 2.2.5 | 商品评价列表 | `GET /api/products/{id}/reviews` |

#### 2.3 数据初始化 (0.5 天)

| # | 任务 | 产出 |
|---|------|------|
| 2.3.1 | 分类数据初始化脚本 | 预设商品分类 |
| 2.3.2 | 商品测试数据脚本 | 30+ 测试商品数据 |

#### 2.4 单元测试 (0.5 天)

| # | 任务 | 产出 |
|---|------|------|
| 2.4.1 | 用户模块单元测试 | 覆盖率 ≥ 80% |
| 2.4.2 | 商品模块单元测试 | 覆盖率 ≥ 80% |

**检验标准**:
- 用户可完成注册→登录→查看/修改信息→管理地址全流程
- 商品可按分类筛选、按关键词搜索、按价格排序
- 所有接口通过 Postman/Swagger 手动验证
- 单元测试全部通过

---

### 6.4 Phase 3: 订单与支付模块 (5 天)

**目标**: 完成购物车、订单、支付全流程闭环。

#### 3.1 购物车模块 (1 天)

| # | 任务 | 产出 |
|---|------|------|
| 3.1.1 | 添加商品到购物车 | `POST /api/cart` |
| 3.1.2 | 购物车列表查询 | `GET /api/cart` |
| 3.1.3 | 修改商品数量/规格 | `PUT /api/cart/{skuId}` |
| 3.1.4 | 删除购物车商品 | `DELETE /api/cart/{skuId}` |
| 3.1.5 | 购物车全选/取消全选 | `PUT /api/cart/select-all` |

#### 3.2 订单模块 (2 天)

| # | 任务 | 产出 |
|---|------|------|
| 3.2.1 | 订单创建（含库存校验+锁定） | `POST /api/orders` |
| 3.2.2 | 订单列表（按状态筛选） | `GET /api/orders` |
| 3.2.3 | 订单详情查询 | `GET /api/orders/{id}` |
| 3.2.4 | 订单取消（释放库存） | `PUT /api/orders/{id}/cancel` |
| 3.2.5 | 确认收货 | `PUT /api/orders/{id}/confirm` |
| 3.2.6 | 订单超时自动取消 | Redis 过期回调 + 定时任务 |
| 3.2.7 | 订单编号生成器 | 雪花算法或日期+序列号 |

#### 3.3 支付模块 (1.5 天)

| # | 任务 | 产出 |
|---|------|------|
| 3.3.1 | 支付发起接口 | `POST /api/pay/{orderNo}` |
| 3.3.2 | 支付回调处理 | 异步回调接口 |
| 3.3.3 | 支付结果查询 | `GET /api/pay/{orderNo}/result` |
| 3.3.4 | 退款处理 | `POST /admin/orders/{id}/refund` |

#### 3.4 单元测试 (0.5 天)

| # | 任务 | 产出 |
|---|------|------|
| 3.4.1 | 购物车模块测试 | 覆盖率 ≥ 80% |
| 3.4.2 | 订单模块测试 | 覆盖率 ≥ 80% |

**检验标准**:
- 用户可完成: 浏览商品→加购物车→下单→支付→发货→收货 全流程
- 订单超时自动取消正常运作
- 库存扣减并发场景无超卖
- 支付回调正确更新订单状态

---

### 6.5 Phase 4: 后台管理系统 (3 天)

**目标**: 完成管理端所有业务接口。

#### 4.1 管理员认证 (0.5 天)

| # | 任务 | 产出 |
|---|------|------|
| 4.1.1 | 管理员登录 | `POST /admin/login` |
| 4.1.2 | 管理员权限校验 | `@PreAuthorize` 角色注解 |

#### 4.2 商品管理 (1 天)

| # | 任务 | 产出 |
|---|------|------|
| 4.2.1 | 商品 CRUD | 商品增加/编辑/删除/列表 |
| 4.2.2 | 商品上下架 | `PUT /admin/products/{id}/status` |
| 4.2.3 | 分类管理 CRUD | 分类增加/编辑/删除/树 |
| 4.2.4 | SKU 库存管理 | 库存查询与调整 |

#### 4.3 订单管理 (0.5 天)

| # | 任务 | 产出 |
|---|------|------|
| 4.3.1 | 订单列表（多条件查询） | `GET /admin/orders` |
| 4.3.2 | 订单发货 | `PUT /admin/orders/{id}/deliver` |
| 4.3.3 | 退款处理 | `PUT /admin/orders/{id}/refund` |

#### 4.4 用户管理 (0.5 天)

| # | 任务 | 产出 |
|---|------|------|
| 4.4.1 | 用户列表查询 | `GET /admin/users` |
| 4.4.2 | 用户状态变更 | `PUT /admin/users/{id}/status` |

#### 4.5 数据统计 (0.5 天)

| # | 任务 | 产出 |
|---|------|------|
| 4.5.1 | 数据概览接口 | `GET /admin/statistics/overview` |
| 4.5.2 | 销售趋势统计 | `GET /admin/statistics/sales-trend` |

**检验标准**:
- 管理员可登录后台，管理商品/订单/用户
- 数据统计接口返回准确的汇总数据
- 管理端接口与用户端接口权限隔离

---

### 6.6 Phase 5: 优化与上线 (2 天)

**目标**: 性能优化、安全加固、文档完善、部署准备。

| # | 任务 | 产出 |
|---|------|------|
| 5.1 | 热点数据缓存优化 | 商品详情、分类数据 Redis 缓存 |
| 5.2 | 接口限流 | 使用 Redis 实现接口防刷 |
| 5.3 | SQL 索引优化 | 针对慢查询添加/调整索引 |
| 5.4 | XSS/CSRF 防护 | 安全过滤器 |
| 5.5 | 敏感信息脱敏 | 日志中手机号/密码脱敏 |
| 5.6 | Docker 部署脚本 | Dockerfile + docker-compose.yml |
| 5.7 | 接口文档完善 | Knife4j 文档注解补全 |
| 5.8 | 集成测试 | 核心业务流程端到端测试 |
| 5.9 | README 编写 | 项目说明、启动方式、部署文档 |

**检验标准**:
- 核心接口 P95 响应 < 500ms
- 通过 SQL 注入/XSS 安全测试
- Docker 一键部署成功
- 集成测试覆盖核心业务流程

---

## 7. 风险与应对

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|---------|
| 并发场景库存超卖 | 高 | 中 | 分布式锁 + DB 乐观锁双重保障 |
| 支付回调丢失 | 高 | 低 | 主动查询 + 定时对账 + 幂等处理 |
| 数据库性能瓶颈 | 高 | 中 | 合理索引 + Redis 缓存 + 读写分离预留 |
| JWT Token 泄漏 | 高 | 低 | 短有效期 AccessToken + HTTPS 传输 |
| 订单超卖后无法恢复 | 中 | 低 | 库存流水记录 + 人工介入流程 |
| 需求变更频繁 | 中 | 中 | 模块解耦设计，接口版本化管理 |

---

## 8. 附录

### 8.1 开发环境要求

| 工具 | 版本 |
|------|------|
| JDK | 17+ |
| Maven | 3.9+ |
| MySQL | 8.0+ |
| Redis | 7.0+ |
| IDE | IntelliJ IDEA 2024+ |
| Git | 2.40+ |

### 8.2 代码规范

本项目所有 Java 代码严格遵循项目根目录下的 [CLAUDE.md](./CLAUDE.md) 编码规范，主要包括：

- 统一的包结构与分层架构
- Controller → Service → Mapper 三层职责分明
- 统一返回结果 `Result<T>` 包装
- 统一异常处理体系
- DTO/VO 分离，参数校验规范
- MyBatis XML 映射 + 动态 SQL
- 构造器注入 (`@RequiredArgsConstructor`)
- 完整的日志、安全、测试规范

代码生成、提交、审查全过程均需参照 CLAUDE.md 执行。

### 8.3 项目结构 (完整)

```
shop-online/
├── pom.xml
├── CLAUDE.md                            # 编码规范
├── plan.md                              # 本文件 — 需求与开发计划
├── README.md
├── docker-compose.yml
├── Dockerfile
├── sql/
│   └── init.sql                         # 数据库初始化脚本
└── src/
    ├── main/
    │   ├── java/com/shop/online/
    │   │   ├── ShopOnlineApplication.java
    │   │   ├── common/
    │   │   │   ├── annotation/
    │   │   │   ├── aspect/
    │   │   │   ├── config/
    │   │   │   ├── constant/
    │   │   │   ├── enums/
    │   │   │   ├── exception/
    │   │   │   ├── handler/
    │   │   │   ├── interceptor/
    │   │   │   ├── result/
    │   │   │   └── utils/
    │   │   ├── module/
    │   │   │   ├── user/
    │   │   │   │   ├── controller/
    │   │   │   │   ├── service/impl/
    │   │   │   │   ├── mapper/
    │   │   │   │   ├── entity/
    │   │   │   │   ├── dto/
    │   │   │   │   ├── vo/
    │   │   │   │   └── converter/
    │   │   │   ├── product/
    │   │   │   ├── order/
    │   │   │   ├── cart/
    │   │   │   ├── payment/
    │   │   │   └── admin/
    │   │   └── infrastructure/
    │   │       ├── security/
    │   │       ├── cache/
    │   │       ├── mq/
    │   │       └── oss/
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       └── logback-spring.xml
    └── test/
        └── java/com/shop/online/
            ├── module/
            │   ├── user/
            │   ├── product/
            │   ├── order/
            │   └── cart/
            └── common/
```

### 8.4 Maven 依赖清单

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>

    <!-- MyBatis -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>3.0.4</version>
    </dependency>

    <!-- PageHelper 分页插件 -->
    <dependency>
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
        <version>2.0.0</version>
    </dependency>

    <!-- MySQL -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>

    <!-- Redisson -->
    <dependency>
        <groupId>org.redisson</groupId>
        <artifactId>redisson-spring-boot-starter</artifactId>
        <version>3.25.2</version>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>com.auth0</groupId>
        <artifactId>java-jwt</artifactId>
        <version>4.4.0</version>
    </dependency>

    <!-- Knife4j -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        <version>4.4.0</version>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Hutool -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>5.8.28</version>
    </dependency>

    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>

    <!-- EasyExcel -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>easyexcel</artifactId>
        <version>3.3.4</version>
    </dependency>

    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

> **下一步**: 执行 `Phase 1` 开发计划时，请严格参照 [CLAUDE.md](./CLAUDE.md) 中的 Java 编码规范生成代码。
