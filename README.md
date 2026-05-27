# 05-mybatis

Spring Boot + MyBatis 示例项目，演示 MyBatis 数据访问、Redis 缓存、动态定时任务等常用功能。

## 技术栈

| 技术 | 说明 |
|------|------|
| Spring Boot | 应用框架 |
| MyBatis | ORM 数据访问层 |
| H2 | 内存数据库（开发环境） |
| Redis | 缓存（Lettuce 连接池） |
| Spring Cache | `@Cacheable` / `@CachePut` / `@CacheEvict` 注解缓存 |
| Thymeleaf | 服务端模板引擎 |
| springdoc-openapi | Swagger UI 接口文档 |
| Lombok | 减少样板代码 |

## 快速启动

```bash
# 确保本地 Redis 已启动（默认 localhost:6379）
mvn spring-boot:run
```

服务启动后监听 `http://localhost:8081`。

## H2 控制台

| 项目 | 值 |
|------|----|
| 控制台地址 | http://localhost:8081/h2-console |
| JDBC URL | `jdbc:h2:mem:mybatisdb` |
| Username | `sa` |
| Password | `sa` |

> H2 为内存数据库，应用重启后数据重置，初始数据由 `src/main/resources/db/schema.sql` 和 `data.sql` 加载。

## 数据库表

| 表 | 说明 |
|----|------|
| `users` | 用户表（id, name, age） |
| `cron` | 动态定时任务配置表（id, cron, description） |

## 主要 API

所有接口基础路径：`/users`

### 新增用户

| 接口 | 方法 | 说明 |
|------|------|------|
| `/users/addUser` | POST (form) | 表单参数提交 |
| `/users/addUser1` | POST (JSON) | JSON body，含参数校验 |
| `/users/addUser3` | POST (JSON) | 含异步事件 |
| `/users/addUser4` | POST (JSON) | 无事务提交 |

### 查询用户

| 接口 | 方法 | 说明 |
|------|------|------|
| `/users/findAll` | GET | 返回页面（Thymeleaf） |
| `/users/findAll1` | GET | 返回 JSON 列表 |
| `/users/findUserById?id=1` | GET | 返回页面 |
| `/users/findUserById1?id=1` | GET | 返回 JSON |
| `/users/findUserByIdCache?id=1` | GET | 从 Redis 缓存读取 |

### 更新用户

| 接口 | 方法 | 说明 |
|------|------|------|
| `/users/updateUser` | POST (form) | 表单参数，直接写库 |
| `/users/updateUser1` | PUT (JSON) | JSON body，直接写库 |
| `/users/updateUserInCache` | PUT (JSON) | 写库并更新 Redis 缓存 |

### 删除用户

| 接口 | 方法 | 说明 |
|------|------|------|
| `/users/deleteUser?id=1` | DELETE | 按 ID 删除 |

### Redis 操作

| 接口 | 方法 | 说明 |
|------|------|------|
| `/users/putCache` | POST | 写入 Redis |
| `/users/findCache` | GET | 读取 Redis |
| `/users/findAllCache` | GET | 获取 cacheName 下所有 key |
| `/users/getTtl` | GET | 检查 key 是否存在 / TTL |

## Swagger UI

```
http://localhost:8081/swagger-ui/index.html
```

## Redis 配置

默认连接本地 Redis，如需修改请编辑 `src/main/resources/application.properties`：

```properties
spring.data.redis.host = localhost
spring.data.redis.port = 6379
spring.data.redis.password =
```
