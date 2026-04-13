package com.alexander.springboot.r_siiau_backend.controller.admin;

import com.alexander.springboot.r_siiau_backend.dto.MyUserDTO;
import com.alexander.springboot.r_siiau_backend.exceptions.MyUserNotFoundException;
import com.alexander.springboot.r_siiau_backend.model.CreateUserM;
import com.alexander.springboot.r_siiau_backend.model.UpdatePasswordM;
import com.alexander.springboot.r_siiau_backend.model.UpdateUserM;
import com.alexander.springboot.r_siiau_backend.service.MyUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class MyUserControllerADMIN {

    @Autowired
    private MyUserService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

    @GetMapping
    public ResponseEntity<Page<MyUserDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/id")
    public ResponseEntity<MyUserDTO> getById(@RequestParam UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new MyUserNotFoundException("User with id:" + id + ", not found."));
    }

    @GetMapping("/code")
    public ResponseEntity<MyUserDTO> getByCode(@RequestParam String code){
        return service.findByCode(code)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new MyUserNotFoundException("User with code:" + code + ", not found."));
    }

    @GetMapping("/email")
    public ResponseEntity<MyUserDTO> getByEmail(@RequestParam String email){
        return service.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new MyUserNotFoundException("User with email:" + email + ", not found."));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestPart(value = "sendData") String postDataJson) {
        CreateUserM userM;
        
        try {
            userM = objectMapper.readValue(postDataJson, CreateUserM.class);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid JSON format: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // Validación manual usando el validador de Spring
        Set<ConstraintViolation<CreateUserM>> violations = validator.validate(userM);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<CreateUserM> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        MyUserDTO created = service.create(userM);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulk(
            @RequestPart(value = "sendData") String postDataJson,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<CreateUserM> users;
        try {
            users = objectMapper.readValue(postDataJson, new com.fasterxml.jackson.core.type.TypeReference<List<CreateUserM>>() {});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid JSON format: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        Map<Integer, Map<String, String>> allErrors = new HashMap<>();
        for (int i = 0; i < users.size(); i++) {
            Set<ConstraintViolation<CreateUserM>> violations = validator.validate(users.get(i));
            if (!violations.isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                for (ConstraintViolation<CreateUserM> violation : violations) {
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                }
                allErrors.put(i, errors);
            }
        }
        if (!allErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(allErrors);
        }

        Pageable pageable = PageRequest.of(page, size);
        var createdPage = service.saveAll(users, pageable);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPage);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestParam UUID id, @RequestPart(value = "sendData") String postDataJson) {
        UpdateUserM userM;
        try {
            userM = objectMapper.readValue(postDataJson, UpdateUserM.class);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid JSON format: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // Validación manual usando el validador de Spring
        Set<ConstraintViolation<UpdateUserM>> violations = validator.validate(userM);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<UpdateUserM> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        MyUserDTO updated = service.update(id, userM);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestParam UUID id, @RequestPart(value = "sendData") String postDataJson) {
        UpdatePasswordM passwordM;
        try {
            passwordM = objectMapper.readValue(postDataJson, UpdatePasswordM.class);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid JSON format: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // Validación manual usando el validador de Spring
        Set<ConstraintViolation<UpdatePasswordM>> violations = validator.validate(passwordM);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<UpdatePasswordM> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        if(service.updatePassword(id, passwordM)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
