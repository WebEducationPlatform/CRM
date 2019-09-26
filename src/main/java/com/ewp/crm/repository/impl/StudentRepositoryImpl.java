package com.ewp.crm.repository.impl;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.Student;
import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.models.dto.StatusDto;
import com.ewp.crm.models.dto.all_students_page.ClientDtoForAllStudentsPage;
import com.ewp.crm.models.dto.all_students_page.SocialNetworkDto;
import com.ewp.crm.models.dto.all_students_page.StudentDto;
import com.ewp.crm.models.dto.all_students_page.StudentStatusDto;
import com.ewp.crm.repository.interfaces.StudentRepositoryCustom;
import org.apache.poi.poifs.crypt.dsig.services.TimeStampService;
import org.apache.poi.poifs.crypt.dsig.services.TimeStampServiceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentRepositoryImpl implements StudentRepositoryCustom {

    private static Logger logger = LoggerFactory.getLogger(StudentRepositoryImpl.class);

    private final EntityManager entityManager;

    @Autowired
    public StudentRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Student> getStudentsWithoutSocialProfileByType(List<SocialNetworkType> excludeSocialProfiles) {
        return entityManager.createQuery("SELECT s FROM Student s JOIN s.client AS c JOIN c.socialProfiles AS sp  WHERE sp.socialNetworkType NOT IN :excludes")
                .setParameter("excludes", excludeSocialProfiles)
                .getResultList();
    }

    @Override
    public List<Student> getStudentsWithTodayNotificationsEnabled() {
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        return entityManager.createQuery("SELECT s FROM Student s WHERE (((s.notifyEmail = TRUE)" +
                " OR (s.notifySMS = TRUE) OR (s.notifyVK = TRUE) OR (s.notifySlack = TRUE))" +
                " AND (s.nextPaymentDate >= :today AND s.nextPaymentDate < :tomorrow))")
                .setParameter("today", today)
                .setParameter("tomorrow", tomorrow)
                .getResultList();
    }

    @Override
    public List<Student> getStudentsWithTodayTrialNotificationsEnabled() {
        // Получаем всех студентов, у которых статус клиента = 3 (На пробных), включен любой способ нотификации
        // и дата удовлетворяет заданному условию
        return entityManager.createNativeQuery(
                "SELECT * FROM student s WHERE (((s.notify_email = '1') OR (s.notify_sms = '1') OR " +
                        "(s.notify_vk = '1') OR (s.notify_slack = '1')) AND " +
                        "(s.end_trial >= CURDATE() AND s.end_trial < CURDATE() + INTERVAL 1 DAY)) AND " +
                        "s.client_id IN (SELECT sc.user_id FROM status_clients sc WHERE sc.status_id = '3');", Student.class)
                .getResultList();
    }

    @Override
    public void detach(Student student) {
        entityManager.detach(student);
    }

    @Override
    @Transactional
    public void resetColors() {
        entityManager.createQuery("UPDATE Student s SET s.color = null WHERE s.color IS NOT null").executeUpdate();
    }

    @Override
    public List<StudentDto> getStudentDtoForAllStudentsPage() {

        List<StudentDto> result = new ArrayList<>();

        List<Tuple> tupleStudents = entityManager.createNativeQuery(
                "SELECT s.id AS id, s.notes AS notes, s.color AS color, s.end_trial AS trialEndDate, " +
                        "s.next_pay AS nextPaymentDate, s.price AS price, s.amount AS payment_amount, " +
                        "s.later AS payLater, s.notify_email AS notifyEmail, s.notify_sms AS notifySms, " +
                        "s.notify_vk AS notifyVK, s.notify_slack AS notifySlack, " +
                        //Поля для клиента
                        "c.client_id AS client_id, c.first_name AS first_name, c.last_name AS last_name, " +
                        "c.phone_number AS phoneNumber, c.email AS clientEmail, " +
                        "ss.id AS studentStatusId, ss.status AS studentStatusName, " +
                        // Поля для статуса
                        "st.status_id AS statusID, st.status_name AS statusName " +

                        "FROM student s, " +
                        "student_status ss, " +
                        "client c, " +
                        "status st, " +
                        "status_clients sc " +

                        "WHERE s.client_id = c.client_id " +
                        "AND s.status_id = ss.id " +
                        "AND st.status_id = sc.status_id " +
                        "AND c.client_id = sc.user_id;"

                , Tuple.class).getResultList();

        for (Tuple tuple : tupleStudents) {
            //Поля для StudentDto
            long studentID = ((BigInteger) tuple.get("id")).longValue();
            String notes = tuple.get("notes") == null ? "" : (String) tuple.get("notes");
            String color = tuple.get("color") == null ? "" : (String) tuple.get("color");
            LocalDateTime trialEndDate = ((Timestamp) tuple.get("trialEndDate")).toLocalDateTime();
            LocalDateTime nextPaymentDate = ((Timestamp) tuple.get("nextPaymentDate")).toLocalDateTime();
            BigDecimal price = ((BigDecimal) tuple.get("price"));
            BigDecimal paymentAmount = ((BigDecimal) tuple.get("payment_amount"));
            BigDecimal payLater = ((BigDecimal) tuple.get("payLater"));
            boolean notifyEmail = (boolean) tuple.get("notifyEmail");
            boolean notifySms = (boolean) tuple.get("notifySms");
            boolean notifyVK = (boolean) tuple.get("notifyVK");
            boolean notifySlack = (boolean) tuple.get("notifySlack");
            long studentStatusId = ((BigInteger) tuple.get("studentStatusId")).longValue();
            String studentStatusName = tuple.get("studentStatusName") == null ? "" : (String) tuple.get("studentStatusName");
            //Создать StudentStatus по имени studentStatusName и добавить студенту.

            //Поля для ClientDtoForAllStudentsPage
            long clientId = ((BigInteger) tuple.get("client_id")).longValue();
            String firstName = tuple.get("first_name") == null ? "" : (String) tuple.get("first_name");
            String lastName = tuple.get("last_name") == null ? "" : (String) tuple.get("last_name");
            String phoneNumber = tuple.get("phoneNumber") == null ? "" : (String) tuple.get("phoneNumber");
            String email = tuple.get("clientEmail") == null ? "" : (String) tuple.get("clientEmail");
            long statusID = ((BigInteger) tuple.get("statusID")).longValue();
            String statusName = tuple.get("statusName") == null ? "" : (String) tuple.get("statusName");

            List<SocialNetworkDto> profiles = getStudentProfiles(clientId);


            ClientDtoForAllStudentsPage clientDtoForAllStudentsPage =
                    new ClientDtoForAllStudentsPage(
                            clientId,
                            firstName,
                            lastName,
                            phoneNumber,
                            new StatusDto(statusID, statusName),
                            email,
                            profiles
                    );
            //Конец создания клиента.

            //StudentDTO
            StudentDto studentDto = new StudentDto(
                    studentID,
                    clientDtoForAllStudentsPage,
                    notes,
                    color,
                    trialEndDate,
                    nextPaymentDate,
                    price,
                    paymentAmount,
                    payLater,
                    notifyEmail,
                    notifySms,
                    notifyVK,
                    notifySlack,
                    new StudentStatusDto(studentStatusId, studentStatusName)
            );
            //End

            result.add(studentDto);
        }

        return result;
    }

    /**
     * Если id соц.сети не нужны, то можно удалить ДТО соц.сети и использовать
     * непосредственно сам класс SocialProfile.java
     * @param clientId - получаем при прогонке по циклу студентов в методе getStudentDtoForAllStudentsPage().
     * @return
     */
    private List<SocialNetworkDto> getStudentProfiles(long clientId) {
        List<SocialNetworkDto> profiles = new ArrayList<>();
        List<Tuple> socialProfiles = entityManager.createNativeQuery(
                "SELECT     sn.id AS trueSocialID," +
                        "           sn.social_id AS socialID," +
                        "           sn.social_network_type AS networkType " +

                        "FROM       social_network sn," +
                        "           client_social_network csn " +

                        "WHERE " + clientId + " = csn.client_id " +
                        "       AND csn.social_network_id = sn.id;"
        , Tuple.class).getResultList();

        for (Tuple studentProfile : socialProfiles) {
            long trueSocialId = ((BigInteger) studentProfile.get("trueSocialId")).longValue();
            String socialId = studentProfile.get("socialID") == null ? "" : (String) studentProfile.get("socialID");
            String socialNetworkType = studentProfile.get("networkType") == null ? "" : (String) studentProfile.get("networkType");
            profiles.add(new SocialNetworkDto(trueSocialId, socialId, SocialNetworkType.valueOf(socialNetworkType)));
        }

        return profiles;
    }
}
