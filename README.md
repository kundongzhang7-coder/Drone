# Drone Detection Platform

无人机检测系统，三端结构：

| 目录 | 技术栈 | 端口 | 作用 |
|---|---|---|---|
| `backend_py/` | Python + FastAPI + YOLOv8 | 8000 | 实时视频帧检测（WebSocket） |
| `backend_java/` | Java 17 + Spring Boot 3 + MyBatis + MySQL | 8080 | 任务管理、视频处理、数据持久化 |
| `drone-detection-ui/` | Vue 3 + Vite + Element Plus | 5173 | 前端界面 |

## 环境要求

- JDK 17+
- Python 3.10+
- Node.js 20.19+（或 22.12+）
- MySQL 8.0+
- Maven（或使用仓库自带 `mvnw`）

## MySQL 准备（首次启动 Java 后端前必做）

1. 安装并启动 MySQL 8（记住 root 密码）。
2. 执行建库建表脚本（二选一）：
   - 命令行：`mysql -u root -p < backend_java/src/main/resources/schema.sql`
   - 或用 Navicat / DataGrip 等工具打开 `schema.sql` 直接运行
3. 修改 `backend_java/src/main/resources/application.properties` 中的数据库密码：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/drone_detection?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=你的MySQL密码
```

脚本会创建 `drone_detection` 数据库和 `detection_task` 表，之后表结构由应用读写，无需再手动维护。

## 启动顺序

### 1. Python 检测服务（端口 8000）

```bash
cd backend_py
python -m venv drone_env
# Windows:  drone_env\Scripts\activate
# macOS/Linux: source drone_env/bin/activate
pip install -r requirements.txt
python server.py
```

> 首次启动会自动下载 `yolov8n.pt` 预训练权重。若你有自己训练的权重，放到 `backend_py/last.pt` 即可自动加载。

### 2. Java 后端（端口 8080）

```bash
cd backend_java
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

> 启动前请确认已完成上面的「MySQL 准备」，否则会报数据库连接失败。

如果需要调用视频处理接口（`/api1/process-video`），请在 `src/main/resources/application.properties` 中确认：

```properties
app.python.command=python
app.python.script-path=../backend_py/yolo_process.py
```

路径按你本机实际位置修改。

### 3. 前端（端口 5173）

```bash
cd drone-detection-ui
npm install
npm run dev
```

浏览器访问 http://localhost:5173 。

## 架构说明

- 前端通过 `ws://localhost:8000/ws/video` 直接连 Python 服务做实时帧检测。
- 前端通过 `http://localhost:8080/api/...` 调 Java 后端做任务记录与历史查询。
- Java 后端在视频处理接口里会调用 `yolo_process.py` 对上传视频做离线检测。

### Java 后端分层

```
controller  →  service（接口）→  service.impl（实现）→  mapper（MyBatis）→  MySQL
                  ↕                                   ↑
        filter（跨域）/ interceptor（日志、鉴权）     pojo（实体类）/ util（工具类）
```

- `filter/CorsFilter`：全局跨域处理，在请求进入 Controller 之前执行。
- `interceptor/RequestLogInterceptor`：记录每个接口的请求路径和耗时；以后加登录鉴权（如 Token 校验）写在它的 `preHandle` 里。

## 目录结构

```
Drone/
├── backend_py/            # Python 检测服务
│   ├── server.py          # FastAPI WebSocket 实时服务
│   ├── yolo_process.py    # 离线视频处理脚本
│   ├── requirements.txt
│   └── src/               # 配置、跟踪器等
├── backend_java/          # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/backend/
│       │   ├── BackendApplication.java   # 启动类（@MapperScan 扫描 mapper 包）
│       │   ├── config/                   # WebConfig（拦截器注册）、WebSocketConfig
│       │   ├── controller/               # 控制层：接收请求、调用 Service
│       │   ├── service/                  # 业务接口
│       │   │   └── impl/                 # 业务实现
│       │   ├── mapper/                   # MyBatis 持久层接口
│       │   ├── pojo/                     # 实体类 / DTO
│       │   ├── util/                     # 工具类（网络、时间）
│       │   ├── interceptor/              # 拦截器（请求日志、鉴权预留）
│       │   ├── filter/                   # 过滤器（跨域）
│       │   └── handler/                  # WebSocket 帧转发
│       └── resources/
│           ├── application.properties    # 数据源、MyBatis、Python 脚本配置
│           └── schema.sql                # MySQL 建库建表脚本
└── drone-detection-ui/    # Vue 前端
    ├── package.json
    └── src/
```

## 备注

- 模型权重（`*.pt` / `*.onnx`）、视频样本、虚拟环境、`node_modules`、构建产物均不入库，见根目录 `.gitignore`。
- 前端代码里接口地址写死为 `localhost:8080` / `localhost:8000`，部署到其他环境时需全局替换。
