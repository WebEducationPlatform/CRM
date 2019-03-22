package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.ContractConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

@Component
@PropertySource(value = "file:./contract.properties",encoding = "UTF-8")
public class ContractConfigImpl implements ContractConfig {

    private String fileName;
    private String filePath;
    private String month;
    private String monthPoint;
    private String onetimePoint;
    private String diploma;
    private String format;

    @Autowired
    public ContractConfigImpl(Environment env) {
        fileName = env.getProperty("contract.name");
        filePath = env.getProperty("contract.path");
        month = env.getProperty("contract.doc.part.month");
        monthPoint = env.getProperty("contract.doc.part.month.point");
        onetimePoint = env.getProperty("contract.doc.part.onetime.point");
        diploma = env.getProperty("contract.doc.part.diploma");
        format = env.getProperty("contract.format");
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public String getMonth() {
        return month;
    }

    @Override
    public String getMonthPoint() {
        return monthPoint;
    }

    @Override
    public String getOnetimePoint() {
        return onetimePoint;
    }

    @Override
    public String getDiploma() {
        return diploma;
    }

    @Override
    public String getFormat() {
        return format;
    }
}
