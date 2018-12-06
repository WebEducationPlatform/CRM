package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "SortedStatuses")
@Table(name = "sorted_statuses")
public class SortedStatuses implements Serializable {

    @EmbeddedId
    private SortedStatusesId sortedStatusesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("statusId")
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    private User user;

    @Column(name = "sortingType")
    private String sortingType;

    public SortedStatuses() {
    }

    public SortedStatuses(Status status, User user) {
        this.status = status;
        this.user = user;
        this.sortedStatusesId = new SortedStatusesId(status.getId(), user.getId());

    }

    public SortedStatusesId getSortedStatusesId() {
        return sortedStatusesId;
    }

    public void setSortedStatusesId(SortedStatusesId sortedStatusesId) {
        this.sortedStatusesId = sortedStatusesId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSortingType() {
        return sortingType;
    }

    public void setSortingType(String sortingType) {
        this.sortingType = sortingType;
    }
}
