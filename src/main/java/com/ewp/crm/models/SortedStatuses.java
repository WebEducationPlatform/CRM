package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
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
    @Enumerated(EnumType.STRING)
    private SortingType sortingType;

    @Column(name = "is_invisible")
    private Boolean isInvisible = false;

    @Column(name = "position")
    private Long position;

    public enum SortingType {
        NEW_FIRST,          //свежие клиенты выше в выдаче
        OLD_FIRST,          //старые клиенты выше в выдаче
        NEW_CHANGES_FIRST,  //клиенты со свежими изменениями в истории выше в выдаче
        OLD_CHANGES_FIRST   //клиенты со старыми изменениями в истории выше в выдаче
    }

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

    public SortingType getSortingType() {
        return sortingType;
    }

    public void setSortingType(SortingType sortingType) {
        this.sortingType = sortingType;
    }

    public Boolean getInvisible() {
        return isInvisible;
    }

    public void setInvisible(Boolean invisible) {
        isInvisible = invisible;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SortedStatuses that = (SortedStatuses) o;
        return Objects.equals(sortedStatusesId, that.sortedStatusesId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sortedStatusesId, status, user);
    }
}
