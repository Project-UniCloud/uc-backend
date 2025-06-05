package com.unicloudapp.auth.application;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Collections;

@Component
class RequestHeaderLogger extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws java.io.IOException, jakarta.servlet.ServletException {

        String origin = request.getHeader("Origin");
        logger.info("Request Origin: " + origin);

        Collections.list(request.getHeaderNames()).forEach(
                name -> logger.info("Header: " + name + " = " + request.getHeader(name))
        );

        filterChain.doFilter(request, response);
    }
}
