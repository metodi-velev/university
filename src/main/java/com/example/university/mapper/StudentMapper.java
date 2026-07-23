package com.example.university.mapper;

import com.example.university.dto.StudentDto;
import com.example.university.model.Student;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Builder
@NoArgsConstructor
public class StudentMapper {
    public Student mapStudentDTOtoStudent(StudentDto studentDto) {
        return Student.builder().name(studentDto.name()).email(studentDto.email()).build();
    }

    public StudentDto mapStudentToStudentDto(Student student) {
        return StudentDto.builder().name(student.getName()).email(student.getEmail()).build();
    }
}
