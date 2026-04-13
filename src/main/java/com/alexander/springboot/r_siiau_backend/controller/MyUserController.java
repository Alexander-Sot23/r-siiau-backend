package com.alexander.springboot.r_siiau_backend.controller;

import com.alexander.springboot.r_siiau_backend.dto.MyUserDTO;
import com.alexander.springboot.r_siiau_backend.exceptions.MyUserNotFoundException;
import com.alexander.springboot.r_siiau_backend.service.MyUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/student")
public class MyUserController {

    @Autowired
    private MyUserService service;

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
}
