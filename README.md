\# 饭店管理信息系统 (Hotel Management System)



> 一个基于 Spring Boot + Vue 的企业级饭店管理平台，覆盖预订、入住、餐饮、KTV、库存、财务、人事及经营分析等全流程业务。



\## 📌 项目背景与价值

本系统旨在解决中小型饭店的 \*\*“信息孤岛”\*\* 与 \*\*“人工低效”\*\* 问题。系统打通了前台、餐饮、财务、管理层的数据壁垒，实现从预订到离店的\*\*全流程线上化与数据实时共享\*\*，为管理者提供\*\*数据驱动的经营决策支持\*\*。



\## 🚀 核心功能模块

\*   \*\*前台接待\*\*：散客/团体预订、入住/换房、图形化房态管理、留言备忘。

\*   \*\*营业服务\*\*：菜品点单（挂账）、KTV 开房/计时/现结、仓库出入库（移动加权平均）。

\*   \*\*财务管理\*\*：押金管理、\*\*离店结账（支持折扣、自动计算房费）\*\*、员工档案、\*\*考勤打卡（自动判定状态）\*\*、\*\*工资自动计算\*\*。

\*   \*\*总经理仪表板\*\*：收入构成（饼图）、客源分布（热力图）、入住率趋势（折线图）、人房比分析。



\## 🛠 技术架构

\*   \*\*前端\*\*：Vue 3 + Element Plus + Axios + ECharts

\*   \*\*后端\*\*：Spring Boot 2.7 + Spring Security + JWT + MyBatis-Plus

\*   \*\*数据库\*\*：MySQL 8.0

\*   \*\*部署\*\*：前后端分离，支持 Tomcat 部署



\## 🗂️ 项目结构 (后端)

src/main/java/com/xxx/hotel

├── controller/ # 控制器层 (接收前端请求)

├── service/ # 业务逻辑层 (核心业务)

├── mapper/ # 数据访问层 (MyBatis-Plus)

├── entity/ # 实体类

├── dto/ # 数据传输对象

├── config/ # 配置类 (Security, CORS等)

└── common/ # 通用模块 (统一返回Result, 全局异常)



\## 💾 数据库设计

\*   共计 \*\*16张\*\* 核心数据表，涵盖 `guest`（客人）、`room`（房间）、`order\_main/detail`（订单）、`attendance`（考勤）、`salary`（工资）等。

\*   详见项目中的 `docs/数据库设计文档.md` 或 \[E-R图](链接)。



\## 🚀 快速启动

\### 环境要求

\- JDK 1.8+

\- Maven 3.6+

\- MySQL 8.0+

\- Node.js 16+ (前端)



\### 后端启动步骤

```bash

\# 1. 克隆项目

git clone https://github.com/jtchen11/your-repo-name.git



\# 2. 导入SQL文件

\# 在MySQL中创建数据库 hotel\_db，并执行 /db/hotel.sql



\# 3. 修改配置

\# 修改 application-dev.yml 中的数据库用户名和密码



\# 4. 启动后端

mvn clean install

mvn spring-boot:run



\###前端启动步骤

\# 1. 进入前端目录

cd frontend



\# 2. 安装依赖

npm install



\# 3. 启动开发服务器

npm run serve

