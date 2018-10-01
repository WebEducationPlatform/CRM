package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Student;
import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.repository.interfaces.StudentRepository;
import com.ewp.crm.repository.interfaces.StudentStatusRepository;
import com.ewp.crm.service.interfaces.StudentService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StudentServiceImpl extends CommonServiceImpl<Student> implements StudentService {

    private StudentRepository studentRepository;
    private StudentStatusRepository studentStatusRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, StudentStatusRepository studentStatusRepository) {
        this.studentRepository = studentRepository;
        this.studentStatusRepository = studentStatusRepository;
    }

    @Value("${price.month}")
    private String PRICE;

    @Value("${default.student.status}")
    private String DEFAULT_STATUS;

    @Value("${status.name.trial}")
    private String trialStatusName;

    @Value("${status.name.learn}")
    private String learnStatusName;

    @Override
    public Student addStudentForClient(Client client) {
        Student result;
        if (client.getStudent() == null && client.getStatus().isCreateStudent()) {
            StudentStatus status = studentStatusRepository.getStudentStatusByStatus(DEFAULT_STATUS);
            if (status == null) {
                status = studentStatusRepository.save(new StudentStatus(DEFAULT_STATUS));
            }
            DateTime currentDate = new DateTime();
            if(client.getStatus().getName().equals("trialLearnStatus")) {
                result = new Student(client, currentDate.plusDays(3), currentDate.plusDays(3), new BigDecimal(PRICE), new BigDecimal(PRICE), new BigDecimal(0.00), status, "");
            } else if (client.getStatus().getName().equals("inLearningStatus")) {
                result = new Student(client, currentDate, currentDate.plusDays(30), new BigDecimal(PRICE), new BigDecimal(PRICE), new BigDecimal(0.00), status, "");
            } else {
                result = new Student(client, currentDate, currentDate, new BigDecimal(0.00), new BigDecimal(0.00), new BigDecimal(0.00), status, "");
            }
            result = studentRepository.save(result);
        } else {
            result = client.getStudent();
        }
        return result;
    }

    @Override
    public List<Student> getStudentsByStatusId(Long id) {
        return studentRepository.getStudentsByStatusId(id);
    }
}
