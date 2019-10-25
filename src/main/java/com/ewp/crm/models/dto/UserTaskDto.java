package com.ewp.crm.models.dto;

import com.ewp.crm.models.UserTask;

import java.time.LocalDate;

public class UserTaskDto {
    private Long id;
    private String task;
    private LocalDate date;
    private LocalDate expiry_date;
    private Long authorId;
    private Long managerId;
    private Long executorId;
    private Long clientId;
    private String authorFullName;
    private String managerFullName;
    private String executorFullName;
    private String clientFullName;

    public UserTaskDto() {
    }

    public UserTaskDto(Long id, String task, LocalDate date, LocalDate expiry_date, Long author_id, Long manager_id,
                       Long executor_id, Long client_id, String authorFullName, String managerFullName,
                       String executorFullName, String clientFullName) {
        this.id = id;
        this.task = task;
        this.date = date;
        this.expiry_date = expiry_date;
        this.authorId = author_id;
        this.managerId = manager_id;
        this.executorId = executor_id;
        this.clientId = client_id;
        this.authorFullName = authorFullName;
        this.managerFullName = managerFullName;
        this.executorFullName = executorFullName;
        this.clientFullName = clientFullName;
    }

    public String getTask() {
        return task;
    }


    public static UserTaskDto getUserTaskDto(UserTask userTask) {
        return new UserTaskDto(userTask.getId(), userTask.getTask(), userTask.getDate(), userTask.getExpiry_date(),
                userTask.getAuthor().getId(),userTask.getManager().getId(), userTask.getExecutor().getId(), userTask.getClient().getId(),
                userTask.getAuthor().getFullName(), userTask.getManager().getFullName(), userTask.getExecutor().getFullName() ,
                userTask.getClient().getName() + " " + userTask.getClient().getLastName());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(LocalDate expiry_date) {
        this.expiry_date = expiry_date;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getExecutorId() {
        return executorId;
    }

    public void setExecutorId(Long executorId) {
        this.executorId = executorId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getAuthorFullName() {
        return authorFullName;
    }

    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }

    public String getManagerFullName() {
        return managerFullName;
    }

    public void setManagerFullName(String managerFullName) {
        this.managerFullName = managerFullName;
    }

    public String getExecutorFullName() {
        return executorFullName;
    }

    public void setExecutorFullName(String executorFullName) {
        this.executorFullName = executorFullName;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }
}
