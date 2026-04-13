package com.alexander.springboot.r_siiau_backend.model;

import com.alexander.springboot.r_siiau_backend.enums.AcademicLevel;
import com.alexander.springboot.r_siiau_backend.enums.MyUserStatus;
import com.alexander.springboot.r_siiau_backend.enums.UserRole;
import jakarta.validation.constraints.*;

public record CreateUserM(@NotBlank
                          String name,

                          @NotBlank
                          String lastname,

                          @NotNull
                          @Size(min = 6, max = 15)
                          String code,

                          @NotNull
                          AcademicLevel academicLevel,

                          @NotBlank
                          String career,

                          @NotNull
                          Integer degree,

                          @Email
                          @NotBlank
                          String email,

                          @NotBlank
                          @Size(min = 6)
                          /*
                          @Pattern(
                                  regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
                                  message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
                          )
                           */
                          String password,

                          @NotNull
                          UserRole role,

                          @NotNull
                          MyUserStatus status){
}
