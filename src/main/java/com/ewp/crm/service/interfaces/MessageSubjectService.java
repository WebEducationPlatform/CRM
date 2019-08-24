package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.MessageSubject;
import com.ewp.crm.models.StudentStatus;

import java.util.List;
import java.util.Optional;

public interface MessageSubjectService extends CommonService<MessageSubject>{

    Optional<MessageSubject> getByTitle(String title);

}
