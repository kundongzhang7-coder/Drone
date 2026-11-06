package com.drone.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO implements Serializable {

    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
}
