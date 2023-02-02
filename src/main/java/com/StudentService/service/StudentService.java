package com.StudentService.service;

import com.StudentService.model.Student;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StudentService {

    List<Student> getAllStudents();

    Student getStudentById(Integer studentId);

    Student addStudent(Student student);

    Student updateStudent(Integer studentId, Student student);

    void deleteStudent(Integer studentId);

}
