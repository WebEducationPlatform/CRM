package com.ewp.crm.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FilterStatusesId  implements Serializable {

    @Column(name = "status_id")
    private Long statusId;

    @Column(name = "user_id")
    private Long userId;

    public FilterStatusesId() {
    }

    public FilterStatusesId(Long statusId, Long userId) {
        this.statusId = statusId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterStatusesId that = (FilterStatusesId) o;
        return Objects.equals(statusId, that.statusId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusId, userId);
    }
}
