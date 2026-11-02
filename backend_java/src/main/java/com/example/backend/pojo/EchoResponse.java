package com.example.backend.pojo;

/**
 * HelloController 回显接口的响应体
 */
public class EchoResponse {

    private String result;

    public EchoResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
