package com.ewp.crm.utils.converters;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialNetwork;
import com.ewp.crm.service.interfaces.SocialNetworkTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Optional;

@Component
public class IncomeStringToClient {

    private final SocialNetworkTypeService socialNetworkTypeService;

    private static final Logger logger = LoggerFactory.getLogger(IncomeStringToClient.class);

    @Autowired
    public IncomeStringToClient(SocialNetworkTypeService socialNetworkTypeService) {
        this.socialNetworkTypeService = socialNetworkTypeService;
    }

    public Client convert(String income) {
        Client client = null;
        if (income != null && !income.isEmpty()) {
            String[] workArr = getArrayFromIncomeData(income);
            if (income.contains("Name 3:")) {
                client = firstPlainTextTemplate(workArr);
            } else if (income.contains("Name:")) {
                client = secondPlainTextTemplate(workArr);
            } else if (income.contains("Имя:")) {
                client = thirdPlainTextTemplate(workArr);
            } else {
                logger.error("The incoming email does not match any of the templates!!!");
            }
        }

        return client;
    }

    private static String[] getArrayFromIncomeData(String income) {
        if (income.contains("<br>")) {
            String[] temp = income.replaceAll("<br><br>", "<br>").split("<br>");
            System.arraycopy(temp, 1, temp, 0, temp.length - 1);
            return temp;
        } else {
            return income.split("\r\n");
        }
    }

    private Client firstPlainTextTemplate(String[] workArr) {
        Client resultClient = new Client();
        Optional<String[]> parsedName = parseName(getValueOfField(workArr[6]));
        if (parsedName.isPresent()) {
            resultClient.setName(parsedName.get()[0]);
            resultClient.setLastName(parsedName.get()[1]);
        } else {
            resultClient.setName(getValueOfField(workArr[0]));
        }
        resultClient.setSocialNetworks(Collections.singletonList(getSocialNetwork(getValueOfField(workArr[1]))));
        resultClient.setPhoneNumber(getValueOfField(workArr[2]));
        resultClient.setEmail(getValueOfField(workArr[3]));
        resultClient.setComment(getValueOfField(workArr[4]));
        return resultClient;
    }

    private Client secondPlainTextTemplate(String[] workArr) {
        Client resultClient = new Client();
        Optional<String[]> parsedName = parseName(getValueOfField(workArr[6]));
        if (parsedName.isPresent()) {
            resultClient.setName(parsedName.get()[0]);
            resultClient.setLastName(parsedName.get()[1]);
        } else {
            resultClient.setName(getValueOfField(workArr[0]));
        }
        resultClient.setSocialNetworks(Collections.singletonList(getSocialNetwork(getValueOfField(workArr[1]))));
        resultClient.setEmail(getValueOfField(workArr[2]));
        resultClient.setPhoneNumber(getValueOfField(workArr[3]));
        resultClient.setCity(getValueOfField(workArr[4]));
        resultClient.setComment(getValueOfField(workArr[5]));
        return resultClient;
    }

    private Client thirdPlainTextTemplate(String[] workArr) {

        Client resultClient = new Client();
        Optional<String[]> parsedName = parseName(getValueOfField(workArr[6]));
        if (parsedName.isPresent()) {
            resultClient.setName(parsedName.get()[0]);
            resultClient.setLastName(parsedName.get()[1]);
        } else {
            resultClient.setName(getValueOfField(workArr[6]));
        }
        resultClient.setSocialNetworks(Collections.singletonList(getSocialNetwork(getValueOfField(workArr[7]))));
        resultClient.setPhoneNumber(getValueOfField(workArr[8]));
        resultClient.setCity(getValueOfField(workArr[9]));
        resultClient.setEmail(getValueOfField(workArr[10]));
        return resultClient;
    }

    private static Optional<String[]> parseName(String fullName) {
        if (StringUtils.countOccurrencesOf(fullName, " ") == 1) {
            return Optional.of(fullName.split(" ", 2));
        }
        return Optional.empty();

    }


    private SocialNetwork getSocialNetwork(String link) {
        if (link.startsWith("https://")) {
            link = link.substring(8);
        }
        SocialNetwork socialNetwork = new SocialNetwork();

        if (link.startsWith("vk.com") || link.startsWith("m.vk.com")) {
            socialNetwork.setLink(link);
            socialNetwork.setSocialNetworkType(socialNetworkTypeService.getByTypeName("vk"));
        } else if (link.startsWith("www.facebook.com") || link.startsWith("m.facebook.com")) {
            socialNetwork.setLink(link);
            socialNetwork.setSocialNetworkType(socialNetworkTypeService.getByTypeName("facebook"));
        }
        return socialNetwork;
    }

    private static String getValueOfField(String fullString) {
        String result;
        try {
            result = fullString.substring(fullString.indexOf(":") + 2);
        } catch (IndexOutOfBoundsException e) {
            result = "";
        }
        return result;
    }


}
