package com.example.university.controller;

import com.example.university.dto.StudentDto;
import com.example.university.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    @Test
    void shouldReturnUserAsJson() throws Exception {
        StudentDto mockStudent = StudentDto.builder()
                .name("Bob")
                .build();

        when(studentService.getStudent(1L)).thenReturn(mockStudent);

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"));
    }

    @Test
    void shouldReturnAllStudents() throws Exception {
        List<StudentDto> students = List.of(
                StudentDto.builder().name("Alice").build(),
                StudentDto.builder().name("Bob").build()
        );

        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"));
    }

    @Test
    void shouldCreateStudent() throws Exception {
        StudentDto createdStudent = StudentDto.builder().name("Charlie").build();
        String studentJson = objectMapper.writeValueAsString(createdStudent);

        when(studentService.save(any(StudentDto.class))).thenReturn(createdStudent);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Charlie"));
    }

    @Test
    void shouldReturn400WhenIdIsNull() throws Exception {
        StudentDto studentDto = StudentDto.builder().name(null).build();
        String studentJson = objectMapper.writeValueAsString(studentDto);

        when(studentService.save(any(StudentDto.class))).thenThrow(
                new DataIntegrityViolationException("Student data violates integrity constraints: NULL not allowed for column \"NAME\"")
        );

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Student data violates integrity constraints: NULL not allowed for column \"NAME\""))
                .andExpect(jsonPath("$.name").doesNotExist());
    }

    @Test
    void shouldReturn500WhenStudentNotFound() throws Exception {
        when(studentService.getStudent(999L)).thenThrow(new IllegalArgumentException("Student not found"));

        mockMvc.perform(get("/students/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturn400WhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/students/invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyListWhenNoStudents() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of());

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturn201WhenPostHasExtraFields() throws Exception {
        String studentJson = "{\"name\":\"Charlie\", \"extra\":\"field\"}";
        StudentDto createdStudent = StudentDto.builder().name("Charlie").build();
        when(studentService.save(any(StudentDto.class))).thenReturn(createdStudent);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Charlie"));
    }
}