package com.vt.backend.auth;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

  // token -> userId
  private final Map<String, Integer> sessions = new ConcurrentHashMap<>();

  public String createSession(Integer userId) {
    String token = UUID.randomUUID().toString();
    sessions.put(token, userId);
    return token;
  }

  public Integer getUserIdFromToken(String token) {
    if (token == null) return null;
    return sessions.get(token);
  }

  public void invalidate(String token) {
    if (token != null) sessions.remove(token);
  }
}
