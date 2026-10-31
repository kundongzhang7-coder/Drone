package com.example.backend.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class VideoFrameHandler extends TextWebSocketHandler {

    private WebSocketSession frontendSession;
    private WebSocketSession pythonSession;
    private WebSocketConnectionManager pythonConnectionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.frontendSession = session;
        System.out.println("前端已连接: " + session.getId());

        // 连接 Python 后端
        StandardWebSocketClient client = new StandardWebSocketClient();
        pythonConnectionManager = new WebSocketConnectionManager(
                client,
                new PythonWebSocketHandler(),
                "ws://localhost:8000/ws/video"
        );
        pythonConnectionManager.start();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("收到前端帧，长度: " + message.getPayloadLength());
        if (pythonSession != null && pythonSession.isOpen()) {
            pythonSession.sendMessage(message);
            System.out.println("转发帧到 Python 成功");
        } else {
            System.err.println("Python 后端未连接，无法转发帧。pythonSession=" + pythonSession);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("前端连接断开: " + session.getId() + ", 关闭原因: " + status);
        // 注意：不要立即关闭 Python 连接，因为可能前端只是短暂断开
        // 这里只做日志记录，让 Python 连接自然超时或由其他机制处理
    }

    private class PythonWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            pythonSession = session;
            System.out.println("已连接到 Python 后端，sessionId: " + session.getId());
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            System.out.println("收到 Python 返回结果，长度: " + message.getPayloadLength());
            if (frontendSession != null && frontendSession.isOpen()) {
                frontendSession.sendMessage(message);
                System.out.println("转发结果到前端成功");
            } else {
                System.err.println("前端未连接，无法转发检测结果");
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            System.out.println("Python 后端连接断开: " + session.getId() + ", 原因: " + status);
            pythonSession = null;
            // 尝试重连（可选）
            // reconnectToPython();
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            System.err.println("Python 后端通信错误: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    // 可选：重连逻辑
    private void reconnectToPython() {
        if (pythonConnectionManager != null) {
            pythonConnectionManager.stop();
        }
        StandardWebSocketClient client = new StandardWebSocketClient();
        pythonConnectionManager = new WebSocketConnectionManager(
                client,
                new PythonWebSocketHandler(),
                "ws://localhost:8000/ws/video"
        );
        pythonConnectionManager.start();
        System.out.println("尝试重连 Python 后端...");
    }
}