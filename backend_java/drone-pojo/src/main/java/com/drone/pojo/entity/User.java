package com.drone.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class User implements Serializable {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    /** 头像在 COS 上的访问 URL */
    private String avatar;
    private String email;
    private String phone;
    /** 账号状态：1 正常，0 禁用 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
