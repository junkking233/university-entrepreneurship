package com.entrepreneurship.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.common.Result;
import com.entrepreneurship.entity.Feedback;
import com.entrepreneurship.entity.Project;
import com.entrepreneurship.entity.User;
import com.entrepreneurship.mapper.UserMapper;
import com.entrepreneurship.service.FeedbackService;
import com.entrepreneurship.service.ProjectService;
import com.entrepreneurship.service.StatisticsService;
import com.entrepreneurship.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Project> result = projectService.list(page, size, null, null, null, "pending");
        return Result.ok(result);
    }

    @PutMapping("/projects/{id}/approve")
    public Result<?> approveProject(@PathVariable Long id) {
        projectService.approve(id);
        return Result.ok("审核通过");
    }

    @PutMapping("/projects/{id}/reject")
    public Result<?> rejectProject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        projectService.reject(id, reason);
        return Result.ok("已拒绝");
    }

    @GetMapping("/users")
    public Result<PageResult<User>> users(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = new Page<>(page, size);
        Page<User> result = userMapper.selectPage(userPage, null);
        PageResult<User> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setRecords(result.getRecords());
        return Result.ok(pageResult);
    }

    @PutMapping("/users/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Object statusObj = body.get("status");
        if (statusObj != null) {
            user.setStatus(Integer.valueOf(statusObj.toString()));
        }
        userMapper.updateById(user);
        return Result.ok("状态更新成功");
    }

    @GetMapping("/feedbacks")
    public Result<PageResult<Feedback>> feedbacks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Feedback> result = feedbackService.listAll(page, size, null);
        return Result.ok(result);
    }

    @PutMapping("/feedbacks/{id}/status")
    public Result<?> updateFeedbackStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        feedbackService.updateStatus(id, body.get("status"));
        return Result.ok("处理成功");
    }

    @GetMapping("/statistics/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> stats = statisticsService.getDashboardStats();
        return Result.ok(stats);
    }
}
