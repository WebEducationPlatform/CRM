package com.ewp.crm.service.impl;

import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.repository.interfaces.StudentStatusRepository;
import com.ewp.crm.service.interfaces.StudentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentStatusServiceImpl extends CommonServiceImpl<StudentStatus> implements StudentStatusService {
    private final StudentStatusRepository studentStatusRepository;

    @Autowired
    public StudentStatusServiceImpl(StudentStatusRepository studentStatusRepository) {
        this.studentStatusRepository = studentStatusRepository;
    }

    @Override
    public StudentStatus getByName(String status) {
        return studentStatusRepository.getStudentStatusByStatus(status);
    }

    @Override
    public void save(StudentStatus studentStatus) {
        studentStatusRepository.save(studentStatus);
    }
}
