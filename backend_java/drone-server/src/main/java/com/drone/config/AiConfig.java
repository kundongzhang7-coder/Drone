package com.drone.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置：基于 OpenAI 兼容协议接入 DeepSeek。
 * 仅在 drone.ai.enabled=true 时装配，缺少密钥不影响系统其它功能启动。
 */
@Configuration
@ConditionalOnProperty(prefix = "drone.ai", name = "enabled", havingValue = "true")
public class AiConfig {

    @Bean
    public ChatClient droneChatClient(OpenAiChatModel openAiChatModel) {
        String systemPrompt = """
                你是「无人机智能识别系统」的智能分析助手。
                你可以调用工具查询真实的检测统计、任务详情和任务列表，基于工具返回的真实数据回答，
                不要编造数据。回答简洁、专业，使用中文。
                当用户问题与系统数据无关时，礼貌说明你的能力范围。
                """;
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(systemPrompt)
                .build();
    }
}
