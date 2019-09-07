package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.AutoAnswer;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoAnswerRepository extends CommonGenericRepository<AutoAnswer> {

    AutoAnswer findBySubjectEquals(String subject);

}
