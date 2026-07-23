package com.example.university.repository;

import com.example.university.exception.StudentNotFoundException;
import com.example.university.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void shouldPersistStudent() {

        Student saved = studentRepository.save(Student.builder().name("John").email("john@email.com").build());

        assertThat(saved.getId()).isNotNull();

        Student loaded = studentRepository.findById(saved.getId())
                .orElseThrow(() -> new StudentNotFoundException("Student", "id", String.valueOf(saved.getId())));

        assertThat(loaded.getName()).isEqualTo("John");
        assertThat(loaded.getEmail()).isEqualTo("john@email.com");
    }
}