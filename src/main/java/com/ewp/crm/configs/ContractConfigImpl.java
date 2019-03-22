package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.ContractConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:./contract.properties")
public class ContractConfigImpl implements ContractConfig {

    private String name;
    private String path;
    private String month;
    private String monthPoint;
    private String onetimePoint;
    private String diploma;

    @Autowired
    public ContractConfigImpl(Environment env) {
        name = env.getProperty("contract.name");
        path = env.getProperty("contract.path");
        month = env.getProperty("contract.doc.part.month");
        monthPoint = env.getProperty("contract.doc.part.month.point");
        onetimePoint = env.getProperty("contract.doc.part.onetime.point");
        diploma = env.getProperty("contract.doc.part.diploma");
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getMonth() {
        return month;
    }

    public String getMonthPoint() {
        return monthPoint;
    }

    public String getOnetimePoint() {
        return onetimePoint;
    }

    public String getDiploma() {
        return diploma;
    }
}
