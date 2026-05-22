package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneurship.entity.MentorInfo;
import com.entrepreneurship.mapper.MentorInfoMapper;
import com.entrepreneurship.service.MentorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class MentorServiceImpl implements MentorService {

    private final MentorInfoMapper mentorInfoMapper;

    public MentorServiceImpl(MentorInfoMapper mentorInfoMapper) {
        this.mentorInfoMapper = mentorInfoMapper;
    }

    @Override
    public List<MentorInfo> listAll() {
        return mentorInfoMapper.selectList(null);
    }

    @Override
    public MentorInfo getById(Long id) {
        return mentorInfoMapper.selectById(id);
    }

    @Override
    public MentorInfo getByUserId(Long userId) {
        LambdaQueryWrapper<MentorInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MentorInfo::getUserId, userId);
        return mentorInfoMapper.selectOne(wrapper);
    }

    @Override
    public void updateMentorInfo(Long userId, MentorInfo mentorInfo) {
        LambdaQueryWrapper<MentorInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MentorInfo::getUserId, userId);
        MentorInfo existing = mentorInfoMapper.selectOne(wrapper);
        if (existing != null) {
            if (mentorInfo.getTitle() != null) existing.setTitle(mentorInfo.getTitle());
            if (mentorInfo.getOrganization() != null) existing.setOrganization(mentorInfo.getOrganization());
            if (mentorInfo.getExpertise() != null) existing.setExpertise(mentorInfo.getExpertise());
            if (mentorInfo.getBio() != null) existing.setBio(mentorInfo.getBio());
            if (mentorInfo.getAvatar() != null) existing.setAvatar(mentorInfo.getAvatar());
            mentorInfoMapper.updateById(existing);
        }
    }

    @Override
    public void updateRating(Long mentorId, Integer rating) {
        MentorInfo mentor = mentorInfoMapper.selectById(mentorId);
        if (mentor != null) {
            int count = mentor.getConsultCount() != null ? mentor.getConsultCount() : 0;
            BigDecimal oldRating = mentor.getRating() != null ? mentor.getRating() : BigDecimal.valueOf(5.0);
            BigDecimal newRating = oldRating.multiply(BigDecimal.valueOf(count))
                    .add(BigDecimal.valueOf(rating))
                    .divide(BigDecimal.valueOf(count + 1), 1, RoundingMode.HALF_UP);
            mentor.setRating(newRating);
            mentor.setConsultCount(count + 1);
            mentorInfoMapper.updateById(mentor);
        }
    }
}
