package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneurship.entity.Investment;
import com.entrepreneurship.entity.Project;
import com.entrepreneurship.entity.User;
import com.entrepreneurship.entity.Feedback;
import com.entrepreneurship.mapper.FeedbackMapper;
import com.entrepreneurship.mapper.InvestmentMapper;
import com.entrepreneurship.mapper.ProjectMapper;
import com.entrepreneurship.mapper.UserMapper;
import com.entrepreneurship.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;
    private final InvestmentMapper investmentMapper;
    private final FeedbackMapper feedbackMapper;

    public StatisticsServiceImpl(ProjectMapper projectMapper, UserMapper userMapper, InvestmentMapper investmentMapper, FeedbackMapper feedbackMapper) {
        this.projectMapper = projectMapper;
        this.userMapper = userMapper;
        this.investmentMapper = investmentMapper;
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    public List<Map<String, Object>> getProjectCategoryDistribution() {
        List<Project> projects = projectMapper.selectList(null);
        Map<String, Long> categoryCount = projects.stream()
                .collect(Collectors.groupingBy(p -> p.getCategory() != null ? p.getCategory() : "未分类", Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : categoryCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getMonthlyProjectTrend() {
        List<Project> projects = projectMapper.selectList(null);
        Map<String, Long> monthlyCount = new TreeMap<>();

        for (Project p : projects) {
            if (p.getCreateTime() != null) {
                String month = p.getCreateTime().getYear() + "-" + String.format("%02d", p.getCreateTime().getMonthValue());
                monthlyCount.merge(month, 1L, Long::sum);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : monthlyCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getUserRoleDistribution() {
        List<User> users = userMapper.selectList(null);
        Map<String, Long> roleCount = users.stream()
                .collect(Collectors.groupingBy(u -> u.getRole() != null ? u.getRole() : "unknown", Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : roleCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        List<Project> projects = projectMapper.selectList(null);
        List<User> users = userMapper.selectList(null);
        List<Investment> investments = investmentMapper.selectList(null);
        List<Feedback> feedbacks = feedbackMapper.selectList(null);

        stats.put("totalProjects", projects.size());
        stats.put("approvedProjects", projects.stream().filter(p -> "approved".equals(p.getStatus())).count());
        stats.put("pendingProjects", projects.stream().filter(p -> "pending".equals(p.getStatus())).count());
        stats.put("totalUsers", users.size());
        stats.put("totalInvestments", investments.size());
        stats.put("pendingFeedback", feedbacks.stream().filter(f -> "pending".equals(f.getStatus()) || "processing".equals(f.getStatus())).count());

        BigDecimal totalAmount = investments.stream()
                .map(i -> i.getAmount() != null ? i.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalInvestmentAmount", totalAmount);

        return stats;
    }
}
