package com.ewp.crm.service.impl;

import com.ewp.crm.models.MessageSubject;
import com.ewp.crm.repository.interfaces.MessageSubjectRepository;
import com.ewp.crm.service.interfaces.MessageSubjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageSubjectServiceImpl extends CommonServiceImpl<MessageSubject> implements MessageSubjectService {

    private final MessageSubjectRepository messageSubjectRepository;

    public MessageSubjectServiceImpl(MessageSubjectRepository messageSubjectRepository) {
        this.messageSubjectRepository = messageSubjectRepository;
    }

    @Override
    public Optional<MessageSubject> getByTitle(String title) {
        return messageSubjectRepository.getByTitle(title);
    }
}
