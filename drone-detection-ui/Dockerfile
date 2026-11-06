# syntax=docker/dockerfile:1
# ===== 构建阶段：Node 编译 Vue 生产包 =====
FROM node:22-alpine AS build
WORKDIR /build

COPY package*.json ./
# 与本地一致，规避 npm 10 的 peer 解析问题
RUN npm install --legacy-peer-deps

COPY . .
RUN npm run build

# ===== 运行阶段：Nginx 托管静态资源 =====
FROM nginx:1.27-alpine
COPY --from=build /build/dist /usr/share/nginx/html
# Nginx 反代配置由 docker-compose 以只读卷挂载
EXPOSE 80
