package com.ewp.crm.models.dto;

import com.ewp.crm.models.UserTask;

import java.time.LocalDate;

public class UserTaskDto {
    private String task;
    private LocalDate date;
    private LocalDate expiry_date;
    private Long author_id;
    private Long manager_id;
    private Long executor_id;
    private Long client_id;


    public UserTaskDto() {
    }

    public UserTaskDto(String task, LocalDate date, LocalDate expiry_date, Long author_id,Long manager_id, Long executor_id, Long client_id) {
        this.task = task;
        this.date = date;
        this.expiry_date = expiry_date;
        this.author_id = author_id;
        this.manager_id = manager_id;
        this.executor_id = executor_id;
        this.client_id = client_id;
    }

    public String getTask() {
        return task;
    }


    public static UserTaskDto getUserTaskDto(UserTask userTask) {
        return new UserTaskDto(userTask.getTask(), userTask.getDate(), userTask.getExpiry_date(),
                userTask.getAuthor().getId(),userTask.getManager().getId(), userTask.getExecutor().getId(), userTask.getClient().getId());
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

    public Long getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(Long author_id) {
        this.author_id = author_id;
    }

    public Long getManager_id() {
        return manager_id;
    }

    public void setManager_id(Long manager_id) {
        this.manager_id = manager_id;
    }

    public Long getExecutor_id() {
        return executor_id;
    }

    public void setExecutor_id(Long executor_id) {
        this.executor_id = executor_id;
    }

    public Long getClient_id() {
        return client_id;
    }

    public void setClient_id(Long client_id) {
        this.client_id = client_id;
    }
}
