package com.alexander.springboot.r_siiau_backend.service.impl;

import com.alexander.springboot.r_siiau_backend.dto.MyUserDTO;
import com.alexander.springboot.r_siiau_backend.entity.MyUser;
import com.alexander.springboot.r_siiau_backend.exceptions.MyUserNotFoundException;
import com.alexander.springboot.r_siiau_backend.model.CreateUserM;
import com.alexander.springboot.r_siiau_backend.model.UpdatePasswordM;
import com.alexander.springboot.r_siiau_backend.model.UpdateUserM;
import com.alexander.springboot.r_siiau_backend.repository.MyUserRepository;
import com.alexander.springboot.r_siiau_backend.service.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MyUserServiceImpl implements MyUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final MyUserRepository repository;

    public MyUserServiceImpl(MyUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<MyUserDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::convertToDTO);
    }

    @Override
    public Optional<MyUserDTO> findById(UUID id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    @Override
    public Optional<MyUserDTO> findByCode(String code) {
        return repository.findByCode(code).map(this::convertToDTO);
    }

    @Override
    public Optional<MyUserDTO> findByEmail(String email) {
        return repository.findByEmail(email).map(this::convertToDTO);
    }

    @Override
    public MyUserDTO create(CreateUserM userM) {
        String code = userM.code();
        Optional<MyUser> userDBC = repository.findByCode(code);
        if(userDBC.isPresent()){
            throw new RuntimeException("Code: " + code + ", is already registered.");
        }
        String email = userM.email();
        Optional<MyUser> userDBE = repository.findByEmail(email);
        if(userDBE.isPresent()){
            throw new RuntimeException("Email: " + email + ", is already registered.");
        }
        MyUser user = convertToModel(userM);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        MyUser saved = repository.save(user);
        return convertToDTO(saved);
    }

    @Override
    public MyUserDTO update(UUID id, UpdateUserM userM) {
        MyUser existing = repository.findById(id)
                .orElseThrow(() -> new MyUserNotFoundException("User with id:" + id + ", not found."));

        existing.setName(userM.name());
        existing.setLastname(userM.lastname());
        existing.setAcademicLevel(userM.academicLevel());
        existing.setCareer(userM.career());
        existing.setDegree(userM.degree());
        existing.setRole(userM.role());
        existing.setStatus(userM.status());
        MyUser updated = repository.save(existing);
        return convertToDTO(updated);
    }

    @Override
    public Page<MyUserDTO> saveAll(List<CreateUserM> users, Pageable pageable) {
        for (CreateUserM userM : users) {
            String code = userM.code();
            repository.findByCode(code).ifPresent(u -> {
                throw new RuntimeException("Code: " + code + ", is already registered.");
            });
            String email = userM.email();
            repository.findByEmail(email).ifPresent(u -> {
                throw new RuntimeException("Email: " + email + ", is already registered.");
            });
        }

        var entities = users.stream()

                .map(this::convertToModel)
                .peek(u -> u.setPasswordHash(passwordEncoder.encode(u.getPasswordHash())))
                .toList();

        var saved = repository.saveAll(entities);
        var dtos = saved.stream()
                .map(this::convertToDTO)
                .toList();

        int total = dtos.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);Math.min(start + pageable.getPageSize(), total);
        java.util.List<MyUserDTO> content = start <= end ? dtos.subList(start, end) : java.util.List.of();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public boolean updatePassword(UUID id, UpdatePasswordM passwordM) {
        MyUser existing = repository.findById(id)
                .orElseThrow(() -> new MyUserNotFoundException("User with id:" + id + ", not found."));

        if (passwordEncoder.matches(passwordM.oldPassword(), existing.getPasswordHash())) {
            existing.setPasswordHash(passwordEncoder.encode(passwordM.newPassword()));
            repository.save(existing);
            return true;
        }

        return false;
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new MyUserNotFoundException("User with id:" + id + ", not found.");
        }
        repository.deleteById(id);
    }

    private MyUserDTO convertToDTO(MyUser user) {
        return new MyUserDTO(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getCode(),
                user.getAcademicLevel(),
                user.getCareer(),
                user.getDegree(),
                user.getRole(),
                user.getStatus(),
                user.getEmail(),
                user.getFirstLogin(),
                user.getLastLogin(),
                user.getCreatedDate(),
                user.getUpdateDate()
        );
    }

    private MyUser convertToModel(CreateUserM userM){
        MyUser user = new MyUser();
        user.setName(userM.name());
        user.setLastname(userM.lastname());
        user.setCode(userM.code());
        user.setAcademicLevel(userM.academicLevel());
        user.setCareer(userM.career());
        user.setDegree(userM.degree());
        user.setEmail(userM.email());
        user.setPasswordHash(userM.password());
        user.setRole(userM.role());
        user.setStatus(userM.status());
        return user;
    }
}
