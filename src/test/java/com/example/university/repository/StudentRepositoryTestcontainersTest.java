package com.example.university.repository;

import com.example.university.exception.StudentNotFoundException;
import com.example.university.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class StudentRepositoryTestcontainersTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16");

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