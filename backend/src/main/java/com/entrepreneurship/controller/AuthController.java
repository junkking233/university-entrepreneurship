package com.entrepreneurship.controller;

import com.entrepreneurship.common.Result;
import com.entrepreneurship.common.SecurityInputUtil;
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
        SecurityInputUtil.sanitize(loginDTO);
        Map<String, Object> result = userService.login(loginDTO);
        return Result.ok(result);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO registerDTO) {
        SecurityInputUtil.sanitize(registerDTO);
        userService.register(registerDTO);
        return Result.ok("注册成功");
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
