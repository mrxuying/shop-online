# Shop-Online 电商网站

B2C 单体电商网站后端服务，基于 Spring Boot 3.2 + MyBatis + Redis + MySQL 构建。

## 技术栈

| 层次 | 技术 | 版本 |
|------|------|------|
| 基础框架 | Spring Boot | 3.2+ |
| 安全框架 | Spring Security + JWT | 6.x |
| ORM | MyBatis | 3.5+ |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis + Redisson | 7.0+ |
| 接口文档 | Knife4j (Swagger) | 4.0+ |
| 工具库 | Lombok、Hutool、MapStruct | — |
| 构建工具 | Maven | 3.9+ |
| JDK | OpenJDK | 17+ |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.9+
- MySQL 8.0+
- Redis 7.0+

### 1. 初始化数据库

```bash
mysql -u root -p < sql/init.sql
```

### 2. 修改配置

编辑 `src/main/resources/application-dev.yml`，修改数据库和 Redis 连接信息。

### 3. 启动应用

```bash
mvn spring-boot:run
```

### 4. 访问接口文档

- Swagger UI: http://localhost:8080/swagger-ui.html
- Knife4j 增强文档: http://localhost:8080/doc.html

### Docker 一键部署

```bash
docker-compose up -d
```

默认管理员账号: `admin` / `admin123`

## 项目结构

```
src/main/java/com/shop/online/
├── ShopOnlineApplication.java        # 启动类
├── common/                           # 公共模块
│   ├── annotation/                   # 自定义注解 (@RateLimit)
│   ├── aspect/                       # AOP 切面 (限流)
│   ├── config/                       # 配置类
│   ├── constant/                     # 常量
│   ├── enums/                        # 枚举
│   ├── exception/                    # 异常定义
│   ├── handler/                      # 全局异常处理器
│   ├── result/                       # 统一返回 (Result、PageResult)
│   └── utils/                        # 工具类
├── module/                           # 业务模块
│   ├── user/                         # 用户模块
│   ├── product/                      # 商品模块
│   ├── cart/                         # 购物车模块
│   ├── order/                        # 订单模块
│   ├── payment/                      # 支付模块
│   └── admin/                        # 管理后台模块
└── infrastructure/                   # 基础设施
    ├── security/                     # JWT、Spring Security
    ├── cache/                        # 缓存抽象
    ├── mq/                           # 消息/定时任务
    └── oss/                          # 对象存储
```

## API 接口概览

### 用户端 (前缀: `/api`)

| Method | URL | 说明 |
|--------|-----|------|
| POST | `/api/user/register` | 用户注册 |
| POST | `/api/user/login` | 用户登录 |
| POST | `/api/user/refresh` | Token 刷新 |
| GET | `/api/user/profile` | 个人信息 |
| PUT | `/api/user/profile` | 修改信息 |
| PUT | `/api/user/password` | 修改密码 |
| GET | `/api/user/address` | 地址列表 |
| POST | `/api/user/address` | 新增地址 |
| PUT | `/api/user/address/{id}` | 修改地址 |
| DELETE | `/api/user/address/{id}` | 删除地址 |
| GET | `/api/categories` | 分类树 |
| GET | `/api/products` | 商品列表 |
| GET | `/api/products/search` | 商品搜索 |
| GET | `/api/products/{id}` | 商品详情 |
| GET | `/api/products/{id}/reviews` | 商品评价 |
| GET | `/api/cart` | 购物车 |
| POST | `/api/cart` | 添加到购物车 |
| PUT | `/api/cart/{skuId}` | 修改数量 |
| DELETE | `/api/cart/{skuId}` | 删除商品 |
| PUT | `/api/cart/select-all` | 全选/取消 |
| POST | `/api/orders` | 创建订单 |
| GET | `/api/orders` | 订单列表 |
| GET | `/api/orders/{id}` | 订单详情 |
| PUT | `/api/orders/{id}/cancel` | 取消订单 |
| PUT | `/api/orders/{id}/confirm` | 确认收货 |
| POST | `/api/pay/{orderNo}` | 发起支付 |
| GET | `/api/pay/{orderNo}/result` | 支付结果 |

### 管理端 (前缀: `/admin`)

| Method | URL | 说明 |
|--------|-----|------|
| POST | `/admin/login` | 管理员登录 |
| GET | `/admin/products` | 商品列表 |
| POST | `/admin/products` | 新增商品 |
| PUT | `/admin/products/{id}` | 编辑商品 |
| DELETE | `/admin/products/{id}` | 删除商品 |
| PUT | `/admin/products/{id}/status` | 上下架 |
| GET | `/admin/categories` | 分类树 |
| POST | `/admin/categories` | 新增分类 |
| PUT | `/admin/categories/{id}` | 编辑分类 |
| DELETE | `/admin/categories/{id}` | 删除分类 |
| GET | `/admin/orders` | 订单列表 |
| PUT | `/admin/orders/{id}/deliver` | 发货 |
| PUT | `/admin/orders/{id}/refund` | 退款 |
| GET | `/admin/users` | 用户列表 |
| PUT | `/admin/users/{id}/status` | 状态变更 |
| GET | `/admin/statistics/overview` | 数据概览 |
| GET | `/admin/statistics/sales-trend` | 销售趋势 |

## 核心特性

- **JWT 双 Token 认证**: Access Token (2h) + Refresh Token (7d)，支持无感刷新
- **分布式锁**: Redisson 实现库存扣减防超卖
- **Redis 购物车**: Hash 结构存储，支持登录合并
- **订单超时取消**: Redis 过期标记 + 定时任务兜底
- **接口限流**: 基于 Redis 的滑动窗口计数
- **XSS 防护**: 请求参数 HTML 过滤
- **统一异常处理**: 全局异常拦截 + 统一返回格式
- **手机号脱敏**: 日志和返回值中自动脱敏
- **自动填充**: 创建时间/更新时间自动处理

## 开发规范

本项目严格遵循 [CLAUDE.md](./CLAUDE.md) 中的 Java 编码规范：
- Controller → Service → Mapper 三层架构
- `@RequiredArgsConstructor` 构造器注入
- DTO 接收参数 + `@Valid` 校验，VO 返回数据
- 统一返回结果 `Result<T>` 包装
- 统一异常体系 `BusinessException`
- 方法命名: `get`/`list`/`page`/`create`/`update`/`delete`
- 日期使用 `LocalDateTime`，密码 BCrypt 加密

## 运行测试

```bash
mvn test
```

## License

MIT
