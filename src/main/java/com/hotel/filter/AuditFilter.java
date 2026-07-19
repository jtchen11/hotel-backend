package com.hotel.filter;

import com.hotel.entity.AuditLog;
import com.hotel.mapper.AuditLogMapper;
import com.hotel.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class AuditFilter implements Filter {

    @Autowired
    private AuditLogMapper auditLogMapper;

    private static final Set<String> WRITE_METHODS = new HashSet<>(Arrays.asList("POST", "PUT", "DELETE"));

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();
        String method = req.getMethod().toUpperCase();

        if (!uri.startsWith("/api/") || !WRITE_METHODS.contains(method)) {
            chain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();

        try {
            chain.doFilter(request, response);
        } finally {
            long cost = System.currentTimeMillis() - start;
            int httpStatus = 200;
            if (response instanceof HttpServletResponse) {
                httpStatus = ((HttpServletResponse) response).getStatus();
            }

            AuditLog log = new AuditLog();
            String operator = "";
            String auth = req.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                Claims claims = JwtUtils.parseToken(auth.substring(7));
                if (claims != null) {
                    operator = claims.get("empName", String.class);
                }
            }
            log.setOperator(operator != null ? operator : "");
            log.setOperation("[" + cost + "ms][" + httpStatus + "] " + method + " " + uri);
            log.setResult(httpStatus < 400 ? "SUCCESS" : "HTTP_" + httpStatus);
            log.setIp(getClientIp(req));
            log.setCreatedAt(LocalDateTime.now());
            try {
                auditLogMapper.insert(log);
            } catch (Exception ignored) {}
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getHeader("Proxy-Client-IP");
        if (ip == null || ip.isEmpty()) ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip != null ? ip : "";
    }
}
