-- 代码片段管理系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS copyer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE copyer;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL COMMENT 'id',
  `userAccount` varchar(256) NOT NULL COMMENT '账号',
  `userPassword` varchar(512) NOT NULL COMMENT '密码',
  `userName` varchar(256) NULL DEFAULT NULL COMMENT '用户昵称',
  `userAvatar` varchar(1024) NULL DEFAULT NULL COMMENT '用户头像',
  `userProfile` varchar(512) NULL DEFAULT NULL COMMENT '用户简介',
  `userRole` varchar(256) NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin',
  `editTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_userAccount` (`userAccount`),
  KEY `idx_userName` (`userName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 代码片段表
CREATE TABLE IF NOT EXISTS `code_snippet` (
  `id` bigint NOT NULL COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '创建用户ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `code` text NOT NULL COMMENT '代码内容',
  `language` varchar(50) NOT NULL COMMENT '编程语言（JavaScript, TypeScript, Java, Python等）',
  `theme` varchar(50) NOT NULL DEFAULT 'vs-dark' COMMENT '编辑器主题（vs-dark, vs, hc-black）',
  `description` varchar(500) NULL DEFAULT NULL COMMENT '简短描述',
  `descriptionMarkdown` text NULL DEFAULT NULL COMMENT 'Markdown格式的详细描述',
  `expiryDate` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `isPublic` tinyint NOT NULL DEFAULT 0 COMMENT '是否公开（0-私有，1-公开）',
  `viewCount` int NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `likeCount` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `favoriteCount` int NOT NULL DEFAULT 0 COMMENT '收藏数',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`),
  KEY `idx_language` (`language`),
  KEY `idx_createTime` (`createTime` DESC),
  KEY `idx_isPublic` (`isPublic`),
  FULLTEXT KEY `ft_title_description` (`title`, `description`) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码片段表';

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
  `id` bigint NOT NULL COMMENT 'id',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `slug` varchar(50) NOT NULL COMMENT '标签slug（URL友好）',
  `color` varchar(20) NULL DEFAULT '#1890ff' COMMENT '标签颜色',
  `useCount` int NOT NULL DEFAULT 0 COMMENT '使用次数',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 代码片段-标签关联表
CREATE TABLE IF NOT EXISTS `snippet_tag` (
  `id` bigint NOT NULL COMMENT 'id',
  `snippetId` bigint NOT NULL COMMENT '代码片段ID',
  `tagId` bigint NOT NULL COMMENT '标签ID',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_snippet_tag` (`snippetId`, `tagId`),
  KEY `idx_snippetId` (`snippetId`),
  KEY `idx_tagId` (`tagId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码片段-标签关联表';

-- 收藏表（可选功能）
CREATE TABLE IF NOT EXISTS `favorite` (
  `id` bigint NOT NULL COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `snippetId` bigint NOT NULL COMMENT '代码片段ID',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_snippet` (`userId`, `snippetId`),
  KEY `idx_userId` (`userId`),
  KEY `idx_snippetId` (`snippetId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 点赞表（可选功能）
CREATE TABLE IF NOT EXISTS `snippet_like` (
  `id` bigint NOT NULL COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `snippetId` bigint NOT NULL COMMENT '代码片段ID',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_snippet` (`userId`, `snippetId`),
  KEY `idx_userId` (`userId`),
  KEY `idx_snippetId` (`snippetId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

-- 插入默认管理员用户（密码：admin123）
INSERT INTO `user` (id, userAccount, userPassword, userName, userRole)
VALUES (1, 'admin', '$2a$10$XqpMqZkOJH.kL6UX1DZBJe7VvqBKZGUxT5Kl6j2zIQXKlH8QHZR4u', '管理员', 'admin')
ON DUPLICATE KEY UPDATE id=id;

-- 插入默认普通用户（密码：user123）
INSERT INTO `user` (id, userAccount, userPassword, userName, userRole)
VALUES (2, 'user', '$2a$10$N.5qp8Gl1yPEYXdj3BM2vuQO6H8U7Q3ZkZFLZT8VxKLpP4UZQG6Fy', '普通用户', 'user')
ON DUPLICATE KEY UPDATE id=id;

-- 插入一些常用标签
INSERT INTO `tag` (id, name, slug, color, useCount) VALUES
(1, 'JavaScript', 'javascript', '#F7DF1E', 0),
(2, 'TypeScript', 'typescript', '#3178C6', 0),
(3, 'Java', 'java', '#007396', 0),
(4, 'Python', 'python', '#3776AB', 0),
(5, 'Go', 'go', '#00ADD8', 0),
(6, 'React', 'react', '#61DAFB', 0),
(7, 'Vue', 'vue', '#4FC08D', 0),
(8, 'Node.js', 'nodejs', '#339933', 0),
(9, 'Spring Boot', 'spring-boot', '#6DB33F', 0),
(10, 'SQL', 'sql', '#CC2927', 0)
ON DUPLICATE KEY UPDATE id=id;

-- 插入示例代码片段
INSERT INTO `code_snippet` (id, userId, title, code, language, theme, description, descriptionMarkdown, isPublic)
VALUES (1, 1, 'Hello World in JavaScript', 'console.log("Hello, World!");', 'javascript', 'vs-dark', '经典的Hello World示例', '# Hello World\n\n这是一个简单的JavaScript Hello World示例。', 1)
ON DUPLICATE KEY UPDATE id=id;

-- 关联示例代码片段和标签
INSERT INTO `snippet_tag` (id, snippetId, tagId) VALUES
(1, 1, 1)
ON DUPLICATE KEY UPDATE id=id;