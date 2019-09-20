package com.ewp.crm.models.dto.all_students_page;

import com.ewp.crm.models.Student;
import com.ewp.crm.models.StudentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Данный класс предназначен для отображения информации о студенитах на вкладке "Все студенты"
 * Поля, которые есть в all-students-table.html:
 *  * 1. id
 *  * 2. color
 *  * 3. client:
 *  *      a) status
 *  *      b) name
 *  *      c) lastName
 *  *      d) email
 *  *      e) phoneNumber
 *  *      f) socialProfiles
 *  *      g) id
 *  * 4. trialEndDate
 *  * 5. nextPaymentDate
 *  * 6. price
 *  * 7. paymentAmount
 *  * 8. payLater
 *  * 9. notifyEmail
 *  * 10. notifySms
 *  * 11. notifyVK
 *  * 12. notifySlack
 *  * 13. notes
 */
public class StudentDto {

    private long id;
    private ClientDtoForAllStudentsPage clientDtoForAllStudentsPage;
    private String notes;
    private String color;
    private LocalDateTime trialEndDate;
    private LocalDateTime nextPaymentDate;
    private BigDecimal price;
    private BigDecimal paymentAmount;
    private BigDecimal payLater;
    private boolean notifyEmail = false;
    private boolean notifySMS = false;
    private boolean notifyVK = false;
    private boolean notifySlack = false;
    private StudentStatus studentStatus;

    public StudentDto() {
    }

    public StudentDto(long id,
                      ClientDtoForAllStudentsPage clientDtoForAllStudentsPage,
                      String notes,
                      String color,
                      LocalDateTime trialEndDate,
                      LocalDateTime nextPaymentDate,
                      BigDecimal price,
                      BigDecimal paymentAmount,
                      BigDecimal payLater,
                      boolean notifyEmail,
                      boolean notifySMS,
                      boolean notifyVK,
                      boolean notifySlack,
                      StudentStatus studentStatus) {
        this.id = id;
        this.clientDtoForAllStudentsPage = clientDtoForAllStudentsPage;
        this.notes = notes;
        this.color = color;
        this.trialEndDate = trialEndDate;
        this.nextPaymentDate = nextPaymentDate;
        this.price = price;
        this.paymentAmount = paymentAmount;
        this.payLater = payLater;
        this.notifyEmail = notifyEmail;
        this.notifySMS = notifySMS;
        this.notifyVK = notifyVK;
        this.notifySlack = notifySlack;
        this.studentStatus = studentStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ClientDtoForAllStudentsPage getClientDtoForAllStudentsPage() {
        return clientDtoForAllStudentsPage;
    }

    public void setClientDtoForAllStudentsPage(ClientDtoForAllStudentsPage clientDtoForAllStudentsPage) {
        this.clientDtoForAllStudentsPage = clientDtoForAllStudentsPage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocalDateTime getTrialEndDate() {
        return trialEndDate;
    }

    public void setTrialEndDate(LocalDateTime trialEndDate) {
        this.trialEndDate = trialEndDate;
    }

    public LocalDateTime getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(LocalDateTime nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getPayLater() {
        return payLater;
    }

    public void setPayLater(BigDecimal payLater) {
        this.payLater = payLater;
    }

    public boolean isNotifyEmail() {
        return notifyEmail;
    }

    public void setNotifyEmail(boolean notifyEmail) {
        this.notifyEmail = notifyEmail;
    }

    public boolean isNotifySMS() {
        return notifySMS;
    }

    public void setNotifySMS(boolean notifySMS) {
        this.notifySMS = notifySMS;
    }

    public boolean isNotifyVK() {
        return notifyVK;
    }

    public void setNotifyVK(boolean notifyVK) {
        this.notifyVK = notifyVK;
    }

    public boolean isNotifySlack() {
        return notifySlack;
    }

    public void setNotifySlack(boolean notifySlack) {
        this.notifySlack = notifySlack;
    }

    public StudentStatus getStudentStatus() {
        return studentStatus;
    }

    public void setStudentStatus(StudentStatus studentStatus) {
        this.studentStatus = studentStatus;
    }

    /**
     * Данный класс предназначен для работы со страницей "Все студенты"
     * @param student - принимаемый студент,
     * @return - возвращаемый студент.
     */
    public static StudentDto getStudentDtoForAllStudentDto(Student student) {
        StudentDto studentDto = new StudentDto();

        studentDto.id = student.getId();
        studentDto.clientDtoForAllStudentsPage =
                ClientDtoForAllStudentsPage.getClientDtoForAllStudentsPage(student.getClient());
        studentDto.notes = student.getNotes();
        studentDto.color = student.getColor();
        studentDto.trialEndDate = student.getTrialEndDate();
        studentDto.nextPaymentDate = student.getNextPaymentDate();
        studentDto.price = student.getPrice();
        studentDto.paymentAmount = student.getPaymentAmount();
        studentDto.payLater = student.getPayLater();
        studentDto.notifyEmail = student.isNotifyEmail();
        studentDto.notifySMS = student.isNotifySMS();
        studentDto.notifyVK = student.isNotifyVK();
        studentDto.notifySlack = student.isNotifySlack();
        studentDto.studentStatus = student.getStatus();

        return studentDto;
    }

    public static List<StudentDto> getStudentDtoForAllStudentDto(List<Student> students) {
        return students
                .stream()
                .map(StudentDto::getStudentDtoForAllStudentDto)
                .collect(Collectors.toList());
    }
}
