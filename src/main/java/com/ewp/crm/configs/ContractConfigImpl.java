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
    private String monthParagraphThree;
    private String monthParagraphFour;
    private String monthPointParagraphFour;
    private String monthPointParagraphThree;
    private String onetimePointParagraphThree;
    private String onetimePointParagraphFour;
    private String monthParagraphThreeDotTwo;
    private String onetimeParagraphThreeDotTwo;
    private String diploma;
    private String format;

    @Autowired
    public ContractConfigImpl(Environment env) {
        fileName = env.getProperty("contract.name");
        filePath = env.getProperty("contract.path");
        monthParagraphThree = env.getProperty("contract.doc.part.month-3");
        monthPointParagraphThree = env.getProperty("contract.doc.part.month.point-3");
        onetimePointParagraphThree = env.getProperty("contract.doc.part.onetime.point-3");
        monthParagraphFour = env.getProperty("contract.doc.part.month-4");
        monthPointParagraphFour = env.getProperty("contract.doc.part.month.point-4");
        onetimePointParagraphFour = env.getProperty("contract.doc.part.onetime.point-4");
        monthParagraphThreeDotTwo = env.getProperty("contract.doc.part.month-3-2");
        onetimeParagraphThreeDotTwo = env.getProperty("contract.doc.part.onetime-3-2");
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
    public String getMonthParagraphThree() {
        return monthParagraphThree;
    }

    @Override
    public String getMonthParagraphFour() {
        return monthParagraphFour;
    }

    @Override
    public String getMonthPointParagraphFour() {
        return monthPointParagraphFour;
    }

    @Override
    public String getMonthPointParagraphThree() {
        return monthPointParagraphThree;
    }

    @Override
    public String getOnetimePointParagraphThree() {
        return onetimePointParagraphThree;
    }

    @Override
    public String getOnetimePointParagraphFour() {
        return onetimePointParagraphFour;
    }

    @Override
    public String getMonthParagraphThreeDotTwo() {
        return monthParagraphThreeDotTwo;
    }

    @Override
    public String getOnetimeParagraphThreeDotTwo() {
        return onetimeParagraphThreeDotTwo;
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
