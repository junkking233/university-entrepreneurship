package com.entrepreneurship.controller;

import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.common.Result;
import com.entrepreneurship.common.SecurityInputUtil;
import com.entrepreneurship.entity.MentorInfo;
import com.entrepreneurship.entity.Training;
import com.entrepreneurship.entity.TrainingRegistration;
import com.entrepreneurship.service.MentorService;
import com.entrepreneurship.service.TrainingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training")
public class TrainingController {

    private final TrainingService trainingService;
    private final MentorService mentorService;

    public TrainingController(TrainingService trainingService, MentorService mentorService) {
        this.trainingService = trainingService;
        this.mentorService = mentorService;
    }

    @PostMapping
    public Result<Training> create(@RequestBody Training training, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        MentorInfo mentorInfo = mentorService.getByUserId(userId);
        if (mentorInfo != null) {
            training.setMentorId(mentorInfo.getId());
        }
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
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return Result.ok(trainingService.list(
                SecurityInputUtil.page(page),
                SecurityInputUtil.size(size),
                SecurityInputUtil.cleanStatus(status),
                SecurityInputUtil.cleanText(keyword, 100, "关键词")));
    }

    @GetMapping("/mentor/my")
    public Result<PageResult<Training>> listMyMentorTrainings(HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        Long userId = (Long) request.getAttribute("userId");
        MentorInfo mentorInfo = mentorService.getByUserId(userId);
        if (mentorInfo == null) {
            return Result.error("不是导师");
        }
        return Result.ok(trainingService.listByMentor(
                mentorInfo.getId(),
                SecurityInputUtil.page(page),
                SecurityInputUtil.size(size),
                SecurityInputUtil.cleanStatus(status),
                SecurityInputUtil.cleanText(keyword, 100, "关键词")));
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
