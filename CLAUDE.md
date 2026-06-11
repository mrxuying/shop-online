# CLAUDE.md — Java 编码规范与最佳实践

本文件定义了 shop-online 电商项目的 Java 代码编写规范。所有代码生成、重构、审查均需严格遵守本文档。

---

## 一、项目技术栈

| 层次 | 技术选型 | 版本要求 |
|------|---------|---------|
| 基础框架 | Spring Boot | 3.2+ |
| JDK | Java | 17+ (LTS) |
| 构建工具 | Maven | 3.9+ |
| ORM | MyBatis | 3.0+ |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis (Spring Cache + Redisson) | 7.0+ |
| 消息队列 | RocketMQ / RabbitMQ | — |
| 接口文档 | Knife4j (Swagger) | 4.0+ |
| 单元测试 | JUnit 5 + Mockito | — |
| 工具库 | Lombok、Hutool、MapStruct | — |
| 参数校验 | Jakarta Validation (Hibernate Validator) | — |

---

## 二、项目包结构规范

```
com.shop.online
├── ShopOnlineApplication.java          # 启动类
├── common/                             # 公共模块
│   ├── annotation/                     # 自定义注解
│   ├── aspect/                         # AOP 切面
│   ├── config/                         # 配置类
│   ├── constant/                       # 常量
│   ├── enums/                          # 枚举
│   ├── exception/                      # 异常定义
│   ├── handler/                        # 全局异常处理器
│   ├── interceptor/                    # 拦截器
│   ├── result/                         # 统一返回结果
│   └── utils/                          # 工具类
├── module/                             # 业务模块 (按领域划分)
│   ├── user/                           # 用户模块
│   │   ├── controller/
│   │   ├── service/
│   │   │   └── impl/
│   │   ├── mapper/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── vo/
│   │   └── converter/                 # MapStruct 转换器
│   ├── product/                        # 商品模块
│   ├── order/                          # 订单模块
│   ├── cart/                           # 购物车模块
│   ├── payment/                        # 支付模块
│   └── admin/                          # 管理后台模块
└── infrastructure/                     # 基础设施
    ├── security/                        # 安全相关 (JWT、Spring Security)
    ├── cache/                           # 缓存抽象
    ├── mq/                              # 消息队列
    └── oss/                             # 对象存储
```

**规则:**
- Controller、Service、Mapper、Entity 等按层分包，严禁按功能横向分包
- 公共代码放入 `common` 包，严禁跨模块直接调用 Mapper
- 模块间调用必须通过 Service 接口，严禁循环依赖

---

## 三、命名规范

### 3.1 类命名

| 类型 | 命名规则 | 示例 |
|------|---------|------|
| Controller | `{模块}Controller` | `UserController` |
| Service 接口 | `I{模块}Service` | `IUserService` |
| Service 实现 | `{模块}ServiceImpl` | `UserServiceImpl` |
| Mapper | `{模块}Mapper` | `UserMapper` |
| Entity | `{表名驼峰}` | `User`、`OrderItem` |
| DTO | `{操作}{模块}DTO` | `UserLoginDTO`、`OrderCreateDTO` |
| VO | `{模块}{场景}VO` | `UserProfileVO`、`ProductDetailVO` |
| Converter | `{模块}Converter` | `UserConverter` |
| Enum | `{含义}Enum` | `OrderStatusEnum` |
| Exception | `{含义}Exception` | `BusinessException` |
| Config | `{模块}Config` | `RedisConfig` |
| Utils | `{模块}Utils` | `JwtUtils` |

### 3.2 方法命名

- 查询单条: `get{Entity}(Long id)` → `getUser(1L)`
- 查询列表: `list{Entity}({Entity}QueryDTO)` → `listUsers(dto)`
- 分页查询: `page{Entity}({Entity}PageDTO)` → `pageUsers(dto)`
- 创建: `create{Entity}({Entity}CreateDTO)` → `createUser(dto)`
- 更新: `update{Entity}({Entity}UpdateDTO)` → `updateUser(dto)`
- 删除: `delete{Entity}(Long id)` → `deleteUser(1L)`
- 批量操作: `batch{操作}{Entity}(List<Long>)` → `batchDeleteUsers(ids)`

### 3.3 变量命名

- 布尔变量: `isXxx`、`hasXxx`、`canXxx` (例: `isDeleted`、`hasChildren`)
- 集合变量: 复数形式或 `{name}List`/`{name}Map` (例: `users`、`userIdList`)
- 数据库字段: 下划线命名 (例: `create_time`)
- Java 属性: 驼峰命名 (例: `createTime`)
- 常量: 全大写下划线 (例: `MAX_RETRY_COUNT`)

---

## 四、代码风格

### 4.1 通用规则

```java
// 【正确】使用 Lombok 简化代码 — Entity 为纯 POJO
@Data
public class User {
    private Long id;
    private String username;
    private LocalDateTime createTime;
}

// 【正确】Controller 层 — 职责单一，仅做参数接收与结果返回
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class UserController {

    private final IUserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    public Result<UserDetailVO> getUser(@PathVariable Long id) {
        return Result.success(userService.getUserDetail(id));
    }
}

// 【正确】Service 层 — 业务逻辑，不包含 Controller 职责
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final UserConverter userConverter;

    @Override
    public UserDetailVO getUserDetail(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return userConverter.toDetailVO(user);
    }
}
```

### 4.2 禁止事项

- **禁止** 在 Controller 中写业务逻辑
- **禁止** 在 Service 中直接操作 HttpServletRequest/HttpServletResponse
- **禁止** 使用 `Map<String, Object>` 作为方法返回值 (应用 VO/DTO)
- **禁止** 使用 `System.out.println` 输出日志 (必须使用 Slf4j)
- **禁止** 使用 `e.printStackTrace()` (必须通过日志框架输出)
- **禁止** try-catch 后吞掉异常不处理
- **禁止** 方法超过 80 行 (超过必须拆分)
- **禁止** 类超过 500 行
- **禁止** 使用魔法值 (必须定义为常量或枚举)
- **禁止** JSON.parseObject 等工具在循环内重复创建

### 4.3 推荐事项

- **推荐** 使用构造器注入 (`@RequiredArgsConstructor`) 而非 `@Autowired`
- **推荐** 使用 `Optional` 处理可能为 null 的值
- **推荐** 使用 `Stream API` 处理集合操作
- **推荐** 日期使用 `LocalDateTime` / `LocalDate`，禁止使用 `java.util.Date`
- **推荐** 大段 if-else 使用策略模式或枚举重构

---

## 五、数据库访问规范

### 5.1 MyBatis 规则

```java
// Entity 只与数据库表一一对应，不包含业务逻辑
@Data
public class Product {
    private Long id;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}

// Mapper 接口 — 定义数据库操作方法
@Mapper
public interface ProductMapper {
    int insert(Product product);
    int updateById(Product product);
    int deleteById(@Param("id") Long id);
    Product selectById(@Param("id") Long id);
    List<Product> selectList(@Param("query") Product query);
    Long selectCount(@Param("query") Product query);
    Product selectOne(@Param("query") Product query);
}
```

```xml
<!-- Mapper XML — 复杂查询使用 XML 映射，SQL 关键字大写 -->
<mapper namespace="com.shop.online.module.product.mapper.ProductMapper">
    <resultMap id="BaseResultMap" type="com.shop.online.module.product.entity.Product">
        <id column="id" property="id"/>
        <result column="category_id" property="categoryId"/>
        <!-- ... -->
    </resultMap>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO product (category_id, name, price, ...)
        VALUES (#{categoryId}, #{name}, #{price}, ...)
    </insert>

    <select id="selectList" resultMap="BaseResultMap">
        SELECT * FROM product
        <where>
            is_deleted = 0
            <if test="query.categoryId != null">AND category_id = #{query.categoryId}</if>
            <if test="query.name != null and query.name != ''">
                AND name LIKE CONCAT('%', #{query.name}, '%')
            </if>
        </where>
    </select>
</mapper>
```

- **必须** Entity 与表结构严格对应，不包含关联查询结果字段
- **必须** 逻辑删除字段 `is_deleted` 在 XML 的 WHERE 子句中显式过滤
- **必须** `createTime` 和 `updateTime` 在 Service 层手动设置 (`LocalDateTime.now()`)
- **推荐** 简单查询通过实体字段传参 + XML 动态 SQL 实现
- **推荐** 复杂查询写在 Mapper XML 中，SQL 关键字大写

### 5.2 分页查询

```java
// 统一使用 PageHelper 分页插件
public PageResult<UserVO> pageUsers(UserPageDTO dto) {
    User query = new User();
    query.setStatus(dto.getStatus());
    if (StringUtils.hasText(dto.getKeyword())) {
        query.setUsername(dto.getKeyword());
    }
    PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
    PageHelper.orderBy("create_time desc");
    List<User> list = userMapper.selectList(query);
    PageInfo<User> pageInfo = new PageInfo<>(list);
    List<UserVO> records = list.stream().map(userConverter::toVO).toList();
    return PageResult.of(pageInfo.getTotal(), dto.getPageNum(), dto.getPageSize(), records);
}
```

---

## 六、异常处理规范

### 6.1 异常体系

```
RuntimeException
├── BusinessException          # 业务异常 (用户可见的错误提示)
├── AuthException              # 认证异常
├── ForbiddenException         # 权限异常
└── SystemException            # 系统异常 (用户不可见的内部错误)
```

### 6.2 异常使用规则

```java
// 【正确】抛出业务异常，由全局异常处理器统一处理
throw new BusinessException(ResultCode.USER_NOT_FOUND);

// 【正确】全局异常处理器
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(SystemException.class)
    public Result<Void> handleSystemException(SystemException e) {
        log.error("系统异常", e);
        return Result.error(ResultCode.SYSTEM_ERROR);
    }
}

// 【错误】吞掉异常
try {
    doSomething();
} catch (Exception e) {
    // 空处理 — 绝对禁止
}
```

### 6.3 统一返回结果

```java
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) { ... }
    public static <T> Result<T> error(Integer code, String message) { ... }
}
```

- 所有 Controller 返回值必须使用 `Result<T>` 包装
- 状态码统一定义在 `ResultCode` 枚举中

---

## 七、参数校验规范

```java
// DTO 使用 Jakarta Validation 注解
@Data
public class UserRegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度3-20位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度6-32位")
    private String password;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}

// Controller 中开启校验
@PostMapping("/register")
public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
    userService.register(dto);
    return Result.success();
}
```

---

## 八、日志规范

```java
@Slf4j  // 使用 Lombok 注解
public class OrderServiceImpl implements IOrderService {

    public void cancelOrder(Long orderId) {
        log.info("开始取消订单, orderId={}", orderId);     // 关键业务节点 → INFO
        log.debug("查询订单详情, orderId={}", orderId);     // 调试信息 → DEBUG
        log.warn("订单库存不足, orderId={}", orderId);      // 需关注 → WARN
        log.error("取消订单失败, orderId={}", orderId, e);  // 异常 → ERROR
    }
}
```

- 日志占位符使用 `{}`，禁止字符串拼接
- 异常信息必须携带异常对象作为最后一个参数
- 生产环境禁止输出用户敏感信息 (密码、手机号脱敏)

---

## 九、API 设计规范

### 9.1 RESTful URL 设计

| Method | URL | 说明 |
|--------|-----|------|
| GET | `/api/products` | 商品列表 (分页+筛选) |
| GET | `/api/products/{id}` | 商品详情 |
| POST | `/api/products` | 新增商品 |
| PUT | `/api/products/{id}` | 更新商品 |
| DELETE | `/api/products/{id}` | 删除商品 |
| GET | `/api/categories/{id}/products` | 某分类下的商品 |

### 9.2 分页请求/响应

```java
@Data
public class PageQuery {
    @Min(1)
    private Integer pageNum = 1;

    @Min(1)
    @Max(100)
    private Integer pageSize = 20;
}

@Data
public class PageResult<T> {
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<T> records;
}
```

---

## 十、安全规范

- **必须** 密码使用 BCrypt 加密存储，禁止明文
- **必须** JWT Token 设置过期时间，过期后强制刷新
- **必须** 用户输入做 XSS 过滤
- **必须** SQL 查询使用参数化，防止注入
- **必须** 敏感接口做权限控制 (`@PreAuthorize`)
- **必须** 接口做防刷限流 (使用 Redis + 注解)
- **推荐** 关键操作记录审计日志

---

## 十一、测试规范

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("获取用户详情 — 用户存在")
    void shouldReturnUserWhenUserExists() {
        // Given
        User user = new User();
        user.setId(1L);
        when(userMapper.selectById(1L)).thenReturn(user);

        // When
        UserDetailVO result = userService.getUserDetail(1L);

        // Then
        assertNotNull(result);
        verify(userMapper).selectById(1L);
    }

    @Test
    @DisplayName("获取用户详情 — 用户不存在")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> userService.getUserDetail(999L));
    }
}
```

- **必须** 测试方法使用 `@DisplayName` 说明测试场景
- **必须** 覆盖正常流程 + 异常流程 + 边界值
- **推荐** 使用 Given-When-Then 结构
- **推荐** Service 层单元测试覆盖率 ≥ 80%

---

## 十二、Git 提交规范

```
<type>(<scope>): <subject>

type 类型:
  feat      — 新功能
  fix       — 修复 Bug
  refactor  — 重构
  docs      — 文档变更
  style     — 代码格式 (不影响代码运行)
  test      — 测试用例
  chore     — 构建过程或辅助工具的变动
  perf      — 性能优化

示例:
  feat(user): 添加用户注册接口
  fix(order): 修复订单金额计算错误
  refactor(product): 重构商品查询逻辑
```

---

## 十三、代码提交前自检清单

在每次生成代码后，确认以下各项:

- [ ] Controller 仅做参数接收与结果返回，不包含业务逻辑
- [ ] Service 不直接操作 HttpServletRequest
- [ ] 方法命名符合规范 (get/list/page/create/update/delete)
- [ ] 使用 DTO 接收参数，VO 返回数据
- [ ] 异常使用统一异常体系，不吞异常
- [ ] Controller 返回值使用 `Result<T>` 包装
- [ ] 使用构造器注入 (`@RequiredArgsConstructor`)
- [ ] 参数校验使用 Jakarta Validation 注解
- [ ] 日期使用 `LocalDateTime`，禁止 `Date`
- [ ] 日志使用 `@Slf4j` + 占位符
- [ ] 禁止魔法值，使用枚举或常量
- [ ] 类 < 500 行，方法 < 80 行
- [ ] 密码加密存储，敏感信息不打印到日志
- [ ] 测试用例使用 `@DisplayName` + Given-When-Then
