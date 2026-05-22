package com.entrepreneurship.controller;

import com.entrepreneurship.common.Result;
import com.entrepreneurship.entity.Investment;
import com.entrepreneurship.mapper.InvestmentMapper;
import com.entrepreneurship.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final InvestmentMapper investmentMapper;

    public StatisticsController(StatisticsService statisticsService, InvestmentMapper investmentMapper) {
        this.statisticsService = statisticsService;
        this.investmentMapper = investmentMapper;
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> stats = statisticsService.getDashboardStats();
        return Result.ok(stats);
    }

    @GetMapping("/projects/category")
    public Result<List<Map<String, Object>>> projectCategoryDistribution() {
        List<Map<String, Object>> data = statisticsService.getProjectCategoryDistribution();
        return Result.ok(data);
    }

    @GetMapping("/projects/monthly")
    public Result<List<Map<String, Object>>> monthlyProjectTrend() {
        List<Map<String, Object>> data = statisticsService.getMonthlyProjectTrend();
        return Result.ok(data);
    }

    @GetMapping("/users/role")
    public Result<List<Map<String, Object>>> userRoleDistribution() {
        List<Map<String, Object>> data = statisticsService.getUserRoleDistribution();
        return Result.ok(data);
    }

    @GetMapping("/investments/monthly")
    public Result<List<Map<String, Object>>> monthlyInvestmentTrend() {
        List<Investment> investments = investmentMapper.selectList(null);
        Map<String, BigDecimal> monthlyAmount = new TreeMap<>();
        Map<String, Integer> monthlyCount = new TreeMap<>();

        for (Investment inv : investments) {
            if (inv.getCreateTime() != null) {
                String month = inv.getCreateTime().getYear() + "-" + String.format("%02d", inv.getCreateTime().getMonthValue());
                monthlyAmount.merge(month, inv.getAmount() != null ? inv.getAmount() : BigDecimal.ZERO, BigDecimal::add);
                monthlyCount.merge(month, 1, Integer::sum);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (String month : monthlyAmount.keySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", month);
            item.put("amount", monthlyAmount.get(month));
            item.put("count", monthlyCount.get(month));
            result.add(item);
        }
        return Result.ok(result);
    }
}
