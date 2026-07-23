package com.example.university.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class StudentDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeStudentDto() {
        StudentDto studentDto = StudentDto.builder().name("John").email("john@email.com").build();
        String studentDtoJson = objectMapper.writeValueAsString(studentDto);
        assertThat(studentDtoJson).contains("\"name\":\"John\"").contains("\"email\":\"john@email.com\"");
    }
}