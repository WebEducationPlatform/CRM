package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.ContractConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "file:./contract.properties",encoding = "UTF-8")
public class ContractConfigImpl implements ContractConfig {

    private final String fileName;
    private final String fileNameWithStamp;
    private final String filePath;
    private final String monthPointThreeTwoPeriod;
    private final String monthPointThreeThree;
    private final String monthPointThreeFour;
    private final String monthPointFourTwo;
    private final String monthPointFourThree;
    private final String onetimePointThreeTwoPeriod;
    private final String onetimePointThreeFour;
    private final String onetimePointFourThree;
    private final String diploma;
    private final String format;

    @Autowired
    public ContractConfigImpl(Environment env) {
        fileName = env.getProperty("contract.name");
        fileNameWithStamp = env.getProperty("contract.name.with.stamp");
        filePath = env.getProperty("contract.path");
        monthPointThreeTwoPeriod = env.getProperty("contract.doc.part.month.point-3.2.period");
        monthPointThreeThree = env.getProperty("contract.doc.part.month.point-3.3");
        monthPointThreeFour = env.getProperty("contract.doc.part.month.point-3.4");
        monthPointFourTwo = env.getProperty("contract.doc.part.month.point-4.2");
        monthPointFourThree = env.getProperty("contract.doc.part.month.point-4.3");
        onetimePointThreeTwoPeriod = env.getProperty("contract.doc.part.onetime.point-3.2.period");
        onetimePointThreeFour = env.getProperty("contract.doc.part.onetime.point-3.4");
        onetimePointFourThree = env.getProperty("contract.doc.part.onetime.point-4.3");

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
    public String getMonthPointThreeTwoPeriod() {
        return monthPointThreeTwoPeriod;
    }

    @Override
    public String getMonthPointThreeThree() {
        return monthPointThreeThree;
    }

    @Override
    public String getMonthPointThreeFour() {
        return monthPointThreeFour;
    }

    @Override
    public String getMonthPointFourTwo() {
        return monthPointFourTwo;
    }

    @Override
    public String getMonthPointFourThree() {
        return monthPointFourThree;
    }

    @Override
    public String getOnetimePointThreeTwoPeriod() {
        return onetimePointThreeTwoPeriod;
    }

    @Override
    public String getOnetimePointThreeFour() {
        return onetimePointThreeFour;
    }

    @Override
    public String getOnetimePointFourThree() {
        return onetimePointFourThree;
    }

    @Override
    public String getDiploma() {
        return diploma;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public String getFileNameWithStamp() {
        return fileNameWithStamp;
    }
}
