package com.alexander.springboot.r_siiau_backend.repository;

import com.alexander.springboot.r_siiau_backend.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser, UUID> {
    Optional<MyUser> findByCode(String code);
    Optional<MyUser> findByEmail(String email);
}
