package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173/") // 允许前端地址（根据实际前端端口修改）
public class HelloController {

    // GET 接口：返回简单文本
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from backend!";
    }

    // POST 接口：接收 JSON，返回处理后的数据
    @PostMapping("/handlevideo")
    public EchoResponse echo(@RequestBody EchoRequest request) {
        String processed = "后端收到：" + request.getMessage();
        return new EchoResponse(processed);
    }
}