package com.alexander.springboot.r_siiau_backend.service;

import com.alexander.springboot.r_siiau_backend.dto.MyUserDTO;
import com.alexander.springboot.r_siiau_backend.model.CreateUserM;
import com.alexander.springboot.r_siiau_backend.model.UpdatePasswordM;
import com.alexander.springboot.r_siiau_backend.model.UpdateUserM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MyUserService {
    Page<MyUserDTO> findAll(Pageable pageable);
    Optional<MyUserDTO> findById(UUID id);
    Optional<MyUserDTO> findByCode(String code);
    Optional<MyUserDTO> findByEmail(String email);
    MyUserDTO create(CreateUserM userM);
    Page<MyUserDTO> saveAll(List<CreateUserM> users, Pageable pageable);
    MyUserDTO update(UUID id, UpdateUserM userM);
    boolean updatePassword(UUID id, UpdatePasswordM passwordM);
    void delete(UUID id);
}
