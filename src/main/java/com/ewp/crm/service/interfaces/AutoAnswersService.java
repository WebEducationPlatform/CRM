package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.AutoAnswer;
import com.ewp.crm.models.Status;

import java.util.Optional;

public interface AutoAnswersService extends CommonService<AutoAnswer> {

    AutoAnswer add(String subject, Long messageTemplate_id, Long status_id);

    Optional<AutoAnswer> getAutoAnswerBySubject(String subject);
}
