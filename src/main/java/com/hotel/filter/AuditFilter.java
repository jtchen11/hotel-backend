package com.hotel.filter;

import com.hotel.common.UserContext;
import com.hotel.entity.AuditLog;
import com.hotel.mapper.AuditLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        try {
            chain.doFilter(request, wrapper);
        } finally {
            long cost = System.currentTimeMillis() - start;

            String resultStatus = "SUCCESS";
            try {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    String body = new String(buf, StandardCharsets.UTF_8);
                    if (body.contains("\"code\":500") || body.contains("\"code\":400")) {
                        resultStatus = "FAILED";
                    }
                }
            } catch (Exception ignored) {}

            AuditLog log = new AuditLog();
            log.setOperator(UserContext.getEmpName() != null ? UserContext.getEmpName() : "");
            log.setOperation("[" + cost + "ms] " + method + " " + uri);
            log.setResult(resultStatus);
            log.setIp(getClientIp(req));
            log.setCreatedAt(LocalDateTime.now());
            try {
                auditLogMapper.insert(log);
            } catch (Exception ignored) {}

            wrapper.copyBodyToResponse();
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
