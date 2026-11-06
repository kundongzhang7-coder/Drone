package com.drone.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置属性
 */
@Data
@ConfigurationProperties(prefix = "drone.jwt")
public class JwtProperties {

    /** 签名密钥（HS256，至少 32 字节），通过环境变量注入 */
    private String secret;

    /** 令牌有效期（小时） */
    private long expirationHours = 2;

    /** 令牌所在请求头名称 */
    private String header = "Authorization";

    /** 令牌前缀 */
    private String prefix = "Bearer ";
}
