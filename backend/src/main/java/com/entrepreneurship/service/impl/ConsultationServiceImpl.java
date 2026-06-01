package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Consultation;
import com.entrepreneurship.entity.MentorInfo;
import com.entrepreneurship.entity.Project;
import com.entrepreneurship.entity.User;
import com.entrepreneurship.mapper.ConsultationMapper;
import com.entrepreneurship.mapper.MentorInfoMapper;
import com.entrepreneurship.mapper.ProjectMapper;
import com.entrepreneurship.mapper.UserMapper;
import com.entrepreneurship.service.ConsultationService;
import com.entrepreneurship.service.MentorService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationMapper consultationMapper;
    private final MentorService mentorService;
    private final UserMapper userMapper;
    private final MentorInfoMapper mentorInfoMapper;
    private final ProjectMapper projectMapper;

    public ConsultationServiceImpl(ConsultationMapper consultationMapper,
                                   MentorService mentorService,
                                   UserMapper userMapper,
                                   MentorInfoMapper mentorInfoMapper,
                                   ProjectMapper projectMapper) {
        this.consultationMapper = consultationMapper;
        this.mentorService = mentorService;
        this.userMapper = userMapper;
        this.mentorInfoMapper = mentorInfoMapper;
        this.projectMapper = projectMapper;
    }

    @Override
    public Consultation create(Consultation consultation) {
        consultation.setStatus("pending");
        consultation.setCreateTime(LocalDateTime.now());
        if (consultation.getTopic() == null || consultation.getTopic().isEmpty()) {
            consultation.setTopic("创业咨询");
        }
        consultationMapper.insert(consultation);
        return enrich(consultation);
    }

    @Override
    public Consultation update(Long id, Consultation consultation) {
        Consultation existing = consultationMapper.selectById(id);
        if (existing != null) {
            if (consultation.getTopic() != null) existing.setTopic(consultation.getTopic());
            if (consultation.getContent() != null) existing.setContent(consultation.getContent());
            if (consultation.getScheduledTime() != null) existing.setScheduledTime(consultation.getScheduledTime());
            consultationMapper.updateById(existing);
            return enrich(existing);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        consultationMapper.deleteById(id);
    }

    @Override
    public Consultation getById(Long id) {
        return enrich(consultationMapper.selectById(id));
    }

    @Override
    public PageResult<Consultation> listByUser(Long userId, int page, int size) {
        LambdaQueryWrapper<Consultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Consultation::getStudentId, userId);
        wrapper.orderByDesc(Consultation::getCreateTime);
        Page<Consultation> mpPage = new Page<>(page, size);
        Page<Consultation> result = consultationMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), enrich(result.getRecords()));
    }

    @Override
    public PageResult<Consultation> listByMentor(Long mentorId, int page, int size) {
        LambdaQueryWrapper<Consultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Consultation::getMentorId, mentorId);
        wrapper.orderByDesc(Consultation::getCreateTime);
        Page<Consultation> mpPage = new Page<>(page, size);
        Page<Consultation> result = consultationMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), enrich(result.getRecords()));
    }

    @Override
    public void updateStatus(Long id, String status, String feedback, Integer rating) {
        Consultation consultation = consultationMapper.selectById(id);
        if (consultation != null) {
            consultation.setStatus(status);
            if (feedback != null) consultation.setNotes(feedback);
            if (rating != null) {
                mentorService.updateRating(consultation.getMentorId(), rating);
            }
            consultationMapper.updateById(consultation);
        }
    }

    private Consultation enrich(Consultation consultation) {
        if (consultation == null) {
            return null;
        }
        if (consultation.getStudentId() != null) {
            User student = userMapper.selectById(consultation.getStudentId());
            if (student != null) {
                consultation.setStudentName(student.getName() != null && !student.getName().isEmpty() ? student.getName() : student.getUsername());
            }
        }
        if (consultation.getMentorId() != null) {
            MentorInfo mentor = mentorInfoMapper.selectById(consultation.getMentorId());
            if (mentor != null && mentor.getUserId() != null) {
                User mentorUser = userMapper.selectById(mentor.getUserId());
                if (mentorUser != null) {
                    consultation.setMentorName(mentorUser.getName() != null && !mentorUser.getName().isEmpty() ? mentorUser.getName() : mentorUser.getUsername());
                }
            }
        }
        if (consultation.getProjectId() != null) {
            Project project = projectMapper.selectById(consultation.getProjectId());
            if (project != null) {
                consultation.setProjectTitle(project.getTitle());
            }
        }
        return consultation;
    }

    private List<Consultation> enrich(List<Consultation> consultations) {
        consultations.forEach(this::enrich);
        return consultations;
    }
}
