package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.MessageSubject;

import java.util.Optional;

public interface MessageSubjectRepository extends CommonGenericRepository<MessageSubject> {
    Optional<MessageSubject> getByTitle(String name);
}
