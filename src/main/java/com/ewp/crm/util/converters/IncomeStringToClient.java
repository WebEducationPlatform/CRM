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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class IncomeStringToClient {

    private final VKService vkService;
    private final FacebookService facebookService;
    private static final Logger logger = LoggerFactory.getLogger(IncomeStringToClient.class);
    private Environment env;

    public static final String UNKNOWN_NAME_DEFAULT = "UNKNOWN?";
    private static final String EMPTY = org.apache.commons.lang3.StringUtils.EMPTY;
    private static final String SPLITTER = "<BR>";

    private static final List<String> CORRECT_TEMPLATES_TO_PARSE = Arrays.asList(
            "Начать обучение",
            "Месяц в подарок",
            "Оплата всей программы",
            "Остались вопросы",
            "Задать вопрос",
            "Java Test",
            "javalearn",
            "javabootcamp.ru",
            "jslearn.online"
    );

    private enum KEYS {
        NAME ("NAME"),
        LAST_NAME ("LAST_NAME"),
        FIRST_NAME ("FIRST_NAME"),
        EMAIL ("EMAIL"),
        PHONE ("PHONE"),
        CITY ("CITY"),
        COUNTRY ("COUNTRY"),
        SOCIAL ("SOCIAL"),
        FORM ("FORM"),
        PAGE ("PAGE"),
        QUESTION ("QUESTION"),
        REQUEST ("REQUEST");

        private String value;

        KEYS(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static List<String> getValues() {
            return Arrays.stream(values()).map(KEYS::getValue).collect(Collectors.toList());
        }
    }

    @Autowired
    public IncomeStringToClient(VKService vkService, Environment env,
                                FacebookService facebookService) {
        this.vkService = vkService;
        this.env = env;
        this.facebookService = facebookService;
    }

    public Optional<Client> convert(String income) {
        Client client = null;
        if (income != null && !income.isEmpty()) {
            logger.debug("Start a conversion of income data '{}'", income);
            String workString = prepareForm(income);
            if (CORRECT_TEMPLATES_TO_PARSE.stream().anyMatch(income::contains)) {
                client = parseClient(workString);
                if (income.contains("javalearn")) {
                    client.setClientDescriptionComment(env.getProperty("messaging.client.description.java-learn-link"));
                }
                if (income.contains("jslearn.online")) {
                    client.setClientDescriptionComment(env.getProperty("messaging.client.description.js-learn-link"));
                }
            } else {
                logger.error("The incoming email does not match any of the templates!!!");
                return Optional.empty();
            }
            vkService.fillClientFromProfileVK(client);
        }
        return Optional.ofNullable(client);
    }

    private static String prepareForm(String text) {
        return text
                .replaceAll("</?a[^>]*>|</?div[^>]*>|</?b[^r>]*>|</?p[^>]*>|\\r|\\n", EMPTY)
                .replaceAll("(<br[^>]*>)+", SPLITTER)
                .replaceAll("(N|n)(A|a)(M|m)(E|e)[^:]*:", KEYS.NAME + ":")
                .replaceAll("(И|и)(М|м)(Я|я)[^:]*:", KEYS.NAME + ":")
                .replaceAll("(P|p)(H|h)(O|o)(N|n)(E|e)[^:]*:", KEYS.PHONE + ":")
                .replaceAll("(Т|т)(Е|е)(Л|л)(Е|е)(Ф|ф)(О|о)(Н|н)[^:]*:", KEYS.PHONE + ":")
                .replaceAll("(Г|г)(О|о)(Р|р)(О|о)(Д|д)[^:]*:", KEYS.CITY + ":")
                .replaceAll("(C|c)(I|i)(T|t)(Y|y)[^6:]*:", KEYS.CITY + ":")
                .replaceAll("(C|c)(I|i)(T|t)(Y|y)[^6:]*6:", KEYS.COUNTRY + ":")
                .replaceAll("(С|с)(Т|т)(Р|р)(А|а)(Н|н)(А|а)[^:]*:", KEYS.COUNTRY + ":")
                .replaceAll("(С|с)(Т|т)(Р|р)(А|а)(Н|н)(И|и)(Ц|ц)(А|а)[^:]*:", KEYS.PAGE + ":")
                .replaceAll("(E|e)-?(M|m)(A|a)(I|i)(L|l)[^:]*:", KEYS.EMAIL + ":")
                .replaceAll("(S|s)(O|o)(C|c)(I|i)(A|a)(L|l)[^:]*:", KEYS.SOCIAL + ":")
                .replaceAll("(С|с)(О|о)(Ц|ц)[^:]*(С|с)(Е|е)(Т|т)(Ь|ь)[^:]*:", KEYS.SOCIAL + ":")
                .replaceAll("(Ф|ф)(О|о)(Р|р)(М|м)(А|а)[^:]*:", KEYS.FORM + ":")
                .replaceAll("(З|з)(А|а)(П|п)(Р|р)(О|о)(С|с)[^:]*:", KEYS.REQUEST + ":")
                .replaceAll("(V|v)(O|o)(P|p)(R|r)(O|o)(S|s)[^:]*:", KEYS.QUESTION + ":")
                .replaceAll("(В|в)(О|о)(П|п)(Р|р)(О|о)(С|с)[^:]*:", KEYS.QUESTION + ":")
                .replaceAll("(S|s)(E|e)(N|n)(T|t)[^:]*(P|p)(A|a)(G|g)(E|e)[^:]*:", KEYS.REQUEST + ":")
                .replaceAll(" ", "~");
    }

    private Client parseClient(String form) {
        logger.debug("Start parsing client from form: '{}'", form);
        String[] arrayFromString = form.split(SPLITTER);
        Map<KEYS, String> clientData = createMapFromClientData(arrayFromString);
        Map<KEYS, String> clientName = splitClientName(clientData.getOrDefault(KEYS.NAME, UNKNOWN_NAME_DEFAULT).replaceAll("~", " "));
        Client.Builder clientBuilder = new Client.Builder(clientName.getOrDefault(KEYS.FIRST_NAME, UNKNOWN_NAME_DEFAULT).replace("~", EMPTY));
        if (clientData.containsKey(KEYS.EMAIL)) {
            clientBuilder.email(clientData.get(KEYS.EMAIL).replace("~", EMPTY));
        }
        if (clientData.containsKey(KEYS.PHONE)) {
            clientBuilder.phone(clientData.get(KEYS.PHONE).replace("~", EMPTY));
        }
        clientBuilder.lastName(clientName.getOrDefault(KEYS.LAST_NAME, EMPTY).replace("~", EMPTY));
        if (clientData.containsKey(KEYS.COUNTRY)) {
            clientBuilder.country(clientData.get(KEYS.COUNTRY).replace("~", EMPTY));
        }
        if (clientData.containsKey(KEYS.CITY)) {
            clientBuilder.city(clientData.get(KEYS.CITY).replace("~", EMPTY));
        }
        Client client = clientBuilder.build();
        if (clientData.containsKey(KEYS.FORM)) {
            client.setClientDescriptionComment(clientData.get(KEYS.FORM).replace("~", " "));
        }
        if (clientData.containsKey(KEYS.QUESTION)) {
            client.setClientDescriptionComment("Вопрос: " + clientData.get(KEYS.QUESTION).replace("~", " "));
        }
        if (clientData.containsKey(KEYS.REQUEST)) {
            client.setRequestFrom(clientData.get(KEYS.REQUEST).replace("~", EMPTY));
        }
        checkSocialNetworks(client, clientData);
        logger.debug("Client '{}' parsing complete", client.toString());
        return client;
    }

    private void checkSocialNetworks(Client client, Map<KEYS, String> clientData) {
        String link = EMPTY;
        if (clientData.containsKey(KEYS.SOCIAL)) {
            link = clientData.get(KEYS.SOCIAL);
        }
        link = link.replaceAll("~", EMPTY);
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

    private Map<KEYS, String> createMapFromClientData(String[] res) {
        Map<KEYS, String> clientData = new HashMap<>();
        for (String re : res) {
            if (re.contains(":")) {
                String name = re.substring(0, re.indexOf(":"));
                String value = re.substring(re.indexOf(":") + 1);
                if (KEYS.getValues().contains(name)) {
                    clientData.put(KEYS.valueOf(name), value);
                }
            }
        }
        return clientData;
    }

    private Map<KEYS, String> splitClientName(String fullName) {
        Map<KEYS, String> clientNameMap = new HashMap<>();
        if (StringUtils.countOccurrencesOf(fullName, " ") == 1) {
            String[] full = fullName.split(" ");
            clientNameMap.put(KEYS.FIRST_NAME, full[0]);
            clientNameMap.put(KEYS.LAST_NAME, full[1]);
        } else {
            clientNameMap.put(KEYS.FIRST_NAME, fullName);
        }
        return clientNameMap;
    }
}