package com.entrepreneurship.controller;

import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.common.Result;
import com.entrepreneurship.entity.Feedback;
import com.entrepreneurship.service.FeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/create")
    public Result<Feedback> create(@RequestBody Feedback feedback, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        feedback.setUserId(userId);
        Feedback result = feedbackService.create(feedback);
        return Result.ok(result);
    }

    @GetMapping("/my")
    public Result<PageResult<Feedback>> my(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<Feedback> result = feedbackService.listByUser(userId, page, size);
        return Result.ok(result);
    }

    @GetMapping("/list")
    public Result<PageResult<Feedback>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        PageResult<Feedback> result = feedbackService.listAll(page, size, status);
        return Result.ok(result);
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        feedbackService.updateStatus(id, body.get("status"));
        return Result.ok("状态更新成功");
    }
}
