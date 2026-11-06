package com.drone.controller;

import com.drone.common.properties.JwtProperties;
import com.drone.common.result.Result;
import com.drone.pojo.dto.LoginDTO;
import com.drone.pojo.dto.RegisterDTO;
import com.drone.pojo.vo.LoginVO;
import com.drone.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtProperties jwtProperties;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录: {}", loginDTO.getUsername());
        return Result.success(userService.login(loginDTO));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String header = request.getHeader(jwtProperties.getHeader());
        String token = null;
        if (header != null) {
            token = header.startsWith(jwtProperties.getPrefix())
                    ? header.substring(jwtProperties.getPrefix().length()).trim()
                    : header.trim();
        }
        userService.logout(token);
        return Result.success();
    }
}
