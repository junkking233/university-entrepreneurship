package com.entrepreneurship.controller;

import com.entrepreneurship.common.Result;
import com.entrepreneurship.dto.LoginDTO;
import com.entrepreneurship.dto.RegisterDTO;
import com.entrepreneurship.interceptor.LoginInterceptor;
import com.entrepreneurship.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        try {
            Map<String, Object> result = userService.login(loginDTO);
            return Result.ok(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO registerDTO) {
        try {
            if (registerDTO.getUsername() == null || registerDTO.getUsername().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (registerDTO.getPassword() == null || registerDTO.getPassword().isEmpty()) {
                return Result.error("密码不能为空");
            }
            if (registerDTO.getRole() == null || registerDTO.getRole().isEmpty()) {
                return Result.error("角色不能为空");
            }
            userService.register(registerDTO);
            return Result.ok("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token != null) {
            LoginInterceptor.removeToken(token);
        }
        return Result.ok("登出成功");
    }
}
