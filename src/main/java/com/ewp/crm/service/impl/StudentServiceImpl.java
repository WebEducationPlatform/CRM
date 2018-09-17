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
    private Long PRICE;

    @Override
    public Student addStudentForClient(Client client) {
        Student result;
        if (client.getStudent() == null) {
            StudentStatus status = studentStatusRepository.save(new StudentStatus("Auto generated " + client.getStatus().getName()));
            DateTime currentDate = new DateTime();
            if(client.getStatus().getName().equals("trialLearnStatus")) {
                result = new Student(client, currentDate.plusDays(3).toDate(), currentDate.plusDays(3).toDate(), PRICE, PRICE, 0L, status, "");
            } else if (client.getStatus().getName().equals("inLearningStatus")) {
                result = new Student(client, currentDate.toDate(), currentDate.plusDays(30).toDate(), PRICE, PRICE, 0L, status, "");
            } else {
                result = new Student(client, currentDate.toDate(), currentDate.toDate(), 0L, 0L, 0L, status, "");
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
