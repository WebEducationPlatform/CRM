package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.AutoAnswer;
import com.ewp.crm.models.Status;

import java.util.List;

public interface AutoAnswersService extends CommonService<AutoAnswer> {
    AutoAnswer add(String subject, Long messageTemplate_id, Long status_id);

    Status findBySubjectEquals(String subject);
}
