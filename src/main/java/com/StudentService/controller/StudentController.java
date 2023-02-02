package com.StudentService.controller;

import com.StudentService.model.Student;
import com.StudentService.service.StudentService;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
//@CrossOrigin("http://localhost:3000")
//@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private MemcachedClient memcachedClient;

    private static final int CACHE_TIME = 60; // cache for 60 seconds

    //GetAllStudents
    @GetMapping("/all")
    public List<Student> getAllStudents() {
        String key = "all_students";
        List<Student> students = (List<Student>) memcachedClient.get(key);
        System.out.println("students get all students data from memcached " + students);
        if (students != null) {
            return students;
        }
        students = studentService.getAllStudents();
        System.out.println("students get all students data from db " + students);
        memcachedClient.set(key, CACHE_TIME, students);
        return students;
    }

    //GetSingleStudent
    @GetMapping("/{studentId}")
    public Student getStudentById(@PathVariable Integer studentId) {
        String key = "student_" + studentId;
        Student student = (Student) memcachedClient.get(key);
        System.out.println("students get single student data from memcached " + student);
        if (student != null) {
            return student;
        }
        student = studentService.getStudentById(studentId);
        System.out.println("students get single student data from db " + student);
        if (student != null) {
            memcachedClient.set(key, CACHE_TIME, student);
        }
        return student;
    }

    //AddStudents New Record and Save in DB
    @PostMapping("/add")
    public Student addStudent(@RequestBody Student student) {
        student = studentService.addStudent(student);
        String key = "student_" + student.getStudentId();
        //cache contains the old data with the same key, the delete method is used to remove that old data
        memcachedClient.delete(key);
        return student;
    }

    //UpdateStudent
    @PutMapping("/{studentId}")
    public Student updateStudent(@PathVariable Integer studentId, @RequestBody Student student) {
        student = studentService.updateStudent(studentId, student);
        System.out.println("students get from updated data from db " + student);
        String key = "student_" + studentId;
        System.out.println("students delete from old data and get new updated data from db " + key);
        memcachedClient.delete(key); //delete method is used to remove that old data and update new data
        return student;
    }

    //DeleteStudent
    @DeleteMapping("/{studentId}")
    public void deleteStudent(@PathVariable Integer studentId) {
        studentService.deleteStudent(studentId);
        String key = "student_" + studentId;
        System.out.println("students delete from db " + key);
        memcachedClient.delete(key);
    }

}

