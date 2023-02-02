package com.StudentService.model;

import lombok.*;

import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Institute implements Serializable {

    private Integer instituteId;
    private String studentId;
    private String instituteName;
    private String instituteAddress;
    private String instituteContact;

}
