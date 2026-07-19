# 🏨 酒店后台管理系统

> 基于 Spring Boot + Vue 3 + MySQL 的全栈酒店 PMS 管理系统，覆盖预订入住、餐饮、KTV、库存、员工考勤工资、总经理数据看板六大业务域，4 种角色权限隔离，Docker Compose 三容器编排部署。

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-green.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 系统截图

![Login](docs/images/login.png)

---

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
- 考勤打卡与月工资核算、工资表 Excel 导出

### 4. 总经理
- KPI 驾驶舱（今日营收、入住率等）
- 收入构成分析、客源分布统计
- 入住率趋势（日/月粒度）
- 员工房间配比

---

## 特殊特性

### 并发安全
- 数据库行级锁（SELECT...FOR UPDATE）防房间超售，并发压测验证零超售
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

---

## 技术栈

| 技术 | 说明 |
| :--- | :--- |
| **Java 17** | 后端开发语言 |
| **Spring Boot 2.7** | 核心框架 |
| **MyBatis-Plus** | ORM 持久层框架 |
| **MySQL 8.0** | 关系型数据库 |
| **H2** | 单元测试内存数据库 |
| **Vue 3 + Element Plus** | 前端框架与 UI 组件库 |
| **Docker** | 容器化部署 |
| **JUnit 5** | 单元测试 |

---

## 快速开始

### 1. 克隆项目
```bash
git clone git@github.com:jtchen11/hotel-backend.git
cd hotel-backend
```

### 2. 初始化数据库
```sql
CREATE DATABASE resturant_system DEFAULT CHARACTER SET utf8mb4;
```

执行 sql/ 目录下的初始化脚本。

### 3. 配置文件
```bash
cp .env.example .env
```

修改 `.env` 中的数据库密码和 JWT 密钥。

### 4. 启动后端
```bash
mvn spring-boot:run
```

### 5. 启动前端
```bash
cd hotel-frontend
npm install
npm run dev
```

访问 `http://localhost:5173`，默认员工密码 `123456`。

## Docker 部署

```bash
docker-compose up -d
```

## 项目结构

```
hotel-backend/
├── src/                     # Java 源代码
├── sql/                     # 数据库初始化脚本
├── hotel-frontend/          # Vue 3 前端
├── concurrency_test.py      # 并发压测脚本
├── run-tests.bat            # 测试启动脚本
├── docker-compose.yml       # Docker 编排
├── Dockerfile               # Docker 镜像构建
├── nginx.conf               # Nginx 配置
├── pom.xml                  # Maven 依赖管理
└── README.md                # 项目说明
```

## 运行测试

```bash
# 单元测试（H2 内存库）
mvn test

# 并发压测（需先启动后端）
python concurrency_test.py
```

## 贡献指南

1. Fork 本仓库
2. 创建功能分支：`git checkout -b feature/your-feature`
3. 提交改动：`git commit -m 'your message'`
4. 推送分支：`git push origin feature/your-feature`
5. 提交 Pull Request

## 开源协议

MIT License

## 联系作者

- GitHub: [@jtchen11](https://github.com/jtchen11)
- 项目地址: [https://github.com/jtchen11/hotel-backend](https://github.com/jtchen11/hotel-backend)
