package com.example.gateway;

import org.apache.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleLoggingFilter extends AbstractRequestLoggingFilter {

    private static final Logger logger = Logger.getLogger(SimpleLoggingFilter.class);

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        String method = request.getMethod();
        StringBuffer requestURL = request.getRequestURL();
        
        logger.info("-------");
        logger.info("Logging cookies:");
        logCookies(request);
        logger.info("Logging headers:");
        logHeaders(request);
        logger.info(String.format("%s request to %s", method, requestURL.toString()));
        logger.info("-------");
    }

    private void logCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                logger.info(cookie.getName() + ": " + cookie.getValue());
            }
        }
    }

    private void logHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }

        headers.forEach((key, value) -> logger.info(key + ": " + value));
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {

    }
}
