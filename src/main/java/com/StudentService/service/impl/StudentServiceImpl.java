package com.StudentService.service.impl;

import com.StudentService.fetchApi.InstituteService;
import com.StudentService.model.Institute;
import com.StudentService.model.Student;
import com.StudentService.repo.StudentRepo;
import com.StudentService.service.StudentService;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private InstituteService instituteService;

    @Autowired
    private MemcachedClient memcachedClient;

    //GetAllStudents
    @Override
    @Cacheable(value = "students", key = "#studentId")
    public List<Student> getAllStudents() {
        List<Student> students = studentRepo.findAll();
        for (Student student : students) {
            Integer studentId = student.getStudentId();
            List<Institute> institutes = instituteService.getInstitutesByStudentId(studentId);
            student.setInstitutes(institutes);
        }
        return students;
    }

    //GetSingleStudent
    @Override
    public Student getStudentById(Integer studentId) {
        // Check if student is in cache
        Student student = (Student) memcachedClient.get(String.valueOf(studentId));
        if (student == null) {
            student = studentRepo.findById(studentId).orElse(null);
            if (student != null) {
                List<Institute> institutes = instituteService.getInstitutesByStudentId(studentId);
                student.setInstitutes(institutes);
                // Put student in cache
                memcachedClient.set(String.valueOf(studentId), 0, student);
            }
        }
        return student;
    }

    //AddStudents New Record and Save in DB
    @Override
    public Student addStudent(Student student) {
        studentRepo.save(student);
        // Remove student from cache
        memcachedClient.delete(String.valueOf(student.getStudentId()));
        return student;
    }

    //UpdateStudent
    @Override
    public Student updateStudent(Integer studentId, Student student) {
        Student existingStudent = studentRepo.findById(studentId).orElse(null);
        if (existingStudent == null) {
            return null;
        }
        existingStudent.setStudentName(student.getStudentName());
        existingStudent.setStudentAddress(student.getStudentAddress());
        existingStudent.setStudentUniversity(student.getStudentUniversity());
        Student updatedStudent = studentRepo.save(existingStudent);
         //Remove student from cache
        memcachedClient.delete(String.valueOf(studentId));
        return updatedStudent;
    }

    //DeleteStudent
    @Override
    public void deleteStudent(Integer studentId) {
        studentRepo.deleteById(studentId);

        //Remove the student from cache
        memcachedClient.delete(String.valueOf(studentId));
    }

}