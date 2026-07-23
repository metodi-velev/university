package com.example.university.service;

import com.example.university.dto.StudentDto;
import com.example.university.exception.StudentNotFoundException;
import com.example.university.mapper.StudentMapper;
import com.example.university.model.Student;
import com.example.university.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceUnitTest {

    @Mock
    private StudentRepository repository;

    @Mock
    private StudentMapper mapper;

    @InjectMocks
    private StudentService service;

    @Test
    void shouldReturnStudentWhenExists() {
        // Arrange
        Student expected = new Student(1L, "John");
        StudentDto expectedDto = StudentDto.builder().name("John").build();

        when(repository.findById(1L))
                .thenReturn(Optional.of(expected));

        when(mapper.mapStudentToStudentDto(expected))
                .thenReturn(expectedDto);

        // Act
        StudentDto actual = service.getStudent(1L);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(expected.getName()).isEqualTo("John");

        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldThrowExceptionWhenStudentDoesNotExist() {
        // Arrange
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.getStudent(99L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessage("Student not found with the given input data id: '99'");

        verify(repository).findById(99L);
    }
}