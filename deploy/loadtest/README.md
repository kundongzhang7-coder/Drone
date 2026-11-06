# 高并发压测方案（JMeter）

配套脚本：`drone-loadtest.jmx`。压测目标是验证登录态、缓存防护与分布式锁在真实并发下的表现，而非单纯把接口压垮。

## 一、前置准备

1. 先按根目录 `docker-compose.yml` 启动整套服务：`docker compose up -d --build`。
2. 灌入大数据量，制造真实基数：
   ```bash
   docker exec -i drone-mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" drone_detection < ../data/seed_tasks.sql
   ```
   默认写入 10 万条 `detection_task`，可修改脚本中的 `CALL seed_tasks(100000)` 参数。
3. 安装 JMeter 5.6+，打开 `drone-loadtest.jmx`。

## 二、脚本结构与执行

- 全局变量（Test Plan → User Defined Variables）：`HOST`、`PORT`、`THREADS`、`RAMPUP`、`DURATION`，按机器配置调整。
- 线程组默认 200 线程、10 秒爬坡、持续 60 秒。
- 每个线程在「仅执行一次」控制器里先调 `POST /api/auth/login`（admin/admin123），用 JSON Extractor 取出 `$.data.token`，之后通过 Header Manager 携带 `Authorization: Bearer <token>`。
- 压测两个读接口：
  - `GET /api/task/stats`：命中全局统计缓存。
  - `GET /api/task/1`：命中单任务热点缓存。
- 每个采样器都有 JSON Assertion 校验 `$.code == 200`，失败会计入错误率。

命令行无界面执行（正式压测推荐）：

```bash
jmeter -n -t drone-loadtest.jmx -l result.jtl -e -o report
```

## 三、对照代码看防护点

| 场景 | 代码位置 | 压测时如何观测 |
| --- | --- | --- |
| 缓存穿透（查询不存在的 id） | `TaskBloomFilter` + `CacheClient.queryWithPassThrough`（空值缓存） | 布隆过滤器先拦截；Redis 中出现空值键，数据库 QPS 不随并发上升 |
| 缓存击穿（热点 key 失效） | `CacheClient.queryWithMutex` + Redisson 锁 `lock:task:` | 失效瞬间只有一个线程回源，其余等待后读缓存 |
| 缓存雪崩 | 统计/任务 TTL 加随机抖动（`CACHE_BASE_TTL`） | 大量键不会同一时刻过期，回源流量平滑 |
| 登录态多实例统一 | token 存 Redis 白名单 `login:token:`，JWT 无状态 + Redis 有状态校验 | 任意后端实例都能校验同一 token，重启应用不丢登录态 |
| 登录并发防护 | 失败计数 `login:fail:` + 注册分布式锁 `lock:register:` | 重复注册只有一次成功；暴力破解被计数限制 |

## 四、结果判读

- 重点看 Summary Report 的 **吞吐量、P95/P99 响应时间、错误率**，而不是平均响应时间。
- 同时用 `redis-cli --stat`、MySQL 慢查询日志、RabbitMQ 管理台对照后端实际压力。
- 若错误率升高，先区分是 401（token 问题）、连接数耗尽还是真正的业务异常，再定位，不要只看一个指标下结论。
