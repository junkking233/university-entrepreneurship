package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Feedback;
import com.entrepreneurship.mapper.FeedbackMapper;
import com.entrepreneurship.service.FeedbackService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackMapper feedbackMapper;

    public FeedbackServiceImpl(FeedbackMapper feedbackMapper) {
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    public Feedback create(Feedback feedback) {
        feedback.setStatus("pending");
        feedback.setCreateTime(LocalDateTime.now());
        feedbackMapper.insert(feedback);
        return feedback;
    }

    @Override
    public Feedback getById(Long id) {
        return feedbackMapper.selectById(id);
    }

    @Override
    public PageResult<Feedback> listByUser(Long userId, int page, int size) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getUserId, userId);
        wrapper.orderByDesc(Feedback::getCreateTime);
        Page<Feedback> mpPage = new Page<>(page, size);
        Page<Feedback> result = feedbackMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public PageResult<Feedback> listAll(int page, int size, String status) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Feedback::getStatus, status);
        }
        wrapper.orderByDesc(Feedback::getCreateTime);
        Page<Feedback> mpPage = new Page<>(page, size);
        Page<Feedback> result = feedbackMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public void reply(Long id, String reply) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback != null) {
            feedback.setReply(reply);
            feedback.setStatus("replied");
            feedback.setUpdateTime(LocalDateTime.now());
            feedbackMapper.updateById(feedback);
        }
    }

    @Override
    public void updateStatus(Long id, String status) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback != null) {
            feedback.setStatus(status);
            feedback.setUpdateTime(LocalDateTime.now());
            feedbackMapper.updateById(feedback);
        }
    }
}
