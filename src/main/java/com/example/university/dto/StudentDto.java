package com.example.university.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record StudentDto(
        @NotBlank(message = "Name is required and cannot be empty")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Email is required and cannot be empty")
        @Email(message = "Email must be valid")
        @Size(max = 30, message = "Email must not exceed 30 characters")
        String email
) {}
