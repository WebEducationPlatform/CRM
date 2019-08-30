package com.ewp.crm.service.impl;

import com.ewp.crm.models.AutoAnswer;
import com.ewp.crm.repository.interfaces.AutoAnswerRepository;
import com.ewp.crm.service.interfaces.AutoAnswersService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutoAnswersServiceImpl implements AutoAnswersService {
    private final AutoAnswerRepository autoAnswerRepository;

    public AutoAnswersServiceImpl(AutoAnswerRepository autoAnswerRepository) {
        this.autoAnswerRepository = autoAnswerRepository;
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
}
