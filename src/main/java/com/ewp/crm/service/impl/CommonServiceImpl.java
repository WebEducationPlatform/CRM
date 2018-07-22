package com.ewp.crm.service.impl;

import com.ewp.crm.repository.interfaces.CommonGenericRepository;
import com.ewp.crm.service.interfaces.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public class CommonServiceImpl<T> implements CommonService<T> {

    @Autowired
    private CommonGenericRepository<T> repository;

    @Override
    public T get(Long id) {
        return repository.findOne(id);
    }

    @Override
    public void add(T entity) {
        repository.saveAndFlush(entity);
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }

    @Override
    public void update(T entity) {
        repository.saveAndFlush(entity);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }
}
