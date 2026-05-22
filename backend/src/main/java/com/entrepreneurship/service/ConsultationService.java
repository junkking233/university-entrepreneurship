package com.entrepreneurship.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Consultation;

public interface ConsultationService {
    Consultation create(Consultation consultation);
    Consultation update(Long id, Consultation consultation);
    void delete(Long id);
    Consultation getById(Long id);
    PageResult<Consultation> listByUser(Long userId, int page, int size);
    PageResult<Consultation> listByMentor(Long mentorId, int page, int size);
    void updateStatus(Long id, String status, String feedback, Integer rating);
}
