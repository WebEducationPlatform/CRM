package com.ewp.crm.service.impl;

import com.ewp.crm.models.AutoAnswer;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.Status;
import com.ewp.crm.repository.interfaces.AutoAnswerRepository;
import com.ewp.crm.service.interfaces.AutoAnswersService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutoAnswersServiceImpl implements AutoAnswersService {
    private final AutoAnswerRepository autoAnswerRepository;
    private final StatusService statusService;
    private final MessageTemplateService messageTemplateService;

    @Autowired
    public AutoAnswersServiceImpl(AutoAnswerRepository autoAnswerRepository, StatusService statusService, MessageTemplateService messageTemplateService) {
        this.autoAnswerRepository = autoAnswerRepository;
        this.statusService = statusService;
        this.messageTemplateService = messageTemplateService;
    }

    @Override
    public AutoAnswer get(Long id) {
        return autoAnswerRepository.getOne(id);
    }

    @Override
    public AutoAnswer add(AutoAnswer entity) {
        autoAnswerRepository.saveAndFlush(entity);
        return entity;
    }

    @Override
    public AutoAnswer add(String subject, Long messageTemplate_id, Long status_id) {

        MessageTemplate messageTemplate = messageTemplateService.get(messageTemplate_id);
        Optional<Status> status = statusService.get(status_id);
        AutoAnswer entity = new AutoAnswer(subject, messageTemplate,status.get());
        autoAnswerRepository.saveAndFlush(entity);
        return entity;
    }

    @Override
    public List<AutoAnswer> getAll() {
        return autoAnswerRepository.findAll();
    }

    @Override
    public void update(AutoAnswer entity) {
        autoAnswerRepository.saveAndFlush(entity);
    }

    @Override
    public void delete(Long id) {
        autoAnswerRepository.delete(autoAnswerRepository.getOne(id));
    }

    @Override
    public void delete(AutoAnswer entity) {
        autoAnswerRepository.delete(entity);
    }

    @Override
    public Optional<Status> getStatusBySubject(String subject){
        return Optional.of(autoAnswerRepository.findBySubjectEquals(subject).getStatus());
    }

    @Override
    public MessageTemplate getMesssageTemplateBySubject(String subject){
        return autoAnswerRepository.findBySubjectEquals(subject).getMessageTemplate();
    }
}
