package com.ewp.crm.service.impl;

import com.ewp.crm.models.*;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.dto.all_students_page.StudentDto;
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
    private final ProjectPropertiesService projectPropertiesService;

    private static Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,
                              ProjectPropertiesService projectPropertiesService) {
        this.studentRepository = studentRepository;
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
        return studentRepository.getStudentsWithoutSocialProfileByType(excludeSocialProfiles);
    }

    @Override
    public List<Student> getStudentsByStatusId(Long id) {
        return studentRepository.getStudentsByStatusId(id);
    }

    @Override
    public List<Student> getStudentsWithTodayNotificationsEnabled() {
        return studentRepository.getStudentsWithTodayNotificationsEnabled();
    }

    @Override
    public List<Student> getStudentsWithTodayTrialNotificationsEnabled() {
        return studentRepository.getStudentsWithTodayTrialNotificationsEnabled();
    }

    @Override
    public void detach(Student student) {
        studentRepository.detach(student);
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
        studentRepository.resetColors();
    }

    @Override
    public Student getStudentByEmail(String email) {
        return studentRepository.getStudentByEmail(email);
    }

    @Override
    public List<StudentDto> getStudentDtoForAllStudentsPage() {
        return studentRepository.getStudentDtoForAllStudentsPage();
    }

    @Override
    public void updateStudentEducationStage(StudentEducationStage studentEducationStage, Student student) {
        studentRepository.updateStudentEducationStage(studentEducationStage, student);
    }

    @Override
    public void updateCourse(Course course, Student student) {
        studentRepository.updateCourse(course, student);
    }

}
