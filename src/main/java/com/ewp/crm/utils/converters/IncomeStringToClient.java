package com.ewp.crm.utils.converters;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Comment;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.UserDAO;
import com.ewp.crm.service.interfaces.SocialProfileTypeService;
import com.ewp.crm.service.interfaces.VKService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class IncomeStringToClient {

    @Autowired
    UserDAO userService;

    private final SocialProfileTypeService socialProfileTypeService;

    private final VKService vkService;

    private static final Logger logger = LoggerFactory.getLogger(IncomeStringToClient.class);

    @Autowired
    public IncomeStringToClient(SocialProfileTypeService socialProfileTypeService, VKService vkService) {
        this.socialProfileTypeService = socialProfileTypeService;
        this.vkService = vkService;
    }

    public Client convert(String income) {
        Client client = null;
        logger.info("Start of conversion");
        if (income != null && !income.isEmpty()) {
            String workString = prepareForm(income);
            if (income.contains("Начать обучение")) {
                client = parseClientFormOne(workString);
            } else if (income.contains("Месяц в подарок")) {
                client = parseClientFormOne(workString);
            } else if (income.contains("Остались вопросы")) {
                client = parseClientFormTwo(workString);
            } else if (income.contains("Задать вопрос")) {
                client = parseClientFormThree(workString);
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
        logger.info("Parsing FormOne...");
        Client client = new Client();
        String removeExtraCharacters = form.substring(form.indexOf("Форма"), form.length())
                .replaceAll(" ", "")
                .replaceAll("Name[0-9]", "Name")
                .replaceAll("Email[0-9]", "Email");
        String[] createArrayFromString = removeExtraCharacters.split("<br/>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        setClientName(client, clientData.get("Name"));
        client.setPhoneNumber(clientData.get("Телефон"));
        client.setCountry(clientData.get("Страна"));
        client.setCity(clientData.get("Город"));
        if (clientData.containsKey("Соцсеть")) {
            SocialProfile currentSocialProfile = getSocialNetwork(clientData.get("Соцсеть"));
            if (currentSocialProfile.getSocialProfileType().getName().equals("unknown")) {
                client.setComment("Ссылка на социальную сеть " + currentSocialProfile.getLink() +
                        " недействительна");
                logger.warn("Unknown social network");
            }
            client.setSocialProfiles(Collections.singletonList(currentSocialProfile));
        }
        client.setEmail(clientData.get("Email"));
        String comment = clientData.get("Форма");
        client.setClientDescriptionComment(comment);

        logger.info("FormOne parsing finished");
        return client;
    }

    private Client parseClientFormTwo(String form) {
        logger.info("Parsing FormTwo...");
        Client client = new Client();
        String removeExtraCharacters = form.substring(form.indexOf("Name"), form.length())
                .replaceAll(" ", "")
                .replaceAll("Name[0-9]", "Name")
                .replaceAll("Email[0-9]", "Email")
                .replaceAll("Phone[0-9]", "Phone")
                .replaceAll("Social[0-9]", "Social")
                .replaceAll("Вопрос[0-9]", "Vopros");
        String[] createArrayFromString = removeExtraCharacters.split("<br/>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        setClientName(client, clientData.get("Name"));
        client.setEmail(clientData.get("Email"));
        client.setPhoneNumber(clientData.get("Phone"));
        client.setClientDescriptionComment(clientData.get("Vopros"));
        checkSocialNetworks(client, clientData);
        logger.info("FormTwo parsing finished");
        return client;
    }

    private void checkSocialNetworks(Client client, Map<String, String> clientData) {
        if (clientData.containsKey("Social")) {
            SocialProfile currentSocialProfile = getSocialNetwork(clientData.get("Social"));
            if (currentSocialProfile.getSocialProfileType().getName().equals("unknown")) {
                client.setComment("Ссылка на социальную сеть " + currentSocialProfile.getLink() +
                        " недействительна");
                logger.warn("Unknown social network");
            }
            client.setSocialProfiles(Collections.singletonList(currentSocialProfile));
        }
    }

    private Client parseClientFormThree(String form) {
        logger.info("Parsing FormThree...");
        Client client = new Client();
        String removeExtraCharacters = form.substring(form.indexOf("Name"), form.length())
                .replaceAll(" ", "")
                .replaceAll("Name[0-9]", "Name")
                .replaceAll("Email[0-9]", "Email")
                .replaceAll("Phone[0-9]", "Phone")
                .replaceAll("Social[0-9]", "Social")
                .replaceAll("Вопрос[0-9]", "Вопрос");
        String[] createArrayFromString = removeExtraCharacters.split("<br/>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        setClientName(client, clientData.get("Name"));
        client.setEmail(clientData.get("Email"));
        client.setPhoneNumber(clientData.get("Phone"));
        client.setClientDescriptionComment(clientData.get("Вопрос"));

        checkSocialNetworks(client, clientData);
        logger.info("FormThree parsing finished");
        return client;
    }

    private Client parseClientFormFour(String form) {
        logger.info("Parsing FormFour...");
        Client client = new Client();
        String replaceSpaceInString = form.replaceAll(" ", "");
        String removeExtraCharacters = replaceSpaceInString.substring(replaceSpaceInString.indexOf("Имя"), replaceSpaceInString.length());
        String[] createArrayFromString = removeExtraCharacters.split("<br/>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        setClientName(client, clientData.get("Имя"));
        if (clientData.containsKey("Social2")) {
            SocialProfile currentSocialProfile = getSocialNetwork(clientData.get("Social2"));
            if (currentSocialProfile.getSocialProfileType().getName().equals("unknown")) {
                client.setComment("Ссылка на социальную сеть " + currentSocialProfile.getLink() +
                        " недействительна");
                logger.warn("Unknown social network");
            }
            client.setSocialProfiles(Collections.singletonList(currentSocialProfile));
        }
        client.setPhoneNumber(clientData.get("Phone6"));
        client.setCountry(clientData.get("City6"));
        client.setEmail(clientData.get("Email2"));
        client.setClientDescriptionComment("Проходил Тест");
        logger.info("FormFour parsing finished");
        return client;
    }

    private SocialProfile getSocialNetwork(String link) {
        SocialProfile socialProfile = new SocialProfile();
        socialProfile.setLink(link);
        if (link.contains("vk.com") || link.contains("m.vk.com")) {
            String validLink = vkService.refactorAndValidateVkLink(link);
            if (validLink.equals("undefined")) {
                socialProfile.setSocialProfileType(socialProfileTypeService.getByTypeName("unknown"));
            } else {
                socialProfile.setSocialProfileType(socialProfileTypeService.getByTypeName("vk"));
            }
        } else if (link.contains("www.facebook.com") || link.contains("m.facebook.com")) {

            socialProfile.setSocialProfileType(socialProfileTypeService.getByTypeName("facebook"));
        } else {
            socialProfile.setSocialProfileType(socialProfileTypeService.getByTypeName("unknown"));
        }
        return socialProfile;
    }

    private Map<String, String> createMapFromClientData(String[] res) {
        Map<String, String> clientData = new HashMap<>();
        for (int i = 0; i < res.length; i++) {
            String name = res[i].substring(0, res[i].indexOf(":"));
            String value = res[i].substring(res[i].indexOf(":") + 1, res[i].length());
            clientData.put(name, value);
        }
        return clientData;
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
