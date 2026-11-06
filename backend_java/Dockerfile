# syntax=docker/dockerfile:1
# ===== 构建阶段：Maven 编译多模块工程 =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# 先复制 POM 与各模块，利用 Docker 层缓存
COPY pom.xml ./
COPY drone-common drone-common
COPY drone-pojo drone-pojo
COPY drone-server drone-server
RUN mvn -q clean package -DskipTests

# ===== 运行阶段：仅携带可执行 fat jar 与 JRE =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/drone-server/target/drone-server.jar /app/drone-server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/drone-server.jar"]
