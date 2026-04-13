package com.alexander.springboot.r_siiau_backend.model;

import com.alexander.springboot.r_siiau_backend.enums.AcademicLevel;
import com.alexander.springboot.r_siiau_backend.enums.MyUserStatus;
import com.alexander.springboot.r_siiau_backend.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserM(@NotBlank
                          String name,

                          @NotBlank
                          String lastname,

                          @NotNull
                          AcademicLevel academicLevel,

                          @NotBlank
                          String career,

                          @NotNull
                          Integer degree,

                          @NotNull
                          UserRole role,

                          @NotNull
                          MyUserStatus status) {
}
