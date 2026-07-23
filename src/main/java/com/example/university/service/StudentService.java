package com.example.university.service;

import com.example.university.dto.StudentDto;
import com.example.university.exception.EmailAlreadyExistsException;
import com.example.university.exception.StudentNotFoundException;
import com.example.university.mapper.StudentMapper;
import com.example.university.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@CacheConfig(cacheNames = "students")
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentService(StudentRepository studentRepository,
                          StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    public StudentDto getStudent(Long id) {
        return studentMapper
                .mapStudentToStudentDto(
                        studentRepository.findById(id)
                                .orElseThrow(() -> new StudentNotFoundException("Student", "id", String.valueOf(id)))
                );
    }

    @CacheEvict(allEntries = true)
    public StudentDto save(StudentDto studentDto) {
        try {
            validateEmailUniqueness(studentDto.email());
            return studentMapper.mapStudentToStudentDto(studentRepository.save(studentMapper.mapStudentDTOtoStudent(studentDto)));
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

    private void validateEmailUniqueness(String email) {
        studentRepository.findByEmail(email)
                .ifPresentOrElse(
                        existingStudent -> {
                            throw new EmailAlreadyExistsException(
                                    existingStudent.getName(),
                                    "email",
                                    email
                            );
                        },
                        () -> log.info("The email {} is free to use.", email)
                );
    }

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(studentMapper::mapStudentToStudentDto)
                .toList();
    }

    @Cacheable(key = "#email")
    public Optional<StudentDto> findStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
                .map(studentMapper::mapStudentToStudentDto)
                .or(Optional::empty);
    }
}
