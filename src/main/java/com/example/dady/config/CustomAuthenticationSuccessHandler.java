package com.example.dady.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        session.setAttribute("user", authentication.getName());
        
        // Set session timeout to 30 minutes
        session.setMaxInactiveInterval(30 * 60);
        
        // Redirect to dashboard with correct path
        response.sendRedirect("/dashboard");
    }
} 