package com.ewp.crm.util.converters;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.service.interfaces.FacebookService;
import com.ewp.crm.service.interfaces.VKService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class IncomeStringToClient {

    private final VKService vkService;
    private final FacebookService facebookService;
    private static final Logger logger = LoggerFactory.getLogger(IncomeStringToClient.class);
    private Environment env;

    private static final String UNKNOWN_NAME_DEFAULT = "UNKNOWN?";
    private static final String EMPTY = org.apache.commons.lang3.StringUtils.EMPTY;

    @Autowired
    public IncomeStringToClient(VKService vkService, Environment env,
                                FacebookService facebookService) {
        this.vkService = vkService;
        this.env = env;
        this.facebookService = facebookService;
    }

    public Optional<Client> convert(String income) {
        Client client = null;
        logger.info("Start of conversion");
        if (income != null && !income.isEmpty()) {
            String workString = prepareForm(income);
            if (income.contains("Начать обучение")) {
                client = parseClientFormOne(workString);
            } else if (income.contains("Месяц в подарок")) {
                client = parseClientFormOne(workString);
            } else if (income.contains("Оплата всей программы")) {
                client = parseClientFormFive(workString);
            } else if (income.contains("Остались вопросы")) {
                client = parseClientFormTwo(workString);
            } else if (income.contains("Задать вопрос")) {
                client = parseClientFormThree(workString);
            } else if (income.contains("Java Test")) {
                client = parseClientFormFour(workString);
            } else if (income.contains("javalearn")) {
                client = parseClientFormJavaLearn(workString);
            } else {
                logger.error("The incoming email does not match any of the templates!!!");
                return Optional.empty();
            }
            vkService.fillClientFromProfileVK(client);
        }
        return Optional.ofNullable(client);
    }

    private static String prepareForm(String text) {
        if (text.contains("Страница")) {
            text = text.substring(text.indexOf("Страница:"));
        }
        return text.replaceAll("<b>|</b>|<p>|</p>|(\\r\\n|\\n)", EMPTY);
    }

    private Client parseClientFormOne(String form) {
        logger.info("Parsing FormOne...");
        String removeExtraCharacters = form.substring(form.indexOf("Страница"), form.length())
                .replaceAll(" ", "~")
                .replaceAll("Name~5", "Name")
                .replaceAll("Email~5", "Email")
                .replaceAll("Соц~~сеть", "Соцсеть");
        String[] createArrayFromString = removeExtraCharacters.split("<br~/>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        Map<String, String> clientName = splitClientName(clientData.getOrDefault("Name", UNKNOWN_NAME_DEFAULT).replaceAll("~", " "));
        Client.Builder clientBuilder = new Client.Builder(clientName.getOrDefault("firstName", UNKNOWN_NAME_DEFAULT));
        if (clientData.containsKey("Email")) {
            clientBuilder.email(clientData.get("Email").replace("~", EMPTY));
        }
        if (clientData.containsKey("Телефон")) {
            clientBuilder.phone(clientData.get("Телефон").replace("~", EMPTY));
        }
        clientBuilder.lastName(clientName.getOrDefault("lastName", EMPTY));
        if (clientData.containsKey("Страна")) {
            clientBuilder.country(clientData.get("Страна").replace("~", EMPTY));
        }
        if (clientData.containsKey("Город")) {
            clientBuilder.city(clientData.get("Город").replace("~", EMPTY));
        }
        Client client = clientBuilder.build();
        client.setClientDescriptionComment(clientData.getOrDefault("Форма", UNKNOWN_NAME_DEFAULT).replace("~", " "));
        if (clientData.containsKey("Запрос")) {
            client.setRequestFrom(clientData.get("Запрос").replace("~", EMPTY));
        }
        checkSocialNetworks(client, clientData);
        logger.info("FormOne parsing finished");
        return client;
    }

    private Client parseClientFormTwo(String form) {
        logger.info("Parsing FormTwo...");
        String removeExtraCharacters = form.substring(form.indexOf("Форма"), form.length())
                .replaceAll(" ", "~")
                .replaceAll("Name~3", "Name");
        String[] createArrayFromString = removeExtraCharacters.split("<br~/>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        Map<String, String> clientName = splitClientName(clientData.getOrDefault("Name", UNKNOWN_NAME_DEFAULT).replaceAll("~", " "));
        Client.Builder clientBuilder = new Client.Builder(clientName.getOrDefault("firstName", UNKNOWN_NAME_DEFAULT));
        if (clientData.containsKey("Email")) {
            clientBuilder.email(clientData.get("Email").replace("~", EMPTY));
        }
        if (clientData.containsKey("Phone")) {
            clientBuilder.phone(clientData.get("Phone").replace("~", EMPTY));
        }
        Client client = clientBuilder.lastName(clientName.getOrDefault("lastName", EMPTY)).build();
        String question = clientData.get("Vopros");
        String formattedQuestion = question.replaceAll("~", " ");
        client.setClientDescriptionComment("Вопрос: " + formattedQuestion);
        checkSocialNetworks(client, clientData);
        logger.info("FormTwo parsing finished");
        return client;
    }

    private Client parseClientFormThree(String form) {
        logger.info("Parsing FormThree...");
        String removeExtraCharacters = form.substring(form.indexOf("Форма"), form.length())
                .replaceAll(" ", "~")
                .replaceAll("Name~3", "Name")
                .replaceAll("Phone~6", "Phone")
                .replaceAll("Email~2", "Email")
                .replaceAll("Social~2", "Social");
        String[] createArrayFromString = removeExtraCharacters.split("<br~/>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        Map<String, String> clientName = splitClientName(clientData.getOrDefault("Name", UNKNOWN_NAME_DEFAULT).replaceAll("~", " "));
        Client.Builder clientBuilder = new Client.Builder(clientName.getOrDefault("firstName", UNKNOWN_NAME_DEFAULT));
        if (clientData.containsKey("Email")) {
            clientBuilder.email(clientData.get("Email").replace("~", EMPTY));
        }
        if (clientData.containsKey("Phone")) {
            clientBuilder.phone(clientData.get("Phone").replace("~", EMPTY));
        }
        Client client = clientBuilder.lastName(clientName.getOrDefault("lastName", EMPTY)).build();
        String question = clientData.get("Вопрос");
        String formattedQuestion = question.replaceAll("~", " ");
        client.setClientDescriptionComment("Вопрос: " + formattedQuestion);
        checkSocialNetworks(client, clientData);
        logger.info("Form Three parsing finished");
        return client;
    }

    private Client parseClientFormFour(String form) {
        logger.info("Parsing FormFour...");
        String removeExtraCharacters = form.substring(form.indexOf("Страница"), form.length())
                .replaceAll(" ", "~")
                .replaceAll("Email~2", "Email")
                .replaceAll("Phone~6", "Phone")
                .replaceAll("City~6", "Country");
        String[] createArrayFromString = removeExtraCharacters.split("<br~/>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        Map<String, String> clientName = splitClientName(clientData.getOrDefault("Имя", UNKNOWN_NAME_DEFAULT).replaceAll("~", " "));
        Client.Builder clientBuilder = new Client.Builder(clientName.getOrDefault("firstName", UNKNOWN_NAME_DEFAULT));
        if (clientData.containsKey("Email")) {
            clientBuilder.email(clientData.get("Email").replace("~", EMPTY));
        }
        if (clientData.containsKey("Phone")) {
            clientBuilder.phone(clientData.get("Phone").replace("~", EMPTY));
        }
        clientBuilder.lastName(clientName.getOrDefault("lastName", EMPTY));
        if (clientData.containsKey("Country")) {
            clientBuilder.country(clientData.get("Country").replace("~", EMPTY));
        }
        Client client = clientBuilder.build();
        client.setClientDescriptionComment(clientData.get("Форма").replace("~", " "));
        if (clientData.containsKey("Запрос")) {
            client.setRequestFrom(clientData.get("Запрос").replace("~", EMPTY));
        }
        checkSocialNetworks(client, clientData);
        logger.info("Form Four parsing finished");
        return client;
    }

    private Client parseClientFormFive(String form) {
        logger.info("Parsing FormFive...");
        String removeExtraCharacters = form.substring(form.indexOf("Страница"), form.length())
                .replaceAll(" ", "~")
                .replaceAll("Name~4", "Name")
                .replaceAll("Email~4", "Email")
                .replaceAll("Соц~~сеть", "Соцсеть");
        String[] createArrayFromString = removeExtraCharacters.split("<br~/>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        Map<String, String> clientName = splitClientName(clientData.getOrDefault("Name", UNKNOWN_NAME_DEFAULT).replaceAll("~", " "));
        Client.Builder clientBuilder = new Client.Builder(clientName.getOrDefault("firstName", UNKNOWN_NAME_DEFAULT));
        if (clientData.containsKey("Email")) {
            clientBuilder.email(clientData.get("Email").replace("~", EMPTY));
        }
        if (clientData.containsKey("Телефон")) {
            clientBuilder.phone(clientData.get("Телефон").replace("~", EMPTY));
        }
        clientBuilder.lastName(clientName.getOrDefault("lastName", EMPTY));
        if (clientData.containsKey("Страна")) {
            clientBuilder.country(clientData.get("Страна").replace("~", EMPTY));
        }
        if (clientData.containsKey("Город")) {
            clientBuilder.city(clientData.get("Город").replace("~", EMPTY));
        }
        Client client = clientBuilder.build();
        client.setClientDescriptionComment(clientData.getOrDefault("Форма", UNKNOWN_NAME_DEFAULT).replace("~", " "));
        if (clientData.containsKey("Запрос")) {
            client.setRequestFrom(clientData.get("Запрос").replace("~", EMPTY));
        }
        checkSocialNetworks(client, clientData);
        logger.info("FormOne parsing finished");
        return client;
    }

    private Client parseClientFormJavaLearn(String form) {
        logger.info("Parsing JavaLearnForm...");
        String removeExtraCharacters = form.substring(form.indexOf("Request"), form.length())
                .replaceAll("<a href=\"", EMPTY)
                .replaceAll("\">", "<br>")
                .replaceAll("Name: ", "Name:")
                .replaceAll(" ", "~");
        String[] createArrayFromString = removeExtraCharacters.split("<br>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        Map<String, String> clientName = splitClientName(clientData.getOrDefault("Name", UNKNOWN_NAME_DEFAULT).replaceAll("~", " "));
        Client.Builder clientBuilder = new Client.Builder(clientName.getOrDefault("firstName", UNKNOWN_NAME_DEFAULT));
        if (clientData.containsKey("Email")) {
            clientBuilder.email(clientData.get("Email").replace("~", EMPTY).replace("mailto:", EMPTY).replace("\"target=\"_blank", EMPTY));
        }
        if (clientData.containsKey("Phone")) {
            clientBuilder.phone(clientData.get("Phone").replace("~", EMPTY));
        }
        Client client = clientBuilder.lastName(clientName.getOrDefault("lastName", EMPTY)).build();
        client.setClientDescriptionComment(env.getProperty("messaging.client.description.java-learn-link"));
        checkSocialNetworks(client, clientData);
        logger.info("JavaLearnForm parsing finished");
        return client;
    }

    private void checkSocialNetworks(Client client, Map<String, String> clientData) {
        String link = EMPTY;
        if (clientData.containsKey("Social")) {
            link = clientData.get("Social");
        } else if (clientData.containsKey("social")) {
            link = clientData.get("social");
        } else if (clientData.containsKey("Соцсеть")) {
            link = clientData.get("Соцсеть");
        }
        link = link.replaceAll("~", EMPTY).replace("\"target=\"_blank", EMPTY);
        if (!link.isEmpty()) {
            SocialProfile currentSocialProfile = getSocialNetwork(link);
            if (currentSocialProfile.getSocialNetworkType().getName().equals("unknown")) {
                client.setComment(String.format(env.getProperty("messaging.client.socials.invalid-link"), link));
                logger.warn("Unknown social network '" + link + "'");
            } else {
                client.setSocialProfiles(Collections.singletonList(currentSocialProfile));
            }
        }
    }

    private SocialProfile getSocialNetwork(String link) {
        SocialProfile socialProfile = new SocialProfile();
        if (link.contains("vk.com") || link.contains("m.vk.com")) {
            String validLink = vkService.refactorAndValidateVkLink(link);
            if (validLink.equals("undefined")) {
                socialProfile.setSocialId(link);
                socialProfile.setSocialNetworkType(SocialNetworkType.UNKNOWN);
            } else {
                Optional<String> socialId = vkService.getIdFromLink(link);
                if (socialId.isPresent()) {
                    socialProfile.setSocialId(socialId.get());
                    socialProfile.setSocialNetworkType(SocialNetworkType.VK);
                }
            }
        } else if (link.contains("facebook.com") || link.contains("fb.com")) {
            Optional<String> socialId = facebookService.getIdFromLink(link);
            if (socialId.isPresent()) {
                socialProfile.setSocialId(socialId.get());
                socialProfile.setSocialNetworkType(SocialNetworkType.FACEBOOK);
            }
        } else {
            socialProfile.setSocialId(link);
            socialProfile.setSocialNetworkType(SocialNetworkType.UNKNOWN);
        }
        return socialProfile;
    }

    private Map<String, String> createMapFromClientData(String[] res) {
        Map<String, String> clientData = new HashMap<>();
        for (String re : res) {
            if (re.contains(":")) {
                String name = re.substring(0, re.indexOf(":"));
                String value = re.substring(re.indexOf(":") + 1);
                clientData.put(name, value);
            }
        }
        return clientData;
    }

    private Map<String, String> splitClientName(String fullName) {
        Map<String, String> clientNameMap = new HashMap<>();
        if (StringUtils.countOccurrencesOf(fullName, " ") == 1) {
            String[] full = fullName.split(" ");
            clientNameMap.put("firstName", full[0]);
            clientNameMap.put("lastName", full[1]);
        } else {
            clientNameMap.put("firstName", fullName);
        }
        return clientNameMap;
    }
}