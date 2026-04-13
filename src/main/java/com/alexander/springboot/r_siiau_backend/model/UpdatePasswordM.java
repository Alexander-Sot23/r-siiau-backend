package com.alexander.springboot.r_siiau_backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordM(@NotBlank
                              String oldPassword,

                              @NotBlank
                              @Size(min = 6)
                              String newPassword) {
}
