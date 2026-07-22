package com.example.university.service;

import com.example.university.dto.StudentDto;
import com.example.university.mapper.StudentMapper;
import com.example.university.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StudentService {

    private final StudentRepository repository;
    private final StudentMapper mapper;

    public StudentService(StudentRepository repository,
                          StudentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public StudentDto getStudent(Long id) {
        return mapper
                .mapStudentToStudentDto(
                        repository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Student not found"))
                );
    }

    public StudentDto save(StudentDto studentDto) {
        try {
            return mapper.mapStudentToStudentDto(repository.save(mapper.mapStudentDTOtoStudent(studentDto)));
        } catch (DataIntegrityViolationException e) {
            Exception cause = (Exception) e.getCause();

            while (cause.getCause() != null) {
                cause = (Exception) cause.getCause();
            }

            //message = cause.getMessage().split(";")[0];
            String causeMessage = cause.getMessage();
            String message = causeMessage.substring(0, causeMessage.indexOf(';'));

            log.error("Failed to save student: {}", message);
            throw new DataIntegrityViolationException("Student data violates integrity constraints: " + message);
        }
    }

    public List<StudentDto> getAllStudents() {
        return repository.findAll().stream()
                .map(mapper::mapStudentToStudentDto)
                .toList();
    }
}
