package com.drone.controller;

import cn.hutool.core.util.StrUtil;
import com.drone.ai.AiService;
import com.drone.common.result.Result;
import com.drone.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 智能助手接口
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final ObjectProvider<AiService> aiServiceProvider;

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> body) {
        AiService aiService = aiServiceProvider.getIfAvailable();
        if (aiService == null) {
            return Result.error(ResultCode.AI_SERVICE_UNAVAILABLE);
        }
        String message = body.get("message");
        if (StrUtil.isBlank(message)) {
            return Result.error(ResultCode.BAD_REQUEST);
        }
        return Result.success(aiService.chat(message));
    }
}
