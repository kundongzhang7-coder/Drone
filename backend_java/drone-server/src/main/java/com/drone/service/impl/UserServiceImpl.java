package com.drone.service.impl;

import cn.hutool.core.util.StrUtil;
import com.drone.common.constant.RedisConstants;
import com.drone.common.context.UserContext;
import com.drone.common.exception.BusinessException;
import com.drone.common.exception.LoginFailedException;
import com.drone.common.result.ResultCode;
import com.drone.common.util.PasswordUtils;
import com.drone.mapper.UserMapper;
import com.drone.pojo.dto.LoginDTO;
import com.drone.pojo.dto.RegisterDTO;
import com.drone.pojo.dto.UserUpdateDTO;
import com.drone.pojo.entity.User;
import com.drone.pojo.vo.LoginVO;
import com.drone.pojo.vo.UserVO;
import com.drone.service.CosService;
import com.drone.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

import static com.drone.common.constant.RedisConstants.*;

/**
 * 用户与认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final com.drone.common.util.JwtUtils jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final CosService cosService;

    @Override
    public LoginVO login(LoginDTO dto) {
        String username = dto.getUsername();

        // 1. 防暴力破解：失败次数超限直接拒绝
        String failKey = LOGIN_FAIL_KEY + username;
        String failCountStr = stringRedisTemplate.opsForValue().get(failKey);
        int failCount = StrUtil.isBlank(failCountStr) ? 0 : Integer.parseInt(failCountStr);
        if (failCount >= LOGIN_FAIL_MAX) {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS);
        }

        // 2. 查询用户并校验 BCrypt 密码
        User user = userMapper.findByUsername(username);
        if (user == null || !PasswordUtils.matches(dto.getPassword(), user.getPassword())) {
            Long count = stringRedisTemplate.opsForValue().increment(failKey);
            if (count != null && count == 1L) {
                stringRedisTemplate.expire(failKey, LOGIN_FAIL_TTL);
            }
            throw new LoginFailedException(ResultCode.LOGIN_FAILED);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new LoginFailedException(ResultCode.USER_DISABLED);
        }

        // 3. 登录成功，清除失败计数
        stringRedisTemplate.delete(failKey);

        // 4. 签发 JWT 并写入 Redis 白名单（所有实例共享同一 Redis，登录态全局一致）
        String token = jwtUtils.createToken(user.getId(), user.getUsername());
        stringRedisTemplate.opsForValue().set(LOGIN_TOKEN_KEY + token,
                String.valueOf(user.getId()), LOGIN_TOKEN_TTL);

        return new LoginVO(token, user.getId(), user.getUsername(),
                user.getNickname(), user.getAvatar());
    }

    @Override
    public void register(RegisterDTO dto) {
        // 分布式锁 + 双重检查，防止并发注册同名账号；数据库唯一索引做最终兜底
        RLock lock = redissonClient.getLock("lock:register:" + dto.getUsername());
        boolean locked;
        try {
            locked = lock.tryLock(5, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("注册被中断，请重试");
        }
        if (!locked) {
            throw new BusinessException(ResultCode.REPEAT_SUBMIT);
        }
        try {
            if (userMapper.findByUsername(dto.getUsername()) != null) {
                throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
            }
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setPassword(PasswordUtils.encode(dto.getPassword()));
            user.setNickname(StrUtil.isBlank(dto.getNickname()) ? dto.getUsername() : dto.getNickname());
            user.setStatus(1);
            userMapper.insert(user);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void logout(String token) {
        if (StrUtil.isNotBlank(token)) {
            stringRedisTemplate.delete(LOGIN_TOKEN_KEY + token);
        }
    }

    @Override
    public UserVO getProfile() {
        User user = userMapper.findById(UserContext.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return toUserVO(user);
    }

    @Override
    public UserVO updateProfile(UserUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        User user = new User();
        user.setId(userId);
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        userMapper.update(user);
        return toUserVO(userMapper.findById(userId));
    }

    @Override
    public String updateAvatar(MultipartFile file) {
        Long userId = UserContext.getUserId();
        String url = cosService.upload(file, "avatar");
        User update = new User();
        update.setId(userId);
        update.setAvatar(url);
        userMapper.update(update);
        return url;
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
