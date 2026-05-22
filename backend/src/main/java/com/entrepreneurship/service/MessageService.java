package com.entrepreneurship.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Message;

public interface MessageService {
    Message send(Message message);
    PageResult<Message> listReceived(Long userId, int page, int size);
    PageResult<Message> listSent(Long userId, int page, int size);
    Message getById(Long id);
    void markAsRead(Long id);
    int getUnreadCount(Long userId);
    void delete(Long id);
}
