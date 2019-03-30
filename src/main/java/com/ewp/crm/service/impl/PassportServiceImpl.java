package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.PassportConfig;
import com.ewp.crm.models.Passport;
import com.ewp.crm.repository.interfaces.PassportDAO;
import com.ewp.crm.service.interfaces.PassportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

@Service
public class PassportServiceImpl extends CommonServiceImpl<Passport> implements PassportService {

    private PassportDAO passportDAO;
    private final byte[] decryptKey;
    private static Logger logger = LoggerFactory.getLogger(PassportServiceImpl.class);


    @Autowired
    public PassportServiceImpl(PassportDAO passportDAO, PassportConfig passportConfig) {
        this.passportDAO = passportDAO;
        decryptKey = passportConfig.getSecretKey();
    }

    @Override
    public Passport encode(Passport passport) {
        passport.setSeries(encrypt(passport.getSeries()));
        passport.setNumber(encrypt(passport.getNumber()));
        passport.setIssuedBy(encrypt(passport.getIssuedBy()));
        passport.setRegistration(encrypt(passport.getRegistration()));
        return passport;
    }

    @Override
    public Passport decode(Passport passport) {
        passport.setSeries(decrypt(passport.getSeries()));
        passport.setNumber(decrypt(passport.getSeries()));
        passport.setIssuedBy(decrypt(passport.getIssuedBy()));
        passport.setRegistration(decrypt(passport.getRegistration()));
        return passport;
    }

    private String encrypt(String inputData) {
        try {
            SecretKey secretKey = new SecretKeySpec(decryptKey, 0, decryptKey.length, "AES");

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] byteCipherText = aesCipher.doFinal(inputData.getBytes());
            return Arrays.toString(byteCipherText);
        } catch (Exception e) {
            logger.error("Error with encrypt passport data", e);
        }
        return null;
    }

    private String decrypt(String inputData) {
        try {
            SecretKey secretKey = new SecretKeySpec(decryptKey, 0, decryptKey.length, "AES");

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] bytePlainText = aesCipher.doFinal(toByte(inputData));
            return new String(bytePlainText);
        } catch (Exception e) {
            logger.error("Error with encrypt passport data", e);
        }
        return null;
    }

    private byte[] toByte(String str) {
        String[] byteValues = str.substring(1, str.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];

        for (int i = 0, len = bytes.length; i < len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        return bytes;
    }
}
