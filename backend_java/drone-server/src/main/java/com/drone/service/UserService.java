package com.drone.service;

import com.drone.pojo.dto.LoginDTO;
import com.drone.pojo.dto.RegisterDTO;
import com.drone.pojo.dto.UserUpdateDTO;
import com.drone.pojo.vo.LoginVO;
import com.drone.pojo.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户与认证服务
 */
public interface UserService {

    LoginVO login(LoginDTO dto);

    void register(RegisterDTO dto);

    void logout(String token);

    UserVO getProfile();

    UserVO updateProfile(UserUpdateDTO dto);

    String updateAvatar(MultipartFile file);
}
