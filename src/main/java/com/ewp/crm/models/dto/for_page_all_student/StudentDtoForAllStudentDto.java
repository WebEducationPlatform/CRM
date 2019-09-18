package com.ewp.crm.models.dto.for_page_all_student;

import com.ewp.crm.models.Student;
import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.models.dto.ClientDto;
import com.ewp.crm.models.dto.StatusDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Данный класс предназначен для отображения информации о студенитах на вкладке "Все студенты"
 */
public class StudentDtoForAllStudentDto {

    private long id;
    private ClientDtoForAllStudentsDto clientDtoForAllStudentsDto;
    private StatusDto statusDto;
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

    public StudentDtoForAllStudentDto() {
    }

    public StudentDtoForAllStudentDto(long id,
                                      ClientDtoForAllStudentsDto clientDtoForAllStudentsDto,
                                      StatusDto statusDto,
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
        this.clientDtoForAllStudentsDto = clientDtoForAllStudentsDto;
        this.statusDto = statusDto;
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

    public ClientDtoForAllStudentsDto getClientDtoForAllStudentsDto() {
        return clientDtoForAllStudentsDto;
    }

    public void setClientDtoForAllStudentsDto(ClientDtoForAllStudentsDto clientDtoForAllStudentsDto) {
        this.clientDtoForAllStudentsDto = clientDtoForAllStudentsDto;
    }

    public StatusDto getStatusDto() {
        return statusDto;
    }

    public void setStatusDto(StatusDto statusDto) {
        this.statusDto = statusDto;
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

    public static StudentDtoForAllStudentDto getStudentDtoForAllStudentDto(Student student) {
        StudentDtoForAllStudentDto studentDtoForAllStudentDto = new StudentDtoForAllStudentDto();

        studentDtoForAllStudentDto.id = student.getId();


        return studentDtoForAllStudentDto;
    }
}
