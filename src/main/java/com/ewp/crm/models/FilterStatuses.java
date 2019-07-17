package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "filter_statuses")
public class FilterStatuses implements Serializable {
    @EmbeddedId
    private FilterStatusesId filterStatusesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("statusId")
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    private User user;

    @Column(name = "filterType")
    @Enumerated(EnumType.STRING)
    private FilteredType filterType ;

    public enum FilteredType {
        BY_MENTOR,          // выборка списка клиентов в статусе по ментору
        BY_OWNER
        }
    @Column(name = "filter_id")
    private Long filterId;

    public FilterStatuses() {
    }

    public FilterStatuses(Status status, User user) {
        this.filterStatusesId = new FilterStatusesId(status.getId(), user.getId());
        this.status = status;
        this.user = user;

    }
    public FilterStatuses(Status status, User user, Long filterId) {
        this.filterStatusesId = new FilterStatusesId(status.getId(), user.getId());
        this.status = status;
        this.user = user;
        this.filterId = filterId;
    }

    public FilterStatusesId getFilterStatusesId() {
        return filterStatusesId;
    }

    public void setFilterStatusesId(FilterStatusesId filterStatusesId) {
        this.filterStatusesId = filterStatusesId;
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

    public FilteredType getFilterType() {
        return filterType;
    }

    public void setFilterType(FilteredType filterType) {
        this.filterType = filterType;
    }

    public Long getFilterId() {
        return filterId;
    }

    public void setFilterId(Long filterId) {
        this.filterId = filterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterStatuses that = (FilterStatuses) o;
        return Objects.equals(filterStatusesId, that.filterStatusesId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(user, that.user) &&
                filterType == that.filterType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(filterStatusesId, status, user, filterType);
    }
}
