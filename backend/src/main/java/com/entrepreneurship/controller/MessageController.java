package com.entrepreneurship.controller;

import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.common.Result;
import com.entrepreneurship.entity.Message;
import com.entrepreneurship.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public Result<Message> send(@RequestBody Message message, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        message.setFromUserId(userId);
        Message result = messageService.send(message);
        return Result.ok(result);
    }

    @GetMapping("/inbox")
    public Result<PageResult<Message>> inbox(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<Message> result = messageService.listReceived(userId, page, size);
        return Result.ok(result);
    }

    @GetMapping("/sent")
    public Result<PageResult<Message>> sent(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<Message> result = messageService.listSent(userId, page, size);
        return Result.ok(result);
    }

    @GetMapping("/unread/count")
    public Result<Map<String, Object>> unreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        int count = messageService.getUnreadCount(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return Result.ok(result);
    }

    @PutMapping("/{id}/read")
    public Result<?> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return Result.ok("标记已读成功");
    }
}
