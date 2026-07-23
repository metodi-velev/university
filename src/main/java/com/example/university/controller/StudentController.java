package com.example.university.controller;

import com.example.university.dto.StudentDto;
import com.example.university.service.StudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Validated
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable @Positive(message = "Student id must be greater than zero.") Long id) {
        return ResponseEntity.ok().body(studentService.getStudent(id));
    }

    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        return ResponseEntity.ok().body(studentService.getAllStudents());
    }

    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@RequestBody @Valid StudentDto studentDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.save(studentDto));
    }

    @GetMapping("/search/findStudentByEmail")
    public ResponseEntity<List<StudentDto>> getStudentByEmail(
            @RequestParam @Email(message = "Email address must be well-formed") String email
    ) {
        return studentService.findStudentByEmail(email)
                .map(student -> ResponseEntity.ok(List.of(student)))
                .orElseGet(() -> ResponseEntity.ok(Collections.emptyList()));
    }
}
