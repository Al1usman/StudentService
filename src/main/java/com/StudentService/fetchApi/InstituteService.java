package com.StudentService.fetchApi;

import com.StudentService.model.Institute;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "INSTITUTE-SERVICE")
public interface InstituteService {

    @GetMapping("/institutes/{studentId}")
    List<Institute> getInstitutesByStudentId(@PathVariable("studentId") Integer studentId);

}