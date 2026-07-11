package com.hotel.interceptor;

import com.hotel.common.UserContext;
import com.hotel.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        // 精确匹配登录接口，放行
        if ("/api/login".equals(uri) || "/api/login/failCount".equals(uri) || uri.startsWith("/api/captcha")) {
            return true;
        }

        // 解析 Token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录\"}");
            return false;
        }
        token = token.substring(7);
        Claims claims = JwtUtils.parseToken(token);
        if (claims == null) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"token无效\"}");
            return false;
        }

        Integer empId = claims.get("empId", Integer.class);
        String empName = claims.get("empName", String.class);
        String role = claims.get("role", String.class);
        UserContext.set(empId, empName, role);

        // JWT 滑动过期：剩余时间少于5分钟则自动续期
        long remain = JwtUtils.getRemainingTime(claims);
        if (remain > 0 && remain < 300000) {
            String newToken = JwtUtils.generateToken(empId, empName, role);
            response.setHeader("X-Auth-Token", newToken);
        }

        // 前台接待员接口
        if (uri.startsWith("/api/reception") || uri.startsWith("/api/room")) {
            if (!"前台接待员".equals(role)) {
                response.setStatus(403);
                response.getWriter().write("{\"code\":403,\"msg\":\"无权限访问\"}");
                return false;
            }
        }

        // 营业服务员接口（餐饮、KTV）
        if (uri.startsWith("/api/dining") || uri.startsWith("/api/ktv")) {
            if (!"营业服务员".equals(role)) {
                response.setStatus(403);
                response.getWriter().write("{\"code\":403,\"msg\":\"无权限访问\"}");
                return false;
            }
        }

        // 库房管理相关（/api/warehouse 和 /api/waiter）
        if (uri.startsWith("/api/warehouse")) {
            // 预警接口：财务管理员和营业服务员均可访问
            if (uri.equals("/api/warehouse/warning")) {
                if (!"营业服务员".equals(role) && !"财务管理员".equals(role)) {
                    response.setStatus(403);
                    response.getWriter().write("{\"code\":403,\"msg\":\"无权限访问\"}");
                    return false;
                }
            }
            // 出入库记录：财务管理员可查看（只读）
            else if (uri.equals("/api/warehouse/records")) {
                if (!"营业服务员".equals(role) && !"财务管理员".equals(role)) {
                    response.setStatus(403);
                    response.getWriter().write("{\"code\":403,\"msg\":\"无权限访问\"}");
                    return false;
                }
            }
            // 其他仓库操作（入库、出库、库存列表等）只允许营业服务员
            else {
                if (!"营业服务员".equals(role)) {
                    response.setStatus(403);
                    response.getWriter().write("{\"code\":403,\"msg\":\"无权限访问\"}");
                    return false;
                }
            }
        }

        // 2. 营业服务员的库房管理页面（/api/waiter/stock）允许财务管理员访问（只读）
        if (uri.startsWith("/api/waiter")) {
            if ("财务管理员".equals(role)) {
                // 财务管理员仅允许访问 stock 页面及其接口
                if (!uri.startsWith("/api/waiter/stock")) {
                    response.setStatus(403);
                    response.getWriter().write("{\"code\":403,\"msg\":\"无权限访问\"}");
                    return false;
                }
            } else if (!"营业服务员".equals(role)) {
                response.setStatus(403);
                response.getWriter().write("{\"code\":403,\"msg\":\"无权限访问\"}");
                return false;
            }
        }

        // 财务管理员接口
        if (uri.startsWith("/api/finance") || uri.startsWith("/api/hr")) {
            if (!"财务管理员".equals(role)) {
                response.setStatus(403);
                response.getWriter().write("{\"code\":403,\"msg\":\"无权限访问\"}");
                return false;
            }
        }

        // 总经理接口
        if (uri.startsWith("/api/gm")) {
            if (!"总经理".equals(role)) {
                response.setStatus(403);
                response.getWriter().write("{\"code\":403,\"msg\":\"无权限访问\"}");
                return false;
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}