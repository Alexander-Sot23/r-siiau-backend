package com.alexander.springboot.r_siiau_backend.security.service;

import com.alexander.springboot.r_siiau_backend.entity.MyUser;

import java.util.UUID;

public interface CurrentUserService {
    MyUser getCurrentUser();
    UUID getCurrentUserId();
    boolean hasRole(String role);
}
