package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.AutoAnswer;

import java.util.List;

public interface AutoAnswersService extends CommonService<AutoAnswer> {
    AutoAnswer add(String subject, Long messageTemplate_id, Long status_id);
}
