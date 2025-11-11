# 代码片段管理系统（Copyer）后端服务

## 项目简介

Copyer 是一个代码片段管理系统的后端服务，为前端React应用提供RESTful API接口。用户可以创建、管理、分享代码片段，支持多种编程语言、标签分类、搜索、点赞、收藏等功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.6 | 核心框架 |
| MyBatis-Plus | 3.5.9 | ORM 框架 |
| Sa-Token | 1.39.0 | 权限认证框架 |
| Redis | - | 分布式缓存 + Session |
| Caffeine | - | 本地缓存 |
| Knife4j | 4.4.0 | API 文档 |
| MapStruct | 1.5.0 | 对象转换 |
| Lombok | - | 代码生成 |
| Hutool | 5.8.16 | 工具类库 |
| MySQL | 8.0+ | 数据库 |

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 3.0+

### 数据库初始化

1. 创建数据库：

```sql
CREATE DATABASE copyer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：

```bash
mysql -u root -p copyer < sql/init_db.sql
```

初始化脚本会自动创建以下表：
- user（用户表）
- code_snippet（代码片段表）
- tag（标签表）
- snippet_tag（代码片段-标签关联表）
- favorite（收藏表）
- snippet_like（点赞表）

### 配置文件

修改 `src/main/resources/application.yml` 配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/copyer?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
    password:  # 本地开发可留空
```

### 启动应用

```bash
# 清理构建（跳过测试）
mvn clean package -Dmaven.test.skip=true

# 运行应用
java -jar target/copyer-backend-1.0.0.jar

# 或直接使用 Maven 运行
mvn spring-boot:run
```

### 访问应用

- 应用地址: http://localhost:8080/api
- API 文档: http://localhost:8080/api/doc.html

## 核心功能

### 1. 用户认证

- 用户注册
- 用户登录/登出
- 基于 Sa-Token 的会话管理
- Token 自动刷新

### 2. 代码片段管理

- 创建代码片段
- 更新代码片段
- 删除代码片段（逻辑删除）
- 获取代码片段详情
- 分页查询代码片段
- 关键词搜索（支持标题、描述、代码内容）
- 按编程语言筛选
- 按标签筛选
- 多种排序方式（创建时间、更新时间、浏览量、点赞数、收藏数）

### 3. 标签管理

- 创建标签（仅管理员）
- 更新标签（仅管理员）
- 删除标签（仅管理员）
- 获取标签列表
- 标签与代码片段关联

### 4. 互动功能

- 点赞代码片段
- 取消点赞
- 收藏代码片段
- 取消收藏
- 浏览次数统计

## API 接口文档

### 用户相关接口

#### 注册用户
- **接口**: `POST /api/user/register`
- **说明**: 用户注册
- **请求参数**:
```json
{
  "account": "string",       // 账号（必填）
  "password": "string",      // 密码（必填，6-20位）
  "checkPassword": "string"  // 确认密码（必填）
}
```
- **响应**: 返回用户ID

#### 用户登录
- **接口**: `POST /api/user/login`
- **说明**: 用户登录
- **请求参数**:
```json
{
  "account": "string",   // 账号
  "password": "string"   // 密码
}
```
- **响应**: 返回用户信息和Token

#### 获取当前用户信息
- **接口**: `GET /api/user/get/login`
- **说明**: 获取当前登录用户信息
- **响应**: 返回用户信息

#### 用户登出
- **接口**: `GET /api/user/logout`
- **说明**: 用户登出
- **响应**: 返回成功状态

### 代码片段相关接口

#### 创建代码片段
- **接口**: `POST /api/snippet/create`
- **说明**: 创建代码片段（需要登录）
- **请求参数**:
```json
{
  "title": "string",                  // 标题（必填，1-200字符）
  "code": "string",                   // 代码内容（必填，最大100KB）
  "language": "string",               // 编程语言（必填）
  "theme": "vs-dark",                 // 编辑器主题（可选）
  "description": "string",            // 简短描述（可选，最大500字符）
  "descriptionMarkdown": "string",    // Markdown描述（可选）
  "expiryDate": "2024-12-31T23:59:59",// 过期时间（可选）
  "isPublic": 1,                      // 是否公开（0-私有，1-公开）
  "tagIds": [1, 2, 3]                 // 标签ID列表（可选，最多10个）
}
```
- **响应**: 返回代码片段ID

#### 更新代码片段
- **接口**: `PUT /api/snippet/update`
- **说明**: 更新代码片段（需要登录且为创建者）
- **请求参数**:
```json
{
  "id": 1,                            // 代码片段ID（必填）
  "title": "string",                  // 标题（可选）
  "code": "string",                   // 代码内容（可选）
  "language": "string",               // 编程语言（可选）
  "theme": "vs-dark",                 // 编辑器主题（可选）
  "description": "string",            // 简短描述（可选）
  "descriptionMarkdown": "string",    // Markdown描述（可选）
  "expiryDate": "2024-12-31T23:59:59",// 过期时间（可选）
  "isPublic": 1,                      // 是否公开（可选）
  "tagIds": [1, 2, 3]                 // 标签ID列表（可选）
}
```
- **响应**: 返回成功状态

#### 删除代码片段
- **接口**: `DELETE /api/snippet/delete`
- **说明**: 删除代码片段（需要登录且为创建者）
- **请求参数**:
```json
{
  "id": 1  // 代码片段ID
}
```
- **响应**: 返回成功状态

#### 获取代码片段详情
- **接口**: `GET /api/snippet/get/{id}`
- **说明**: 获取代码片段详情（自动增加浏览次数）
- **路径参数**: id - 代码片段ID
- **响应**: 返回代码片段详细信息

#### 分页查询代码片段
- **接口**: `POST /api/snippet/list/page`
- **说明**: 分页查询代码片段列表
- **请求参数**:
```json
{
  "current": 1,              // 当前页码（默认1）
  "pageSize": 10,            // 每页大小（默认10）
  "id": 1,                   // 代码片段ID（可选）
  "userId": 1,               // 创建用户ID（可选）
  "searchText": "keyword",   // 搜索关键词（可选，搜索标题、描述、代码）
  "language": "JavaScript",  // 编程语言（可选）
  "tagIds": [1, 2],          // 标签ID列表（可选）
  "isPublic": 1,             // 是否公开（可选）
  "sortField": "createTime", // 排序字段（可选：createTime, updateTime, viewCount, likeCount, favoriteCount）
  "sortOrder": "desc"        // 排序方式（可选：asc, desc）
}
```
- **响应**: 返回分页数据

#### 点赞代码片段
- **接口**: `POST /api/snippet/like/{id}`
- **说明**: 点赞代码片段（需要登录）
- **路径参数**: id - 代码片段ID
- **响应**: 返回成功状态

#### 取消点赞代码片段
- **接口**: `POST /api/snippet/unlike/{id}`
- **说明**: 取消点赞代码片段（需要登录）
- **路径参数**: id - 代码片段ID
- **响应**: 返回成功状态

#### 收藏代码片段
- **接口**: `POST /api/snippet/favorite/{id}`
- **说明**: 收藏代码片段（需要登录）
- **路径参数**: id - 代码片段ID
- **响应**: 返回成功状态

#### 取消收藏代码片段
- **接口**: `POST /api/snippet/unfavorite/{id}`
- **说明**: 取消收藏代码片段（需要登录）
- **路径参数**: id - 代码片段ID
- **响应**: 返回成功状态

### 标签相关接口

#### 创建标签
- **接口**: `POST /api/tag/create`
- **说明**: 创建标签（仅管理员）
- **请求参数**:
```json
{
  "name": "JavaScript",      // 标签名称（必填，1-50字符）
  "slug": "javascript",      // 标签slug（必填，只能包含小写字母、数字和连字符）
  "color": "#F7DF1E"         // 标签颜色（可选，十六进制颜色值）
}
```
- **响应**: 返回标签ID

#### 更新标签
- **接口**: `PUT /api/tag/update`
- **说明**: 更新标签（仅管理员）
- **请求参数**:
```json
{
  "id": 1,                   // 标签ID（必填）
  "name": "JavaScript",      // 标签名称（可选）
  "slug": "javascript",      // 标签slug（可选）
  "color": "#F7DF1E"         // 标签颜色（可选）
}
```
- **响应**: 返回成功状态

#### 删除标签
- **接口**: `DELETE /api/tag/delete`
- **说明**: 删除标签（仅管理员，未被使用的标签才能删除）
- **请求参数**:
```json
{
  "id": 1  // 标签ID
}
```
- **响应**: 返回成功状态

#### 获取标签详情
- **接口**: `GET /api/tag/get/{id}`
- **说明**: 获取标签详情
- **路径参数**: id - 标签ID
- **响应**: 返回标签信息

#### 分页查询标签
- **接口**: `POST /api/tag/list/page`
- **说明**: 分页查询标签列表
- **请求参数**:
```json
{
  "current": 1,              // 当前页码（默认1）
  "pageSize": 10,            // 每页大小（默认10）
  "id": 1,                   // 标签ID（可选）
  "name": "Java",            // 标签名称（可选，模糊查询）
  "slug": "java",            // 标签slug（可选）
  "sortField": "useCount",   // 排序字段（可选：useCount, createTime）
  "sortOrder": "desc"        // 排序方式（可选：asc, desc）
}
```
- **响应**: 返回分页数据

#### 获取所有标签
- **接口**: `GET /api/tag/list/all`
- **说明**: 获取所有标签列表（按使用次数倒序）
- **响应**: 返回标签列表

#### 根据代码片段ID获取标签列表
- **接口**: `GET /api/tag/list/snippet/{snippetId}`
- **说明**: 获取指定代码片段的标签列表
- **路径参数**: snippetId - 代码片段ID
- **响应**: 返回标签列表

## 统一响应格式

所有接口返回统一的响应格式：

```json
{
  "code": 0,           // 状态码（0表示成功）
  "message": "ok",     // 消息
  "data": { }          // 数据
}
```

## 权限说明

### 接口权限

- 🔓 **公开接口**: 无需登录
  - 用户注册
  - 用户登录
  - 获取代码片段详情（公开的）
  - 分页查询代码片段
  - 获取标签列表

- 🔐 **需要登录**:
  - 创建代码片段
  - 更新代码片段（仅创建者）
  - 删除代码片段（仅创建者）
  - 点赞/取消点赞
  - 收藏/取消收藏

- 👑 **仅管理员**:
  - 创建标签
  - 更新标签
  - 删除标签

### 默认账户

初始化脚本会创建以下默认账户：

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 管理员 |
| user | user123 | 普通用户 |

## 数据模型

### 代码片段（code_snippet）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| userId | bigint | 创建用户ID |
| title | varchar(200) | 标题 |
| code | text | 代码内容 |
| language | varchar(50) | 编程语言 |
| theme | varchar(50) | 编辑器主题 |
| description | varchar(500) | 简短描述 |
| descriptionMarkdown | text | Markdown格式描述 |
| expiryDate | datetime | 过期时间 |
| isPublic | tinyint | 是否公开（0-私有，1-公开） |
| viewCount | int | 浏览次数 |
| likeCount | int | 点赞数 |
| favoriteCount | int | 收藏数 |
| createTime | datetime | 创建时间 |
| updateTime | datetime | 更新时间 |
| isDelete | tinyint | 是否删除 |

### 标签（tag）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| name | varchar(50) | 标签名称 |
| slug | varchar(50) | 标签slug（URL友好） |
| color | varchar(20) | 标签颜色 |
| useCount | int | 使用次数 |
| createTime | datetime | 创建时间 |
| updateTime | datetime | 更新时间 |
| isDelete | tinyint | 是否删除 |

## 项目结构

```
copyer-backend/
├── sql/                           # SQL脚本
│   └── init_db.sql               # 数据库初始化脚本
├── src/main/
│   ├── java/com/ly/app/
│   │   ├── common/               # 通用代码
│   │   │   ├── annotation/      # 自定义注解
│   │   │   ├── aop/             # AOP切面
│   │   │   ├── cache/           # 缓存实现
│   │   │   ├── config/          # 配置类
│   │   │   ├── constant/        # 常量定义
│   │   │   ├── enums/           # 枚举类
│   │   │   ├── exception/       # 异常类
│   │   │   ├── handler/         # 处理器
│   │   │   ├── manager/         # 管理器
│   │   │   ├── model/           # 通用模型
│   │   │   └── units/           # 工具类
│   │   ├── controller/          # 控制器
│   │   │   ├── UserController.java
│   │   │   ├── CodeSnippetController.java
│   │   │   └── TagController.java
│   │   ├── domain/              # 领域模型
│   │   │   ├── entity/          # 实体类
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── vo/              # 视图对象
│   │   │   ├── enums/           # 枚举
│   │   │   └── cover/           # 对象转换器
│   │   ├── mapper/              # 数据访问层
│   │   │   ├── UserMapper.java
│   │   │   ├── CodeSnippetMapper.java
│   │   │   ├── TagMapper.java
│   │   │   ├── SnippetTagMapper.java
│   │   │   ├── FavoriteMapper.java
│   │   │   └── SnippetLikeMapper.java
│   │   └── service/             # 服务层
│   │       ├── UserService.java
│   │       ├── CodeSnippetService.java
│   │       ├── TagService.java
│   │       └── impl/            # 服务实现
│   └── resources/
│       ├── application.yml      # 配置文件
│       └── ...
└── pom.xml                      # Maven配置
```

## 开发指南

### 添加新的API接口

1. 在 `dto` 包中创建请求/响应对象
2. 在 `Service` 接口中添加方法定义
3. 在 `ServiceImpl` 中实现业务逻辑
4. 在 `Controller` 中添加API端点
5. 使用 `@ApiOperation` 注解添加接口说明

### 注意事项

1. **权限控制**: 使用 `@AuthCheck` 注解进行权限控制
2. **参数校验**: 使用 `@Validated` 注解进行参数校验
3. **异常处理**: 使用 `AssertUtil` 进行断言和异常抛出
4. **事务管理**: Service层关键方法使用 `@Transactional` 注解
5. **逻辑删除**: 删除操作使用逻辑删除，不是物理删除

## 常见问题

### 1. 如何修改数据库配置？

修改 `application.yml` 中的 `spring.datasource` 配置。

### 2. 如何禁用 API 文档？

在 `application.yml` 中设置：
```yaml
knife4j:
  enable: false
```

### 3. Redis 连接失败？

检查 Redis 是否已启动，或修改 `application.yml` 中的 Redis 配置。

### 4. 如何修改 Token 有效期？

修改 `application.yml` 中的 `sa-token.timeout` 配置（单位：秒）。

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题，请提交 Issue 或联系开发者。
