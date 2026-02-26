package com.school.service;

import com.school.dto.LoginRequest;
import com.school.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}

