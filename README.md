# Hotel PMS 管理系统

饭店管理信息系统（Hotel Management Information System），基于 **Spring Boot + Vue 3** 的全栈 Web 应用，覆盖预订入住、餐饮、KTV、库存、员工考勤工资、总经理数据看板六大业务域，4 种角色权限隔离，Docker Compose 三容器编排部署。

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
| 构建工具 | Vite | - |
| 工具库 | Hutool, Lombok, Apache POI | - |
| 测试 | JUnit 5 + H2 | - |

## 企业级特性

### 并发安全
- 数据库行级锁（SELECT...FOR UPDATE）防房间超售，并发压测 10 请求同房间入住零超售
- 原子条件更新（UPDATE...WHERE quantity>=#num）防库存超卖
- 接口幂等方案（唯一键去重），网络重试场景下入住/下单零重复处理

### 安全防御
- JWT 无状态认证，支持水平扩展
- 用户名 + IP 双维度失败计数，3 次错误触发验证码
- IP 60 次/分钟接口限流
- XSS 参数过滤
- 全局异常脱敏，不泄露内部信息

### 可观测性
- 审计日志模块：自动记录所有写操作的操作人、耗时、客户端 IP、业务结果
- 7 个 JUnit 单元测试（H2 内存库），覆盖可用房间、库存扣减、幂等去重、结账计算等核心逻辑

## 系统角色

### 1. 前台接待员
- 散客/团队预订、入住办理、换房、续房
- 房态图（楼层分布、房间状态、在住客人）
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
- 收入构成分析、客源分布统计
- 入住率趋势（日/月粒度）
- 员工房间配比

## 快速启动

### 前置要求
- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Node.js 18+

### 1. 数据库初始化

\\sql
CREATE DATABASE IF NOT EXISTS resturant_system DEFAULT CHARACTER SET utf8mb4;
\
执行 sql/ 目录下的建表脚本。

### 2. 启动后端

\\ash
# 配置 application.yml 中的数据库用户名密码
mvn spring-boot:run
\
### 3. 启动前端

\\ash
cd hotel-frontend
npm install
npm run dev
\
访问 http://localhost:5173，默认员工密码 123456。

## API 文档

启动后端后访问：
\'
http://localhost:8080/swagger-ui.html
\'

## 测试

\\ash
# 单元测试（H2 内存库）
mvn test

# 并发压测（需先启动后端）
python concurrency_test.py
\
## Docker 部署

\\ash
docker-compose up -d
\
## 项目结构

\'
hotel-backend/
├── src/main/java/com/hotel/
│   ├── config/          # 配置类（CORS、MyBatis-Plus）
│   ├── controller/      # 控制器层
│   ├── service/         # 服务接口 + 实现
│   ├── mapper/          # MyBatis-Plus Mapper
│   ├── entity/          # 数据库实体
│   ├── dto/             # 数据传输对象
│   ├── filter/          # XSS 过滤、审计日志
│   ├── interceptor/     # JWT 登录拦截、IP 限流
│   ├── common/          # 通用类（Result、UserContext）
│   ├── utils/           # 工具类（JWT、日期、金额）
│   ├── task/            # 定时任务
│   └── exception/       # 全局异常处理
├── hotel-frontend/      # Vue 3 前端项目
├── sql/                 # 数据库 DDL
├── docker-compose.yml   # Docker 编排
└── README.md
\'
