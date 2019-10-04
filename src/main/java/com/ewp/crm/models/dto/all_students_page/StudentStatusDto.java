package com.ewp.crm.models.dto.all_students_page;

import com.ewp.crm.models.Status;
import com.ewp.crm.models.StudentStatus;

public class StudentStatusDto {
    private long id;
    private String status;

    public StudentStatusDto() {
    }

    public StudentStatusDto(long id, String status) {
        this.id = id;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static StudentStatusDto getStudentStatusDto(StudentStatus studentStatus) {
        StudentStatusDto studentStatusDto = new StudentStatusDto();

        studentStatusDto.id = studentStatus.getId();
        studentStatusDto.status = studentStatus.getStatus();

        return studentStatusDto;
    }
}
