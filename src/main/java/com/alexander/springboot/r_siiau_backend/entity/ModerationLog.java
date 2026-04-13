package com.alexander.springboot.r_siiau_backend.entity;

import com.alexander.springboot.r_siiau_backend.convert.MapToJsonConverter;
import com.alexander.springboot.r_siiau_backend.enums.Actions;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@Table(name = "moderation_log")
public class ModerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_user", nullable = false)
    private MyUser myUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Actions action;

    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "details_json", columnDefinition = "TEXT")
    private Map<String, Object> details = new HashMap<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
