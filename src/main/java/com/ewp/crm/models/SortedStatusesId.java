package com.ewp.crm.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SortedStatusesId implements Serializable {

    @Column(name = "status_id")
    private Long statusId;

    @Column(name = "user_id")
    private Long userId;

    public SortedStatusesId() {
    }

    public SortedStatusesId(Long statusId, Long userId) {
        this.statusId = statusId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SortedStatusesId that = (SortedStatusesId) o;
        return Objects.equals(statusId, that.statusId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusId, userId);
    }
}
