package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Consultation;
import com.entrepreneurship.mapper.ConsultationMapper;
import com.entrepreneurship.mapper.MentorInfoMapper;
import com.entrepreneurship.service.ConsultationService;
import com.entrepreneurship.service.MentorService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationMapper consultationMapper;
    private final MentorService mentorService;

    public ConsultationServiceImpl(ConsultationMapper consultationMapper, MentorService mentorService) {
        this.consultationMapper = consultationMapper;
        this.mentorService = mentorService;
    }

    @Override
    public Consultation create(Consultation consultation) {
        consultation.setStatus("pending");
        consultation.setCreateTime(LocalDateTime.now());
        consultationMapper.insert(consultation);
        return consultation;
    }

    @Override
    public Consultation update(Long id, Consultation consultation) {
        Consultation existing = consultationMapper.selectById(id);
        if (existing != null) {
            if (consultation.getTopic() != null) existing.setTopic(consultation.getTopic());
            if (consultation.getDescription() != null) existing.setDescription(consultation.getDescription());
            if (consultation.getAppointmentTime() != null) existing.setAppointmentTime(consultation.getAppointmentTime());
            consultationMapper.updateById(existing);
            return existing;
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        consultationMapper.deleteById(id);
    }

    @Override
    public Consultation getById(Long id) {
        return consultationMapper.selectById(id);
    }

    @Override
    public PageResult<Consultation> listByUser(Long userId, int page, int size) {
        LambdaQueryWrapper<Consultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Consultation::getUserId, userId);
        wrapper.orderByDesc(Consultation::getCreateTime);
        Page<Consultation> mpPage = new Page<>(page, size);
        Page<Consultation> result = consultationMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public PageResult<Consultation> listByMentor(Long mentorId, int page, int size) {
        LambdaQueryWrapper<Consultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Consultation::getMentorId, mentorId);
        wrapper.orderByDesc(Consultation::getCreateTime);
        Page<Consultation> mpPage = new Page<>(page, size);
        Page<Consultation> result = consultationMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public void updateStatus(Long id, String status, String feedback, Integer rating) {
        Consultation consultation = consultationMapper.selectById(id);
        if (consultation != null) {
            consultation.setStatus(status);
            if (feedback != null) consultation.setFeedback(feedback);
            if (rating != null) {
                consultation.setRating(rating);
                mentorService.updateRating(consultation.getMentorId(), rating);
            }
            consultationMapper.updateById(consultation);
        }
    }
}
