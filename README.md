# Hotel Management System

饭店管理信息系统（Hotel Management Information System），基于 **Spring Boot + Vue 3** 的全栈 Web 应用，提供一体化饭店运营管理解决方案。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.7.18 |
| ORM | MyBatis-Plus | 3.5.3 |
| 数据库 | MySQL | 8.0 |
| 认证 | JWT (jjwt 0.12.x) + BCrypt | - |
| 前端框架 | Vue 3 (Composition API) | 3.5 |
| UI 组件 | Element Plus | 2.14 |
| 状态管理 | Pinia | 3.0 |
| 路由 | Vue Router | 4.6 |
| 图表 | ECharts | 6.1 |
| 构建工具 | Vite | 8.0 |
| 工具库 | Hutool, Lombok, Apache POI | - |

## 系统角色

系统内置 **4 种角色**，每种角色拥有独立的功能页面和权限：

### 1. 前台接待员
- 散客/团体预订、入住办理
- 房态图查看（楼层分布、房间状态）
- 预订取消、留言管理、物品损坏登记

### 2. 营业服务员
- 菜单点菜（库存自动扣减、售罄下架）
- KTV 包厢开台/关台/计时计费
- 仓库管理（入库/出库/库存预警）
- 待结账单查看

### 3. 财务管理
- 在住客人账单查询（房费、餐饮、KTV、杂费）
- 结账预览与结算、押金管理
- 员工管理（入职/离职/信息维护）
- 考勤打卡与月工资核算
- 工资表 Excel 导出

### 4. 总经理
- KPI 驾驶舱（今日营收、入住率等）
- 收入构成分析（按类型汇总）
- 客源分布（按籍贯统计）
- 入住率趋势（日/月粒度）
- 员工房间配比

## 项目结构

`
hotel-backend/
├── src/main/java/com/hotel/
│   ├── config/          # 配置类（CORS、MyBatis-Plus、BCrypt）
│   ├── controller/      # 控制器层（reception/operation/finance/gm）
│   ├── service/         # 服务接口 + 实现
│   ├── mapper/          # MyBatis-Plus Mapper
│   ├── entity/          # 数据库实体
│   ├── dto/             # 数据传输对象
│   ├── common/          # 通用类（Result、StatusCode、UserContext）
│   ├── interceptor/     # JWT 登录拦截 + 角色鉴权
│   ├── utils/           # 工具类（JWT、日期、金额、校验）
│   ├── task/            # 定时任务（自动取消预订、自动缺勤）
│   └── exception/       # 全局异常处理
├── src/main/resources/
│   ├── application.yml  # 主配置文件
│   ├── mapper/          # MyBatis XML 映射
│   └── static/uploads/  # 菜品/房间图片
├── hotel-frontend/      # Vue 3 前端项目
├── pom.xml              # Maven 构建文件
└── README.md
`

## 快速启动

### 前置要求
- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Node.js 18+

### 1. 数据库初始化

`sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS resturant_system DEFAULT CHARACTER SET utf8mb4;

-- 导入建表脚本（见 sql/init.sql）
`

### 2. 启动后端

`ash
# 修改 application.yml 中的数据库用户名和密码
# 然后执行：
mvn spring-boot:run
`

### 3. 启动前端

`ash
cd hotel-frontend
npm install
npm run dev
`

访问 http://localhost:5173，默认员工密码 123456。

## API 文档

启动后端后访问 Swagger UI：
`
http://localhost:8080/swagger-ui.html
`

## 安全特性

- ✅ 密码 BCrypt 加盐哈希存储（非明文）
- ✅ JWT 令牌认证（12 小时过期）
- ✅ 接口级角色权限校验
- ✅ 请求参数 Validation 校验
- ✅ 全局异常处理，不泄漏内部信息

## Docker 部署

`ash
# 构建并启动所有服务
docker-compose up -d
`

## 许可证

MIT License
