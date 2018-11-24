package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Группа в vk, по которой будут отслеживаться вновь вступившие в нее (потенциальные студенты)
 */
@Entity
@Table(name = "vk_tracked_club") // отслеживаемая группа в ВК?? СТАЛЬНОЙ КОПИБАР
public class VkTrackedClub implements Serializable{

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_id", unique = true)
    private Long groupId;

    @Column(name = "token")
    private String token;

    /**
     * ??????
     */
    @Column(name = "client_id")
    private Long clientId;

    public VkTrackedClub(Long groupId, String token, String groupName, Long clientId) {
        this.groupId = groupId;
        this.token = token;
        this.groupName = groupName;
        this.clientId = clientId;
    }

    public VkTrackedClub(){

    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClientSecret() {
        return groupName;
    }

    public Long getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setClientSecret(String groupName) {
        this.groupName = groupName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
