package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.BusinessException;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.MentorInfo;
import com.entrepreneurship.entity.Training;
import com.entrepreneurship.entity.TrainingRegistration;
import com.entrepreneurship.entity.User;
import com.entrepreneurship.mapper.MentorInfoMapper;
import com.entrepreneurship.mapper.TrainingMapper;
import com.entrepreneurship.mapper.TrainingRegistrationMapper;
import com.entrepreneurship.mapper.UserMapper;
import com.entrepreneurship.service.TrainingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingMapper trainingMapper;
    private final TrainingRegistrationMapper trainingRegistrationMapper;
    private final MentorInfoMapper mentorInfoMapper;
    private final UserMapper userMapper;

    public TrainingServiceImpl(TrainingMapper trainingMapper,
                               TrainingRegistrationMapper trainingRegistrationMapper,
                               MentorInfoMapper mentorInfoMapper,
                               UserMapper userMapper) {
        this.trainingMapper = trainingMapper;
        this.trainingRegistrationMapper = trainingRegistrationMapper;
        this.mentorInfoMapper = mentorInfoMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Training create(Training training) {
        training.setCurrentParticipants(0);
        if (training.getStatus() == null || training.getStatus().isEmpty()) {
            training.setStatus("upcoming");
        }
        training.setCreateTime(LocalDateTime.now());
        trainingMapper.insert(training);
        return enrich(training);
    }

    @Override
    public Training update(Long id, Training training) {
        Training existing = trainingMapper.selectById(id);
        if (existing != null) {
            if (training.getTitle() != null) existing.setTitle(training.getTitle());
            if (training.getDescription() != null) existing.setDescription(training.getDescription());
            if (training.getInstructor() != null) existing.setInstructor(training.getInstructor());
            if (training.getStartTime() != null) existing.setStartTime(training.getStartTime());
            if (training.getEndTime() != null) existing.setEndTime(training.getEndTime());
            if (training.getLocation() != null) existing.setLocation(training.getLocation());
            if (training.getMaxParticipants() != null) existing.setMaxParticipants(training.getMaxParticipants());
            if (training.getStatus() != null) existing.setStatus(training.getStatus());
            if (training.getCoverImage() != null) existing.setCoverImage(training.getCoverImage());
            trainingMapper.updateById(existing);
            return enrich(existing);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        trainingMapper.deleteById(id);
    }

    @Override
    public Training getById(Long id) {
        return enrich(trainingMapper.selectById(id));
    }

    @Override
    public PageResult<Training> list(int page, int size, String status, String keyword) {
        return listInternal(page, size, status, keyword, null);
    }

    @Override
    public PageResult<Training> listByMentor(Long mentorId, int page, int size, String status, String keyword) {
        return listInternal(page, size, status, keyword, mentorId);
    }

    private PageResult<Training> listInternal(int page, int size, String status, String keyword, Long mentorId) {
        LambdaQueryWrapper<Training> wrapper = new LambdaQueryWrapper<>();
        if (mentorId != null) {
            wrapper.eq(Training::getMentorId, mentorId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Training::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Training::getTitle, keyword)
                    .or().like(Training::getDescription, keyword)
                    .or().like(Training::getLocation, keyword));
        }
        wrapper.orderByDesc(Training::getCreateTime);
        Page<Training> mpPage = new Page<>(page, size);
        Page<Training> result = trainingMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), enrich(result.getRecords()));
    }

    @Override
    @Transactional
    public TrainingRegistration register(Long trainingId, Long userId) {
        Training training = trainingMapper.selectById(trainingId);
        if (training == null) {
            throw new BusinessException("培训活动不存在");
        }
        if (training.getMaxParticipants() != null && training.getCurrentParticipants() != null
                && training.getCurrentParticipants() >= training.getMaxParticipants()) {
            throw new BusinessException("报名人数已满");
        }

        LambdaQueryWrapper<TrainingRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingRegistration::getTrainingId, trainingId);
        wrapper.eq(TrainingRegistration::getUserId, userId);
        if (trainingRegistrationMapper.selectOne(wrapper) != null) {
            throw new BusinessException("已报名，无需重复报名");
        }

        TrainingRegistration registration = new TrainingRegistration();
        registration.setTrainingId(trainingId);
        registration.setUserId(userId);
        registration.setStatus("registered");
        registration.setRegisterTime(LocalDateTime.now());
        trainingRegistrationMapper.insert(registration);

        training.setCurrentParticipants((training.getCurrentParticipants() != null ? training.getCurrentParticipants() : 0) + 1);
        trainingMapper.updateById(training);

        return registration;
    }

    @Override
    @Transactional
    public void cancelRegistration(Long trainingId, Long userId) {
        LambdaQueryWrapper<TrainingRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingRegistration::getTrainingId, trainingId);
        wrapper.eq(TrainingRegistration::getUserId, userId);
        TrainingRegistration registration = trainingRegistrationMapper.selectOne(wrapper);
        if (registration != null) {
            trainingRegistrationMapper.deleteById(registration.getId());
            Training training = trainingMapper.selectById(trainingId);
            if (training != null && training.getCurrentParticipants() != null && training.getCurrentParticipants() > 0) {
                training.setCurrentParticipants(training.getCurrentParticipants() - 1);
                trainingMapper.updateById(training);
            }
        }
    }

    @Override
    public PageResult<TrainingRegistration> listRegistrations(Long userId, int page, int size) {
        LambdaQueryWrapper<TrainingRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingRegistration::getUserId, userId);
        wrapper.orderByDesc(TrainingRegistration::getRegisterTime);
        Page<TrainingRegistration> mpPage = new Page<>(page, size);
        Page<TrainingRegistration> result = trainingRegistrationMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    private Training enrich(Training training) {
        if (training == null || training.getMentorId() == null) {
            return training;
        }
        MentorInfo mentor = mentorInfoMapper.selectById(training.getMentorId());
        if (mentor != null && mentor.getUserId() != null) {
            User user = userMapper.selectById(mentor.getUserId());
            if (user != null) {
                training.setMentorName(user.getName() != null && !user.getName().isEmpty() ? user.getName() : user.getUsername());
            }
        }
        return training;
    }

    private List<Training> enrich(List<Training> trainings) {
        trainings.forEach(this::enrich);
        return trainings;
    }
}
