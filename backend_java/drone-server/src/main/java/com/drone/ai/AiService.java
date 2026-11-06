package com.drone.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 智能助手服务：驱动 ChatClient 并挂载工具，由模型自主决定是否调用工具。
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "drone.ai", name = "enabled", havingValue = "true")
public class AiService {

    private final ChatClient droneChatClient;
    private final DetectionTools detectionTools;

    public String chat(String userMessage) {
        return droneChatClient.prompt()
                .user(userMessage)
                .tools(detectionTools)
                .call()
                .content();
    }
}
