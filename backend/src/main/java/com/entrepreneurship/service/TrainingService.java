package com.entrepreneurship.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Training;
import com.entrepreneurship.entity.TrainingRegistration;

public interface TrainingService {
    Training create(Training training);
    Training update(Long id, Training training);
    void delete(Long id);
    Training getById(Long id);
    PageResult<Training> list(int page, int size, String status);
    TrainingRegistration register(Long trainingId, Long userId);
    void cancelRegistration(Long trainingId, Long userId);
    PageResult<TrainingRegistration> listRegistrations(Long userId, int page, int size);
}
