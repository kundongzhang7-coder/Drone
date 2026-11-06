package com.drone.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 腾讯云 COS 配置属性，密钥通过环境变量注入
 */
@Data
@ConfigurationProperties(prefix = "drone.cos")
public class CosProperties {

    private String secretId;
    private String secretKey;
    private String region;
    private String bucket;
    /** 访问域名前缀，例如 https://drone-1482941883.cos.ap-chengdu.myqcloud.com */
    private String baseUrl;
}
