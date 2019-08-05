package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.Student;
import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.repository.interfaces.StudentRepository;
import com.ewp.crm.repository.interfaces.StudentRepositoryCustom;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl extends CommonServiceImpl<Student> implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentRepositoryCustom studentRepositoryCustom;
    private final ProjectPropertiesService projectPropertiesService;

    private static Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,
                              StudentRepositoryCustom studentRepositoryCustom,
                              ProjectPropertiesService projectPropertiesService) {
        this.studentRepository = studentRepository;
        this.studentRepositoryCustom = studentRepositoryCustom;
        this.projectPropertiesService = projectPropertiesService;
    }

    @Override
    public Optional<Student> addStudentForClient(Client client) {
        Student result;
        if (client.getStudent() == null && client.getStatus().isCreateStudent()) {
            StudentStatus status = projectPropertiesService.getOrCreate().getDefaultStudentStatus();
            if (status == null) {
                logger.error("Default student status not set!");
                return Optional.empty();
//                status = studentStatusRepository.save(new StudentStatus("Новый студент"));
            }
            int trialOffset = client.getStatus().getTrialOffset();
            int nextPaymentOffset = client.getStatus().getNextPaymentOffset();
            result = new Student(client,
                    LocalDateTime.now().plusDays(trialOffset),
                    LocalDateTime.now().plusDays(nextPaymentOffset),
                    projectPropertiesService.getOrCreate().getDefaultPricePerMonth(),
                    projectPropertiesService.getOrCreate().getDefaultPayment(),
                    new BigDecimal(0.00),
                    status,
                    "");
            result = studentRepository.save(result);
        } else {
            result = client.getStudent();
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Student> getStudentsWithoutSocialProfileByType(List<SocialNetworkType> excludeSocialProfiles) {
        return studentRepositoryCustom.getStudentsWithoutSocialProfileByType(excludeSocialProfiles);
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
    public long countActiveByDateAndStatuses(ZonedDateTime day, List<Long> studentStatuses) {
        return studentRepositoryCustom.countActiveByDateAndStatuses(day, studentStatuses);
    }

    @Override
    public void detach(Student student) {
        studentRepositoryCustom.detach(student);
    }

    @Override
    public Optional<Student> getStudentByClientId(Long clientId) {
        return Optional.ofNullable(studentRepository.getStudentByClientId(clientId));
    }

    @Override
    public void save(Student student) {
        studentRepository.save(student);
    }

    @Override
    public void resetColors() {
        studentRepositoryCustom.resetColors();
    }

    @Override
    public Student getStudentByEmail(String email) {
        return studentRepository.getStudentByEmail(email);
    }

}
