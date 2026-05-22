package com.entrepreneurship.controller;

import com.entrepreneurship.common.Result;
import com.entrepreneurship.entity.MentorInfo;
import com.entrepreneurship.service.MentorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {

    private final MentorService mentorService;

    public MentorController(MentorService mentorService) {
        this.mentorService = mentorService;
    }

    @GetMapping("/list")
    public Result<List<MentorInfo>> listAll() {
        return Result.ok(mentorService.listAll());
    }

    @GetMapping("/{id}")
    public Result<MentorInfo> getById(@PathVariable Long id) {
        return Result.ok(mentorService.getById(id));
    }

    @GetMapping("/profile")
    public Result<MentorInfo> getMyProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(mentorService.getByUserId(userId));
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody MentorInfo mentorInfo, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        mentorService.updateMentorInfo(userId, mentorInfo);
        return Result.ok("更新成功");
    }
}
