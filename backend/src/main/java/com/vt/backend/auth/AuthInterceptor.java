package com.vt.backend.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

  private final AuthService authService;

  public AuthInterceptor(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

    String path = request.getRequestURI();

    // public routes
    if (path.startsWith("/auth")) return true;

    // allow preflight
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

    String token = request.getHeader("X-Auth-Token");
    Integer userId = authService.getUserIdFromToken(token);

    if (userId == null) {
      response.setStatus(401);
      return false;
    }

    request.setAttribute("userId", userId);
    return true;
  }
}
