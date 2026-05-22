package com.entrepreneurship.controller;

import com.entrepreneurship.common.Result;
import com.entrepreneurship.common.SecurityInputUtil;
import com.entrepreneurship.entity.User;
import com.entrepreneurship.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public Result<User> profile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        user.setPassword(null);
        return Result.ok(user);
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SecurityInputUtil.sanitize(user);
        User updated = userService.updateProfile(userId, user);
        updated.setPassword(null);
        return Result.ok(updated);
    }

    @PutMapping("/password")
    public Result<?> updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String oldPassword = SecurityInputUtil.cleanPassword(params.get("oldPassword"), "原密码");
        String newPassword = SecurityInputUtil.cleanPassword(params.get("newPassword"), "新密码");
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.ok("密码修改成功");
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        SecurityInputUtil.requirePositiveId(id, "用户ID");
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.ok(user);
    }
}
