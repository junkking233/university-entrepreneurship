package com.entrepreneurship.controller;

import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.common.Result;
import com.entrepreneurship.common.SecurityInputUtil;
import com.entrepreneurship.entity.Consultation;
import com.entrepreneurship.entity.MentorInfo;
import com.entrepreneurship.service.ConsultationService;
import com.entrepreneurship.service.MentorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/consultation")
public class ConsultationController {

    private final ConsultationService consultationService;
    private final MentorService mentorService;

    public ConsultationController(ConsultationService consultationService, MentorService mentorService) {
        this.consultationService = consultationService;
        this.mentorService = mentorService;
    }

    @PostMapping
    public Result<Consultation> create(@RequestBody Consultation consultation, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        consultation.setStudentId(userId);
        SecurityInputUtil.sanitize(consultation);
        return Result.ok(consultationService.create(consultation));
    }

    @PutMapping("/{id}")
    public Result<Consultation> update(@PathVariable Long id, @RequestBody Consultation consultation) {
        SecurityInputUtil.requirePositiveId(id, "咨询ID");
        SecurityInputUtil.sanitizeConsultationOptional(consultation);
        return Result.ok(consultationService.update(id, consultation));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        SecurityInputUtil.requirePositiveId(id, "咨询ID");
        consultationService.delete(id);
        return Result.ok("删除成功");
    }

    @GetMapping("/{id}")
    public Result<Consultation> getById(@PathVariable Long id) {
        SecurityInputUtil.requirePositiveId(id, "咨询ID");
        return Result.ok(consultationService.getById(id));
    }

    @GetMapping("/my")
    public Result<PageResult<Consultation>> listMy(HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(consultationService.listByUser(userId, SecurityInputUtil.page(page), SecurityInputUtil.size(size)));
    }

    @GetMapping("/mentor/{mentorId}")
    public Result<PageResult<Consultation>> listByMentor(@PathVariable Long mentorId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        SecurityInputUtil.requirePositiveId(mentorId, "导师ID");
        return Result.ok(consultationService.listByMentor(mentorId, SecurityInputUtil.page(page), SecurityInputUtil.size(size)));
    }

    @GetMapping("/mentor/my")
    public Result<PageResult<Consultation>> listMyMentorConsultations(HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        MentorInfo mentor = mentorService.getByUserId(userId);
        if (mentor == null) {
            return Result.error("不是导师");
        }
        return Result.ok(consultationService.listByMentor(mentor.getId(), SecurityInputUtil.page(page), SecurityInputUtil.size(size)));
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        SecurityInputUtil.requirePositiveId(id, "咨询ID");
        String status = SecurityInputUtil.cleanStatus((String) params.get("status"));
        String feedback = SecurityInputUtil.cleanText((String) params.get("feedback"), 2000, "反馈内容");
        Integer rating = params.get("rating") != null ? ((Number) params.get("rating")).intValue() : null;
        if (rating != null && (rating < 1 || rating > 5)) {
            return Result.error(400, "评分必须为1-5");
        }
        consultationService.updateStatus(id, status, feedback, rating);
        return Result.ok("更新成功");
    }
}
