package com.example.university.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String student, String email, String emailValue) {
        super(String.format("Student '%s' with the %s '%s' already exists. Conflict, please, enter a different email.",
                student,
                email,
                emailValue
        ));
    }
}
