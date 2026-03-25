# 🌾 农产品销售管理系统

一个基于 Spring Boot + Vue 3 的全栈农产品电商平台，提供完整的商品管理、购物车、订单、收藏、评论等功能。

## 📖 项目简介

农产品销售管理系统是一个面向农产品销售的综合性电商平台，旨在为消费者提供新鲜、优质的农产品购买渠道，同时为商家提供便捷的商品管理和订单处理功能。系统采用前后端分离架构，具有良好的可扩展性和维护性。

### 主要特点

- 🚀 **现代化技术栈** - Spring Boot 3.2 + Vue 3 + Element Plus
- 🔐 **安全认证** - JWT Token 认证机制
- 📱 **响应式设计** - 适配多种设备尺寸
- 🖼️ **图片优化** - 懒加载、响应式图片、格式自动适配
- 📊 **数据可视化** - ECharts 图表展示销售数据

## ✨ 功能特性

### 用户端功能

| 功能模块 | 描述 |
|---------|------|
| 🔍 商品浏览 | 商品搜索、分类筛选、价格区间、排序功能 |
| 🛒 购物车 | 添加商品、修改数量、批量选择、价格计算 |
| ❤️ 收藏夹 | 收藏/取消收藏商品、收藏列表管理 |
| 📦 订单管理 | 下单、支付、查看订单状态、订单历史 |
| ⭐ 商品评价 | 发表评价、上传图片、评分、点赞、回复 |
| 👤 个人中心 | 个人信息管理、收货地址管理 |

### 管理端功能

| 功能模块 | 描述 |
|---------|------|
| 📈 数据看板 | 销售统计、订单统计、用户增长趋势图表 |
| 🏷️ 分类管理 | 商品分类的增删改查 |
| 🍎 商品管理 | 商品的上架、下架、编辑、库存管理 |
| 📋 订单管理 | 订单状态更新、发货处理 |
| 👥 用户管理 | 用户信息查看、状态管理 |

## 🛠️ 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|-----|------|-----|
| Spring Boot | 3.2.0 | 核心框架 |
| MyBatis | 3.0.3 | ORM 框架 |
| MySQL | 8.0+ | 数据库 |
| JWT | 0.12.3 | Token 认证 |
| Lombok | - | 简化代码 |

### 前端技术

| 技术 | 版本 | 说明 |
|-----|------|-----|
| Vue | 3.4.0 | 前端框架 |
| Vue Router | 4.2.5 | 路由管理 |
| Pinia | 2.1.7 | 状态管理 |
| Element Plus | 2.4.4 | UI 组件库 |
| Axios | 1.6.2 | HTTP 请求 |
| ECharts | 5.4.3 | 图表库 |
| Vite | 5.0.10 | 构建工具 |

## 📁 项目目录结构

```
agricultural-products/
├── backend/                          # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/agricultural/products/
│   │   │   │   ├── common/           # 通用类（Result、分页等）
│   │   │   │   ├── config/           # 配置类（CORS、静态资源）
│   │   │   │   ├── controller/       # 控制器层
│   │   │   │   ├── entity/           # 实体类
│   │   │   │   ├── mapper/           # MyBatis Mapper
│   │   │   │   ├── service/          # 服务层
│   │   │   │   └── utils/            # 工具类（JWT、密码加密）
│   │   │   └── resources/
│   │   │       ├── mapper/           # MyBatis XML
│   │   │       └── application.yml   # 配置文件
│   │   └── test/                     # 测试目录
│   └── pom.xml                       # Maven 依赖配置
│
├── frontend/                         # 前端项目
│   ├── src/
│   │   ├── api/                      # API 接口封装
│   │   ├── assets/                   # 静态资源
│   │   ├── components/               # 公共组件
│   │   │   ├── ProductImage.vue      # 图片组件（懒加载、响应式）
│   │   │   └── ImagePreview.vue      # 图片预览组件
│   │   ├── router/                   # 路由配置
│   │   ├── store/                    # Pinia 状态管理
│   │   ├── utils/                    # 工具函数
│   │   ├── views/                    # 页面组件
│   │   ├── App.vue                   # 根组件
│   │   └── main.js                   # 入口文件
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── database.sql                      # 数据库初始化脚本
├── database_extension.sql            # 数据库扩展脚本
└── README.md                         # 项目说明文档
```

## 🚀 快速开始

### 环境要求

- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### 安装步骤

#### 1. 克隆项目

```bash
git clone https://github.com/your-username/agricultural-products.git
cd agricultural-products
```

#### 2. 数据库配置

```bash
# 创建数据库并导入初始数据
mysql -u root -p < database.sql
mysql -u root -p < database_extension.sql
mysql -u root -p < database_review_extension.sql
```

#### 3. 后端配置与启动

```bash
cd backend

# 修改数据库配置（如需要）
# 编辑 src/main/resources/application.yml
# 修改数据库用户名、密码等

# 启动后端服务
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

#### 4. 前端配置与启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:5174` 启动

### 生产环境构建

```bash
# 后端打包
cd backend
mvn clean package -DskipTests
java -jar target/products-1.0.0.jar

# 前端打包
cd frontend
npm run build
# 生成的文件在 dist/ 目录
```

## 📝 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 8080                          # 服务端口

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/agricultural_products
    username: root                    # 数据库用户名
    password: 1234                    # 数据库密码

jwt:
  secret: your-secret-key             # JWT 密钥
  expiration: 86400000                # Token 有效期（毫秒）

file:
  upload-path: D:/upload/             # 文件上传路径
```

### 前端配置 (vite.config.js)

```javascript
export default defineConfig({
  server: {
    port: 5174,                       // 开发服务器端口
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## 👤 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|-----|-------|------|-----|
| 管理员 | admin | admin | 拥有所有权限 |
| 普通用户 | user1 | 123456 | 普通用户权限 |

## 📸 系统截图

### 商品浏览页面
- 支持搜索、分类筛选、价格区间、排序
- 响应式图片懒加载
- 骨架屏加载效果

### 购物车页面
- 商品数量修改
- 批量选择
- 价格实时计算

### 管理后台
- 数据统计看板
- 商品管理
- 订单管理

## 🔌 API 接口

### 认证接口

```
POST /api/auth/register    # 用户注册
POST /api/auth/login       # 用户登录
```

### 商品接口

```
GET  /api/product/page     # 分页查询商品
GET  /api/product/{id}     # 获取商品详情
POST /api/product          # 添加商品（管理员）
PUT  /api/product          # 更新商品（管理员）
DELETE /api/product/{id}   # 删除商品（管理员）
```

### 订单接口

```
GET  /api/order/list       # 获取用户订单列表
GET  /api/order/{id}       # 获取订单详情
POST /api/order            # 创建订单
PUT  /api/order/status     # 更新订单状态
```

### 更多接口请参考各 Controller 文件

## 🤝 贡献指南

欢迎对本项目进行贡献！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 代码规范

- 后端遵循阿里巴巴 Java 开发手册
- 前端遵循 Vue 官方风格指南
- 提交信息遵循 Conventional Commits 规范

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

如有问题或建议，欢迎通过以下方式联系：

- 提交 Issue: [GitHub Issues](https://github.com/your-username/agricultural-products/issues)
- 邮箱: your-email@example.com

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [ECharts](https://echarts.apache.org/)

---

⭐ 如果这个项目对你有帮助，欢迎 Star 支持！
