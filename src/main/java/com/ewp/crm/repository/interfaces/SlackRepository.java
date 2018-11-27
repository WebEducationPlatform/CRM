package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SlackProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SlackRepository extends CommonGenericRepository<SlackProfile> {

    @Query("SELECT sp FROM SlackProfile sp WHERE sp.email = :email")
    SlackProfile getSlackProfileByEmail(@Param("email") String email);
}
