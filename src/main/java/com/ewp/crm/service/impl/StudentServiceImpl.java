package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Student;
import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.repository.interfaces.StudentRepository;
import com.ewp.crm.repository.interfaces.StudentRepositoryCustom;
import com.ewp.crm.repository.interfaces.StudentStatusRepository;
import com.ewp.crm.service.interfaces.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentServiceImpl extends CommonServiceImpl<Student> implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentStatusRepository studentStatusRepository;
    private final StudentRepositoryCustom studentRepositoryCustom;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, StudentStatusRepository studentStatusRepository, StudentRepositoryCustom studentRepositoryCustom) {
        this.studentRepository = studentRepository;
        this.studentStatusRepository = studentStatusRepository;
        this.studentRepositoryCustom = studentRepositoryCustom;
    }

    @Value("${price.month}")
    private String PRICE;

    @Value("${default.student.status}")
    private String DEFAULT_STATUS;

    @Override
    public Student addStudentForClient(Client client) {
        Student result;
        if (client.getStudent() == null && client.getStatus().isCreateStudent()) {
            StudentStatus status = studentStatusRepository.getStudentStatusByStatus(DEFAULT_STATUS);
            if (status == null) {
                status = studentStatusRepository.save(new StudentStatus(DEFAULT_STATUS));
            }
            int trialOffset = client.getStatus().getTrialOffset();
            int nextPaymentOffset = client.getStatus().getNextPaymentOffset();
            result = new Student(client, LocalDateTime.now().plusDays(trialOffset), LocalDateTime.now().plusDays(nextPaymentOffset), new BigDecimal(0.00), new BigDecimal(0.00), new BigDecimal(0.00), status, "");
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

    @Override
    public List<Student> getStudentsWithTodayNotificationsEnabled() {
        return studentRepositoryCustom.getStudentsWithTodayNotificationsEnabled();
    }

    @Override
    public void detach(Student student) {
        studentRepositoryCustom.detach(student);
    }
}
