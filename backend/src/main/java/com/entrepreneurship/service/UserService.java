package com.entrepreneurship.service;

import com.entrepreneurship.dto.LoginDTO;
import com.entrepreneurship.dto.RegisterDTO;
import com.entrepreneurship.entity.User;

import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginDTO loginDTO);
    User register(RegisterDTO registerDTO);
    User getById(Long id);
    void updatePassword(Long userId, String oldPassword, String newPassword);
    User updateProfile(Long userId, User user);
    String md5(String input);
}
