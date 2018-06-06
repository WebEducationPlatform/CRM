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

@Component
public class IncomeStringToClient {

    private final SocialNetworkTypeService socialNetworkTypeService;

    private static final Logger logger = LoggerFactory.getLogger(IncomeStringToClient.class);

    @Autowired
    public IncomeStringToClient(SocialNetworkTypeService socialNetworkTypeService) {
        this.socialNetworkTypeService = socialNetworkTypeService;
    }

    //TODO Переделать как только Никита изменит форму.
	public Client convert(String income) {
		Client client = null;
		if (income != null && !income.isEmpty()) {
			String workString = prepareForm(income);
			if (income.contains("Форма: Начать обучение")) {
				client = parseClientFormOne(workString);
			} else if (income.contains("Форма: Месяц в подарок")) {
				client = parseClientFormOne(workString);
			} else if (income.contains("Форма: Остались вопросы")) {
				client = parseClientFormTwo(workString);
			} else if (income.contains("Форма: Java Test")) {
				client = parseClientFormFour(workString);
			} else {
				logger.error("The incoming email does not match any of the templates!!!");
			}
		}
		return client;
	}

	private static String prepareForm(String text) {
		if (text.contains("<br>")) {
			text = text.replaceAll("<br>", "");
		}
		if (text.contains("Имя: ")) {
			return text.substring(text.indexOf("Имя: "));
		}
		return text.substring(text.indexOf("Name "), text.length()).replaceAll("(\r\n|\n)", "");
	}

	private Client parseClientFormOne(String form) {
		Client client = new Client();
		String[] result = form.split(":\\s");
		setClientName(client, result[1].substring(0, result[1].indexOf(" Соц ")));
		client.setSocialNetworks(Collections.singletonList(getSocialNetwork(result[2].substring(0, result[2].indexOf(" Телефон")))));
		client.setPhoneNumber(result[3].substring(0, result[3].indexOf(" Страна")));
		client.setCountry(result[4].substring(0, result[4].indexOf(" Город")));
		client.setCity(result[5].substring(0, result[5].indexOf(" Email")));
		if (form.contains("Согласен")) {
			client.setEmail(result[6].substring(0, result[6].indexOf(" Согласен")));
		} else {
			client.setEmail(result[6]);
			client.setClientDescriptionComment("На пробные 3 дня");
		}
		return client;
	}

	private Client parseClientFormTwo(String form) {
		Client client = new Client();
		String[] result = form.split(":\\s");
		setClientName(client, result[1].substring(0, result[1].indexOf(" Social")));
		client.setSocialNetworks(Collections.singletonList(getSocialNetwork(result[2].substring(0, result[2].indexOf(" Phone")))));
		client.setPhoneNumber(result[3].substring(0, result[3].indexOf(" Email")));
		client.setEmail(result[4].substring(0, result[4].indexOf(" Vopros")));
		client.setClientDescriptionComment(result[5]);
		return client;
	}

	private Client parseClientFormFour(String form) {
		Client client = new Client();
		String[] result = form.split(":\\s");
		setClientName(client, result[1].substring(0, result[1].indexOf(" Social")));
		client.setSocialNetworks(Collections.singletonList(getSocialNetwork(result[2].substring(0, result[2].indexOf(" Phone")))));
		client.setPhoneNumber(result[3].substring(0, result[3].indexOf(" City")));
		client.setCountry(result[4].substring(0, result[4].indexOf(" Email")));
		client.setEmail(result[5]);
		client.setClientDescriptionComment("Проходил Тест");
		return client;
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
