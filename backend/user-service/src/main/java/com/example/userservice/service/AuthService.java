package com.example.userservice.service;

import com.example.userservice.dto.RegisterRequest;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    void register(RegisterRequest request) throws BadRequestException;

}
