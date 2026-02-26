package com.school.service;

import com.school.dto.CreateUserRequest;
import com.school.dto.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
}

