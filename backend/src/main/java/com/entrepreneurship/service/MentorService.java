package com.entrepreneurship.service;

import com.entrepreneurship.entity.MentorInfo;

import java.util.List;

public interface MentorService {
    List<MentorInfo> listAll();
    MentorInfo getById(Long id);
    MentorInfo getByUserId(Long userId);
    void updateMentorInfo(Long userId, MentorInfo mentorInfo);
    void updateRating(Long mentorId, Integer rating);
}
