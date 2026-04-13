package com.alexander.springboot.r_siiau_backend.exceptions;

public class MyUserNotFoundException extends RuntimeException {
    public MyUserNotFoundException(String message) {
        super(message);
    }
    
}
