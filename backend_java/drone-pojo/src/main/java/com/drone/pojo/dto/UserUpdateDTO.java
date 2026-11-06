package com.drone.pojo.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateDTO implements Serializable {

    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String phone;
}
