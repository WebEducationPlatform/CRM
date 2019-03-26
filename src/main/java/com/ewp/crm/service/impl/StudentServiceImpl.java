package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Student;
import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.repository.interfaces.StudentRepository;
import com.ewp.crm.repository.interfaces.StudentRepositoryCustom;
import com.ewp.crm.repository.interfaces.StudentStatusRepository;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentServiceImpl extends CommonServiceImpl<Student> implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentStatusRepository studentStatusRepository;
    private final StudentRepositoryCustom studentRepositoryCustom;
    private final ProjectPropertiesService projectPropertiesService;

    private static Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,
                              StudentStatusRepository studentStatusRepository,
                              StudentRepositoryCustom studentRepositoryCustom,
                              ProjectPropertiesService projectPropertiesService) {
        this.studentRepository = studentRepository;
        this.studentStatusRepository = studentStatusRepository;
        this.studentRepositoryCustom = studentRepositoryCustom;
        this.projectPropertiesService = projectPropertiesService;
    }

    @Override
    public Student addStudentForClient(Client client) {

        Student result;

        if (client.getStudent() == null && client.getStatus().isCreateStudent()) {

            StudentStatus status = projectPropertiesService.getOrCreate().getDefaultStudentStatus();
            if (status == null) {
                logger.error("Default student status not set!");
                return null;
                // status = studentStatusRepository.save(new StudentStatus("Новый студент"));
            }

            int trialOffset = client.getStatus().getTrialOffset();


            Student newStudent = new Student(
                    client,
                    calculateTrialPeriodEnd(trialOffset),

                    projectPropertiesService.getOrCreate().getDefaultPricePerMonth(),
                    projectPropertiesService.getOrCreate().getDefaultPayment(),
                    new BigDecimal(0.00),
                    status,
                    "");
            result = studentRepository.save(newStudent);
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

    @Override
    public Student getStudentByClientId(Long clientId) {
        return studentRepository.getStudentByClientId(clientId);
    }

    @Override
    public void save(Student student) {
        studentRepository.save(student);
    }

    @Override
    public void resetColors() {
        studentRepositoryCustom.resetColors();
    }

    private LocalDateTime calculateTrialPeriodEnd(int trialOffset) {
        return LocalDateTime.now().plusDays(trialOffset);
    }

}