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
import java.util.HashMap;
import java.util.Map;
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
            String workString = prepareForm(income);
            if (income.contains("Начать обучение")) {
                client = parseClientFormOne(workString);
            } else if (income.contains("Месяц в подарок")) {
                client = parseClientFormOne(workString);
            } else if (income.contains("Остались вопросы")) {
                client = parseClientFormTwo(workString);
            } else if (income.contains("Java Test")) {
                client = parseClientFormFour(workString);
            } else {
                logger.error("The incoming email does not match any of the templates!!!");
            }
        }
        return client;
    }

    private static String prepareForm(String text) {
        return text.substring(text.indexOf("Форма:"), text.length())
                .replaceAll("<b>|</b>|(\\r\\n|\\n)", "");
    }

    private Client parseClientFormOne(String form) {
        Client client = new Client();
        String s2 = form.substring(form.indexOf("Name"), form.length())
                .replaceAll(" ", "")
                .replaceAll("Name[0-9]", "Name")
                .replaceAll("Email[0-9]", "Email");
        String[] res = s2.split("<br/>");
        Map<String, String> map = ClientData(res);
        setClientName(client, map.get("Name"));
        client.setPhoneNumber(map.get("Телефон"));
        client.setCountry(map.get("Страна"));
        client.setCity(map.get("Город"));
        if (map.containsKey("Соцсеть")) {
            client.setSocialNetworks(Collections.singletonList(getSocialNetwork(map.get("Соцсеть"))));
        }
        if (form.contains("Согласен")) {
            client.setEmail(map.get("Email"));
        } else {
            client.setEmail(map.get("Email"));
            client.setClientDescriptionComment("На пробные 3 дня");
        }
        return client;
    }

    private Client parseClientFormTwo(String form) {
        Client client = new Client();
        String s2 = form.substring(form.indexOf("Name"), form.length())
                .replaceAll(" ", "")
                .replaceAll("Name[0-9]", "Name")
                .replaceAll("Email[0-9]", "Email");
        String[] res = s2.split("<br/>");
        Map<String, String> map = ClientData(res);
        setClientName(client, map.get("Name"));
        client.setPhoneNumber(map.get("Phone"));
        client.setClientDescriptionComment(map.get("Vopros"));
        if (map.containsKey("Social")) {
            client.setSocialNetworks(Collections.singletonList(getSocialNetwork(map.get("Social"))));
        }
        return client;
    }

    private Client parseClientFormFour(String form) {
        Client client = new Client();
        String s = form.replaceAll(" ", "");
        String s2 = s.substring(s.indexOf("Имя"), s.length());
        String[] res = s2.split("<br/>");
        Map<String, String> map = ClientData(res);
        setClientName(client, map.get("Имя"));
        if (map.containsKey("Social2")) {
            client.setSocialNetworks(Collections.singletonList(getSocialNetwork(map.get("Social2"))));
        }
        client.setPhoneNumber(map.get("Phone6"));
        client.setCountry(map.get("City6"));
        client.setEmail(map.get("Email2"));
        client.setClientDescriptionComment("Проходил Тест");
        return client;
    }

    private SocialNetwork getSocialNetwork(String link) {
        SocialNetwork socialNetwork = new SocialNetwork();
        if (link.contains("vk.com") || link.contains("m.vk.com")) {
            socialNetwork.setLink(link);
            socialNetwork.setSocialNetworkType(socialNetworkTypeService.getByTypeName("vk"));
        } else if (link.startsWith("www.facebook.com") || link.startsWith("m.facebook.com")) {
            socialNetwork.setLink(link);
            socialNetwork.setSocialNetworkType(socialNetworkTypeService.getByTypeName("facebook"));
        } else {
            socialNetwork = null;
        }
        return socialNetwork;
    }

    private Map<String, String> ClientData(String[] res) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < res.length; i++) {
            String name = res[i].substring(0, res[i].indexOf(":"));
            String value = res[i].substring(res[i].indexOf(":") + 1, res[i].length());
            map.put(name, value);
        }
        return map;
    }

    private void setClientName(Client client, String fullName) {
        if (StringUtils.countOccurrencesOf(fullName, " ") == 1) {
            String[] full = fullName.split(" ");
            client.setName(full[0]);
            client.setLastName(full[1]);
        } else {
            client.setName(fullName);
        }
    }
}
