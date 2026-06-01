package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Message;
import com.entrepreneurship.entity.User;
import com.entrepreneurship.mapper.MessageMapper;
import com.entrepreneurship.mapper.UserMapper;
import com.entrepreneurship.service.MessageService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    public MessageServiceImpl(MessageMapper messageMapper, UserMapper userMapper) {
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Message send(Message message) {
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);
        return enrich(message);
    }

    @Override
    public PageResult<Message> listReceived(Long userId, int page, int size) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId);
        wrapper.orderByDesc(Message::getCreateTime);
        Page<Message> mpPage = new Page<>(page, size);
        Page<Message> result = messageMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), enrich(result.getRecords()));
    }

    @Override
    public PageResult<Message> listSent(Long userId, int page, int size) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getSenderId, userId);
        wrapper.orderByDesc(Message::getCreateTime);
        Page<Message> mpPage = new Page<>(page, size);
        Page<Message> result = messageMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), enrich(result.getRecords()));
    }

    @Override
    public Message getById(Long id) {
        return enrich(messageMapper.selectById(id));
    }

    @Override
    public void markAsRead(Long id) {
        Message message = messageMapper.selectById(id);
        if (message != null) {
            message.setIsRead(1);
            messageMapper.updateById(message);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId);
        wrapper.eq(Message::getIsRead, 0);
        Message message = new Message();
        message.setIsRead(1);
        messageMapper.update(message, wrapper);
    }

    @Override
    public int getUnreadCount(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId);
        wrapper.eq(Message::getIsRead, 0);
        return messageMapper.selectCount(wrapper).intValue();
    }

    @Override
    public void delete(Long id) {
        messageMapper.deleteById(id);
    }

    private Message enrich(Message message) {
        if (message == null) {
            return null;
        }
        if (message.getSenderId() != null) {
            User sender = userMapper.selectById(message.getSenderId());
            message.setSenderName(displayName(sender));
        }
        if (message.getReceiverId() != null) {
            User receiver = userMapper.selectById(message.getReceiverId());
            message.setReceiverName(displayName(receiver));
        }
        return message;
    }

    private List<Message> enrich(List<Message> messages) {
        messages.forEach(this::enrich);
        return messages;
    }

    private String displayName(User user) {
        if (user == null) {
            return null;
        }
        return user.getName() != null && !user.getName().isEmpty() ? user.getName() : user.getUsername();
    }
}
