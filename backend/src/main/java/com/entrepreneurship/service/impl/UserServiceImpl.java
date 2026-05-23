package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneurship.common.BusinessException;
import com.entrepreneurship.dto.LoginDTO;
import com.entrepreneurship.dto.RegisterDTO;
import com.entrepreneurship.entity.InvestorInfo;
import com.entrepreneurship.entity.MentorInfo;
import com.entrepreneurship.entity.User;
import com.entrepreneurship.interceptor.LoginInterceptor;
import com.entrepreneurship.mapper.InvestorInfoMapper;
import com.entrepreneurship.mapper.MentorInfoMapper;
import com.entrepreneurship.mapper.UserMapper;
import com.entrepreneurship.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final MentorInfoMapper mentorInfoMapper;
    private final InvestorInfoMapper investorInfoMapper;

    public UserServiceImpl(UserMapper userMapper, MentorInfoMapper mentorInfoMapper, InvestorInfoMapper investorInfoMapper) {
        this.userMapper = userMapper;
        this.mentorInfoMapper = mentorInfoMapper;
        this.investorInfoMapper = investorInfoMapper;
    }

    @Override
    public String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!md5(loginDTO.getPassword()).equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账户已被禁用");
        }

        String token = LoginInterceptor.createToken(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        user.setPassword(null);
        result.put("user", user);
        return result;
    }

    @Override
    @Transactional
    public User register(RegisterDTO registerDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerDTO.getUsername());
        if (userMapper.selectOne(wrapper) != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(md5(registerDTO.getPassword()));
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setRole(registerDTO.getRole());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);

        if ("mentor".equals(registerDTO.getRole())) {
            MentorInfo mentorInfo = new MentorInfo();
            mentorInfo.setUserId(user.getId());
            mentorInfo.setExpertise("创业指导");
            mentorInfo.setRating(java.math.BigDecimal.valueOf(5.0));
            mentorInfo.setCreateTime(LocalDateTime.now());
            mentorInfoMapper.insert(mentorInfo);
        } else if ("investor".equals(registerDTO.getRole())) {
            InvestorInfo investorInfo = new InvestorInfo();
            investorInfo.setUserId(user.getId());
            investorInfo.setTotalInvestment(java.math.BigDecimal.ZERO);
            investorInfo.setCreateTime(LocalDateTime.now());
            investorInfoMapper.insert(investorInfo);
        }

        return user;
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!md5(oldPassword).equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(md5(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public User updateProfile(Long userId, User updateUser) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (updateUser.getName() != null) user.setName(updateUser.getName());
        if (updateUser.getEmail() != null) user.setEmail(updateUser.getEmail());
        if (updateUser.getPhone() != null) user.setPhone(updateUser.getPhone());
        if (updateUser.getAvatar() != null) user.setAvatar(updateUser.getAvatar());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return user;
    }
}
