package com.entrepreneurship.controller;

import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.common.Result;
import com.entrepreneurship.entity.Training;
import com.entrepreneurship.entity.TrainingRegistration;
import com.entrepreneurship.service.TrainingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public Result<Training> create(@RequestBody Training training) {
        try {
            return Result.ok(trainingService.create(training));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Training> update(@PathVariable Long id, @RequestBody Training training) {
        return Result.ok(trainingService.update(id, training));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        trainingService.delete(id);
        return Result.ok("删除成功");
    }

    @GetMapping("/{id}")
    public Result<Training> getById(@PathVariable Long id) {
        return Result.ok(trainingService.getById(id));
    }

    @GetMapping("/list")
    public Result<PageResult<Training>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return Result.ok(trainingService.list(page, size, status));
    }

    @PostMapping("/{trainingId}/register")
    public Result<TrainingRegistration> register(@PathVariable Long trainingId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            return Result.ok(trainingService.register(trainingId, userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{trainingId}/register")
    public Result<?> cancelRegistration(@PathVariable Long trainingId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            trainingService.cancelRegistration(trainingId, userId);
            return Result.ok("取消报名成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/my-registrations")
    public Result<PageResult<TrainingRegistration>> listMyRegistrations(HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(trainingService.listRegistrations(userId, page, size));
    }
}
