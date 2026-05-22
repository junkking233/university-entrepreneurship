package com.entrepreneurship.interceptor;

import com.entrepreneurship.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Map<String, Long> tokenStore = new ConcurrentHashMap<>();

    public static String createToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenStore.put(token, userId);
        return token;
    }

    public static Long getUserIdByToken(String token) {
        return tokenStore.get(token);
    }

    public static void removeToken(String token) {
        tokenStore.remove(token);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(Result.error(401, "未登录，请先登录")));
            return false;
        }

        Long userId = getUserIdByToken(token);
        if (userId == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(Result.error(401, "登录已过期，请重新登录")));
            return false;
        }

        request.setAttribute("userId", userId);
        return true;
    }
}
