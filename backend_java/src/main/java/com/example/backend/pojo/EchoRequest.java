package com.example.backend.pojo;

/**
 * HelloController 回显接口的请求体
 */
public class EchoRequest {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
