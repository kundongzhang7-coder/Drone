package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import com.example.backend.handler.VideoFrameHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final VideoFrameHandler videoFrameHandler;

    public WebSocketConfig(VideoFrameHandler videoFrameHandler) {
        this.videoFrameHandler = videoFrameHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(videoFrameHandler, "/ws/video")
                .setAllowedOrigins("http://localhost:5173");
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(10 * 1024 * 1024); // 10 MB
        container.setMaxBinaryMessageBufferSize(10 * 1024 * 1024);
        return container;
    }
}