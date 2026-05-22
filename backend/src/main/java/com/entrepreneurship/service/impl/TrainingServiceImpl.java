package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.BusinessException;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Training;
import com.entrepreneurship.entity.TrainingRegistration;
import com.entrepreneurship.mapper.TrainingMapper;
import com.entrepreneurship.mapper.TrainingRegistrationMapper;
import com.entrepreneurship.service.TrainingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingMapper trainingMapper;
    private final TrainingRegistrationMapper trainingRegistrationMapper;

    public TrainingServiceImpl(TrainingMapper trainingMapper, TrainingRegistrationMapper trainingRegistrationMapper) {
        this.trainingMapper = trainingMapper;
        this.trainingRegistrationMapper = trainingRegistrationMapper;
    }

    @Override
    public Training create(Training training) {
        training.setCurrentParticipants(0);
        training.setStatus("active");
        training.setCreateTime(LocalDateTime.now());
        trainingMapper.insert(training);
        return training;
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
            return existing;
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        trainingMapper.deleteById(id);
    }

    @Override
    public Training getById(Long id) {
        return trainingMapper.selectById(id);
    }

    @Override
    public PageResult<Training> list(int page, int size, String status) {
        LambdaQueryWrapper<Training> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Training::getStatus, status);
        }
        wrapper.orderByDesc(Training::getCreateTime);
        Page<Training> mpPage = new Page<>(page, size);
        Page<Training> result = trainingMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
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
}
