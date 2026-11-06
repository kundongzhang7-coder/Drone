package com.drone.lock;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 逻辑过期数据包装：物理上不设置 TTL，通过逻辑过期时间判断新鲜度。
 */
@Data
public class RedisData implements Serializable {

    private LocalDateTime expireTime;
    private Object data;
}
