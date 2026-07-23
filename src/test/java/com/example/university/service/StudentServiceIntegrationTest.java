package com.example.university.service;

import com.example.university.dto.StudentDto;
import com.example.university.exception.EmailAlreadyExistsException;
import com.example.university.exception.StudentNotFoundException;
import com.example.university.mapper.StudentMapper;
import com.example.university.model.Student;
import com.example.university.repository.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

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
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        Student student = Student.builder().name("John").email("john@email.com").build();
        actualStudent = repository.save(student);
    }

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnStudentWhenExists() {

        actualFirstStudentDto = studentService.getStudent(actualStudent.getId());

        assertThat(actualFirstStudentDto).isNotNull();
        assertThat(actualFirstStudentDto.name()).isEqualTo(actualStudent.getName()).isEqualTo("John");
        assertThat(actualFirstStudentDto.email()).isEqualTo(actualStudent.getEmail()).isEqualTo("john@email.com");
    }

    @Test
    void shouldThrowExceptionWhenStudentDoesNotExist() {
        assertThatThrownBy(() -> studentService.getStudent(99L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessage("Student not found with the given input data id: '99'");

        assertThat(repository.findById(actualStudent.getId())).isPresent();
    }

    @Test
    void shouldThrowExceptionWhenStudentNameIsNull() {
        assertThatThrownBy(() -> studentService.save(StudentDto.builder().name(null).email("dummy@email.com").build()))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Student data violates integrity constraints:")
                .hasMessageContaining("NULL not allowed for column \"NAME\"");

        assertThat(repository.findById(actualStudent.getId())).isPresent();
    }

    @Test
    void shouldSaveStudent() {
        StudentDto secondStudentDto = StudentDto.builder().name("Jane").email("jane@email.com").build();

        StudentDto actualSecondStudentDto = studentService.save(secondStudentDto);

        actualFirstStudentDto = studentService.getStudent(actualStudent.getId());

        assertThat(actualSecondStudentDto).isNotNull();
        assertThat(actualSecondStudentDto.name()).isEqualTo(secondStudentDto.name()).isEqualTo("Jane");
        assertThat(actualSecondStudentDto.email()).isEqualTo(secondStudentDto.email()).isEqualTo("jane@email.com");
        assertThat(repository.findById(actualStudent.getId())).isPresent();
        assertThat(repository.findAll()).hasSize(2);
        assertThat(studentService.getAllStudents()).hasSize(2);

        assertThat(studentService.getAllStudents()).hasSize(2).contains(actualSecondStudentDto).contains(actualFirstStudentDto);
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        StudentDto studentDto1 = StudentDto.builder().name("Jane Doe").email("jane@email.com").build();
        studentService.save(studentDto1);

        StudentDto studentDto2 = StudentDto.builder().name("Jane Kane").email("jane@email.com").build();

        assertThatThrownBy(() -> studentService.save(studentDto2))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("jane@email.com");
    }

    @Test
    void shouldCacheStudentByEmail() {
        // First call - should hit database
        Optional<StudentDto> firstCall = studentService.findStudentByEmail("john@email.com");

        // Second call - should hit cache
        Optional<StudentDto> secondCall = studentService.findStudentByEmail("john@email.com");

        // Verify cache stats
        Cache cache = cacheManager.getCache("students");

        assertThat(cache).isNotNull();
        assertThat(cache.get("john@email.com"))
                .isNotNull()
                .matches(wrapper -> wrapper.get() instanceof StudentDto)
                .extracting(Cache.ValueWrapper::get)
                .isInstanceOf(StudentDto.class);
    }

    @Test
    void shouldEvictCacheOnSave() {
        // First call to populate cache
        studentService.findStudentByEmail("john@email.com");

        // Save new student - should evict all cache
        StudentDto newStudent = new StudentDto("Jane Doe", "jane@email.com");
        studentService.save(newStudent);

        // Cache should be empty for existing entries
        Cache cache = cacheManager.getCache("students");

        assertThat(cache).isNotNull();
        assertThat(cache.get("john@email.com")).isNull();
    }
}