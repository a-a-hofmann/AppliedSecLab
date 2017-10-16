package com.example;

import org.apache.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleLoggingFilter extends AbstractRequestLoggingFilter {

    private static final Logger logger = Logger.getLogger(SimpleLoggingFilter.class);

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        String method = request.getMethod();
        StringBuffer requestURL = request.getRequestURL();

        logger.info(String.format("%s request to %s", method, requestURL.toString()));
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {

    }
}
