package com.entrepreneurship.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    List<Map<String, Object>> getProjectCategoryDistribution();
    List<Map<String, Object>> getMonthlyProjectTrend();
    List<Map<String, Object>> getUserRoleDistribution();
    Map<String, Object> getDashboardStats();
}
