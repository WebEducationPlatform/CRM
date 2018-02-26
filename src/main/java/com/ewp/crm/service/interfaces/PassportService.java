package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Passport;

import java.util.List;

public interface PassportService {

    void add(Passport passport);

    void update(Passport passport);

    List<Passport> getAll();

    Passport get(Long id);

    void delete(Passport passport);
}
