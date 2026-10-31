package com.example.backend.controller;

public class EchoResponse {
    private String result;

    public EchoResponse(String result) { this.result = result; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}