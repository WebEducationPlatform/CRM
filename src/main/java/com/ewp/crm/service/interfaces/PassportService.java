package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Passport;

import java.util.Optional;

public interface PassportService extends CommonService<Passport> {

    Optional<Passport> encode(Passport passport);

    Optional<Passport> decode(Passport passport);
}
