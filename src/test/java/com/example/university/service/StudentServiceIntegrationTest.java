package com.example.university.service;

import com.example.university.dto.StudentDto;
import com.example.university.mapper.StudentMapper;
import com.example.university.model.Student;
import com.example.university.repository.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class StudentServiceIntegrationTest {

    private StudentDto actualFirstStudentDto;
    private Student actualStudent;

    @Autowired
    private StudentRepository repository;

    @Autowired
    private StudentMapper mapper;

    @Autowired
    private StudentService service;
    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        Student student = Student.builder().name("John").build();
        actualStudent = repository.save(student);
    }

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnStudentWhenExists() {

        // Act
        actualFirstStudentDto = service.getStudent(actualStudent.getId());

        // Assert
        assertThat(actualFirstStudentDto).isNotNull();
        assertThat(actualFirstStudentDto.getName()).isEqualTo(actualStudent.getName()).isEqualTo("John");
    }

    @Test
    void shouldThrowExceptionWhenStudentDoesNotExist() {
        // Act & Assert
        assertThatThrownBy(() -> service.getStudent(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Student not found");

        assertThat(repository.findById(actualStudent.getId())).isPresent();
    }

    @Test
    void shouldThrowExceptionWhenStudentNameIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> service.save(StudentDto.builder().name(null).build()))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Student data violates integrity constraints:")
                .hasMessageContaining("NULL not allowed for column \"NAME\"");

        assertThat(repository.findById(actualStudent.getId())).isPresent();
    }

    @Test
    void shouldSaveStudent() {
        StudentDto secondStudentDto = StudentDto.builder().name("Metodi").build();

        // Act
        StudentDto actualSecondStudentDto = service.save(secondStudentDto);

        actualFirstStudentDto = service.getStudent(actualStudent.getId());

        // Assert
        assertThat(actualSecondStudentDto).isNotNull();
        assertThat(actualSecondStudentDto.getName()).isEqualTo(secondStudentDto.getName()).isEqualTo("Metodi");
        assertThat(repository.findById(actualStudent.getId())).isPresent();
        assertThat(repository.findAll()).hasSize(2);
        assertThat(service.getAllStudents()).hasSize(2);

        assertThat(service.getAllStudents()).hasSize(2).contains(actualSecondStudentDto).contains(actualFirstStudentDto);
    }
}