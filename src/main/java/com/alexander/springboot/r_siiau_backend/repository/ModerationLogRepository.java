package com.alexander.springboot.r_siiau_backend.repository;

import com.alexander.springboot.r_siiau_backend.entity.ModerationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModerationLogRepository extends JpaRepository<ModerationLog, UUID> {
}
