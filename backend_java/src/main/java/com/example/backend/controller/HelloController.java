package com.example.backend.controller;

import com.example.backend.pojo.EchoRequest;
import com.example.backend.pojo.EchoResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {

    /** GET 接口：返回简单文本 */
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from backend!";
    }

    /** POST 接口：接收 JSON，返回处理后的数据 */
    @PostMapping("/handlevideo")
    public EchoResponse echo(@RequestBody EchoRequest request) {
        String processed = "后端收到：" + request.getMessage();
        return new EchoResponse(processed);
    }
}
