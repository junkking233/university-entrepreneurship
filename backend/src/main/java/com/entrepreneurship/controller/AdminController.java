package com.entrepreneurship.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.common.Result;
import com.entrepreneurship.common.SecurityInputUtil;
import com.entrepreneurship.entity.Feedback;
import com.entrepreneurship.entity.Project;
import com.entrepreneurship.entity.User;
import com.entrepreneurship.mapper.UserMapper;
import com.entrepreneurship.service.FeedbackService;
import com.entrepreneurship.service.ProjectService;
import com.entrepreneurship.service.StatisticsService;
import com.entrepreneurship.service.UserService;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProjectService projectService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final FeedbackService feedbackService;
    private final StatisticsService statisticsService;

    public AdminController(ProjectService projectService,
                           UserService userService,
                           UserMapper userMapper,
                           FeedbackService feedbackService,
                           StatisticsService statisticsService) {
        this.projectService = projectService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.feedbackService = feedbackService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/projects/pending")
    public Result<PageResult<Project>> pendingProjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        requireAdmin(request);
        PageResult<Project> result = projectService.list(SecurityInputUtil.page(page), SecurityInputUtil.size(size), null, null, null, "pending");
        return Result.ok(result);
    }

    @PutMapping("/projects/{id}/approve")
    public Result<?> approveProject(@PathVariable Long id, HttpServletRequest request) {
        requireAdmin(request);
        SecurityInputUtil.requirePositiveId(id, "项目ID");
        projectService.approve(id);
        return Result.ok("审核通过");
    }

    @PutMapping("/projects/{id}/reject")
    public Result<?> rejectProject(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        requireAdmin(request);
        SecurityInputUtil.requirePositiveId(id, "项目ID");
        String reason = SecurityInputUtil.cleanText(body.get("reason"), 500, "拒绝原因");
        projectService.reject(id, reason);
        return Result.ok("已拒绝");
    }

    @GetMapping("/users")
    public Result<PageResult<User>> users(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            HttpServletRequest request) {
        requireAdmin(request);
        Page<User> userPage = new Page<>(SecurityInputUtil.page(page), SecurityInputUtil.size(size));
        String cleanKeyword = SecurityInputUtil.cleanText(keyword, 100, "关键词");
        String cleanRole = role == null || role.isBlank() ? null : SecurityInputUtil.cleanRole(role);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (cleanKeyword != null && !cleanKeyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, cleanKeyword)
                    .or().like(User::getName, cleanKeyword)
                    .or().like(User::getEmail, cleanKeyword));
        }
        if (cleanRole != null && !cleanRole.isEmpty()) {
            wrapper.eq(User::getRole, cleanRole);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(userPage, wrapper);
        result.getRecords().forEach(user -> user.setPassword(null));
        PageResult<User> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setRecords(result.getRecords());
        return Result.ok(pageResult);
    }

    @PutMapping("/users/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        requireAdmin(request);
        SecurityInputUtil.requirePositiveId(id, "用户ID");
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Object statusObj = body.get("status");
        if (statusObj != null) {
            String status = SecurityInputUtil.cleanText(statusObj.toString(), 20, "状态");
            if ("active".equals(status) || "1".equals(status)) {
                user.setStatus(1);
            } else if ("disabled".equals(status) || "inactive".equals(status) || "0".equals(status)) {
                user.setStatus(0);
            } else {
                return Result.error(400, "状态不合法");
            }
        }
        userMapper.updateById(user);
        return Result.ok("状态更新成功");
    }

    @GetMapping("/users/{id}")
    public Result<User> getUser(@PathVariable Long id, HttpServletRequest request) {
        requireAdmin(request);
        SecurityInputUtil.requirePositiveId(id, "用户ID");
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.ok(user);
    }

    @DeleteMapping("/users/{id}")
    public Result<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        requireAdmin(request);
        SecurityInputUtil.requirePositiveId(id, "用户ID");
        userMapper.deleteById(id);
        return Result.ok("删除成功");
    }

    @GetMapping("/projects")
    public Result<PageResult<Project>> projects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        requireAdmin(request);
        PageResult<Project> result = projectService.list(
                SecurityInputUtil.page(page),
                SecurityInputUtil.size(size),
                SecurityInputUtil.cleanText(keyword, 100, "关键词"),
                SecurityInputUtil.cleanText(category, 50, "项目分类"),
                SecurityInputUtil.cleanText(field, 50, "项目领域"),
                SecurityInputUtil.cleanStatus(status));
        return Result.ok(result);
    }

    @GetMapping("/feedbacks")
    public Result<PageResult<Feedback>> feedbacks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        requireAdmin(request);
        PageResult<Feedback> result = feedbackService.listAll(
                SecurityInputUtil.page(page),
                SecurityInputUtil.size(size),
                SecurityInputUtil.cleanStatus(status));
        return Result.ok(result);
    }

    @PutMapping("/feedbacks/{id}/status")
    public Result<?> updateFeedbackStatus(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        requireAdmin(request);
        SecurityInputUtil.requirePositiveId(id, "反馈ID");
        feedbackService.updateStatus(id, SecurityInputUtil.cleanStatus(body.get("status")));
        return Result.ok("处理成功");
    }

    @GetMapping("/statistics/overview")
    public Result<Map<String, Object>> overview(HttpServletRequest request) {
        requireAdmin(request);
        Map<String, Object> stats = statisticsService.getDashboardStats();
        return Result.ok(stats);
    }

    private void requireAdmin(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        SecurityInputUtil.requireRole(user, "admin");
    }
}
