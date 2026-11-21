package ru.goalissimo.backend.users.infrastructure.dto.web;

import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
