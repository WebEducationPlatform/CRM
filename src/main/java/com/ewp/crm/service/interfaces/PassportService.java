package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Passport;

public interface PassportService extends CommonService<Passport> {

    Passport encode(Passport passport);

    Passport decode(Passport passport);
}
