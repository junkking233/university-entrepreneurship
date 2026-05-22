package com.entrepreneurship.controller;

import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.common.Result;
import com.entrepreneurship.common.SecurityInputUtil;
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
        SecurityInputUtil.sanitize(training);
        return Result.ok(trainingService.create(training));
    }

    @PutMapping("/{id}")
    public Result<Training> update(@PathVariable Long id, @RequestBody Training training) {
        SecurityInputUtil.requirePositiveId(id, "培训ID");
        SecurityInputUtil.sanitizeTrainingOptional(training);
        return Result.ok(trainingService.update(id, training));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        SecurityInputUtil.requirePositiveId(id, "培训ID");
        trainingService.delete(id);
        return Result.ok("删除成功");
    }

    @GetMapping("/{id}")
    public Result<Training> getById(@PathVariable Long id) {
        SecurityInputUtil.requirePositiveId(id, "培训ID");
        return Result.ok(trainingService.getById(id));
    }

    @GetMapping("/list")
    public Result<PageResult<Training>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return Result.ok(trainingService.list(
                SecurityInputUtil.page(page),
                SecurityInputUtil.size(size),
                SecurityInputUtil.cleanStatus(status)));
    }

    @PostMapping("/{trainingId}/register")
    public Result<TrainingRegistration> register(@PathVariable Long trainingId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SecurityInputUtil.requirePositiveId(trainingId, "培训ID");
        return Result.ok(trainingService.register(trainingId, userId));
    }

    @DeleteMapping("/{trainingId}/register")
    public Result<?> cancelRegistration(@PathVariable Long trainingId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SecurityInputUtil.requirePositiveId(trainingId, "培训ID");
        trainingService.cancelRegistration(trainingId, userId);
        return Result.ok("取消报名成功");
    }

    @GetMapping("/my-registrations")
    public Result<PageResult<TrainingRegistration>> listMyRegistrations(HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(trainingService.listRegistrations(userId, SecurityInputUtil.page(page), SecurityInputUtil.size(size)));
    }
}
