package com.entrepreneurship.controller;

import com.entrepreneurship.common.Result;
import com.entrepreneurship.common.SecurityInputUtil;
import com.entrepreneurship.dto.LoginDTO;
import com.entrepreneurship.dto.RegisterDTO;
import com.entrepreneurship.entity.User;
import com.entrepreneurship.interceptor.LoginInterceptor;
import com.entrepreneurship.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public Result<Map<String, Object>> register(@RequestBody RegisterDTO registerDTO) {
        SecurityInputUtil.sanitize(registerDTO);
        User user = userService.register(registerDTO);
        return Result.ok(buildAuthPayload(user));
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token != null) {
            LoginInterceptor.removeToken(token);
        }
        return Result.ok("登出成功");
    }

    @GetMapping("/verify")
    public Result<Map<String, Object>> verify(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        if (user == null || (user.getStatus() != null && user.getStatus() == 0)) {
            return Result.error(401, "登录已过期，请重新登录");
        }
        user.setPassword(null);
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("userInfo", user);
        return Result.ok(result);
    }

    private Map<String, Object> buildAuthPayload(User user) {
        String token = LoginInterceptor.createToken(user.getId());
        user.setPassword(null);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        result.put("userInfo", user);
        return result;
    }
}
