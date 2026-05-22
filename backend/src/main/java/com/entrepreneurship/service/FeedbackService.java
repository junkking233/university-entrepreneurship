package com.entrepreneurship.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Feedback;

public interface FeedbackService {
    Feedback create(Feedback feedback);
    Feedback getById(Long id);
    PageResult<Feedback> listByUser(Long userId, int page, int size);
    PageResult<Feedback> listAll(int page, int size, String status);
    void reply(Long id, String reply);
    void updateStatus(Long id, String status);
}
