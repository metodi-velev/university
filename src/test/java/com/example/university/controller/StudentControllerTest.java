package com.example.university.controller;

import com.example.university.dto.StudentDto;
import com.example.university.exception.StudentNotFoundException;
import com.example.university.service.StudentService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
                .email("bob@email.com")
                .build();

        when(studentService.getStudent(1L)).thenReturn(mockStudent);

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@email.com"));
    }

    @Test
    void shouldReturnAllStudents() throws Exception {
        List<StudentDto> students = List.of(
                StudentDto.builder().name("Alice").email("alice@email.com").build(),
                StudentDto.builder().name("Bob").email("bob@email.com").build()
        );

        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].email").value("alice@email.com"))
                .andExpect(jsonPath("$[1].name").value("Bob"))
                .andExpect(jsonPath("$[1].email").value("bob@email.com"));
    }

    @Test
    void shouldCreateStudent() throws Exception {
        StudentDto createdStudent = StudentDto.builder().name("Charlie").email("charlie@email.com").build();
        String studentJson = objectMapper.writeValueAsString(createdStudent);

        when(studentService.save(any(StudentDto.class))).thenReturn(createdStudent);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Charlie"))
                .andExpect(jsonPath("$.email").value("charlie@email.com"));
    }

    @Test
    void shouldReturn400WhenIdIsNull() throws Exception {
        StudentDto studentDto = StudentDto.builder().name(null).email("dummy@email.com").build();
        String studentJson = objectMapper.writeValueAsString(studentDto);

/*        when(studentService.save(any(StudentDto.class))).thenThrow(
                new DataIntegrityViolationException("Student data violates integrity constraints: NULL not allowed for column \"NAME\"")
        );*/

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(
                                MethodArgumentNotValidException.class,
                                result.getResolvedException()))
                .andExpect(jsonPath("$.name").value("Name is required and cannot be empty"))
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    void shouldReturn404WhenStudentNotFound() throws Exception {
        when(studentService.getStudent(999L)).thenThrow(
                new StudentNotFoundException("Student", "id", String.valueOf(999L)));

        mockMvc.perform(get("/students/999"))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertInstanceOf(
                                StudentNotFoundException.class,
                                result.getResolvedException()))
                .andExpect(jsonPath("$.message")
                        .value("Student not found with the given input data id: '999'"));
    }

    @Test
    void shouldReturn400WhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/students/invalid-id"))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(
                                MethodArgumentTypeMismatchException.class,
                                result.getResolvedException()))
                .andExpect(jsonPath("$.message")
                        .value("Method parameter 'id': Failed to convert value of type 'java.lang.String' to " +
                                "required type 'java.lang.Long'; For input string: \"invalid-id\""));
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        StudentDto studentDto = StudentDto.builder().name("Charlie").email("charlie").build();
        String studentJson = objectMapper.writeValueAsString(studentDto);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(
                                MethodArgumentNotValidException.class,
                                result.getResolvedException()))
                .andExpect(jsonPath("$.email")
                        .value("Email must be valid"));
    }

    @Test
    void shouldReturn400WhenIdIsNotPositive() throws Exception {
        mockMvc.perform(get("/students/0"))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(
                                ConstraintViolationException.class,
                                result.getResolvedException()))
                .andExpect(jsonPath("['getStudent.id']")
                        .value("Student id must be greater than zero."));
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
        String studentJson = "{\"name\":\"Charlie\", \"email\":\"charlie@email.com\",\"extra\":\"field\"}";
        StudentDto createdStudent = StudentDto.builder().name("Charlie").email("charlie@email.com").build();
        when(studentService.save(any(StudentDto.class))).thenReturn(createdStudent);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Charlie"));
    }
}