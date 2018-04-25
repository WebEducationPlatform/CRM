package com.ewp.crm.component;

import com.ewp.crm.component.util.VKUtil;
import com.ewp.crm.exceptions.parse.ParseClientException;
import com.ewp.crm.exceptions.util.VKAccessTokenException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialNetwork;
import com.ewp.crm.models.Status;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@EnableScheduling
public class ScheduleTasks {

	private VKUtil vkUtil;

	private ClientService clientService;

	private StatusService statusService;

	private SocialNetworkService socialNetworkService;

	private SocialNetworkTypeService socialNetworkTypeService;


	private static Logger logger = LoggerFactory.getLogger(ScheduleTasks.class);

	@Autowired
	public ScheduleTasks(VKUtil vkUtil, ClientService clientService, StatusService statusService, SocialNetworkService socialNetworkService, SocialNetworkTypeService socialNetworkTypeService) {
		this.vkUtil = vkUtil;
		this.clientService = clientService;
		this.statusService = statusService;
		this.socialNetworkService = socialNetworkService;
		this.socialNetworkTypeService = socialNetworkTypeService;
	}

	private void addClient(Client newClient) {
		Status newClientsStatus = statusService.getFirstStatusForClient();
		newClient.setStatus(newClientsStatus);
		newClient.setState(Client.State.NEW);
		newClient.getSocialNetworks().get(0).setSocialNetworkType(socialNetworkTypeService.getByTypeName("vk"));
		clientService.addClient(newClient);
		logger.info("New client with id{} has added from VK", newClient.getId());
	}

	private void updateClient(Client newClient) {
		SocialNetwork socialNetwork = newClient.getSocialNetworks().get(0);
		Client updateClient = socialNetworkService.getSocialNetworkByLink(socialNetwork.getLink()).getClient();
		updateClient.setPhoneNumber(newClient.getPhoneNumber());
		updateClient.setEmail(newClient.getEmail());
		updateClient.setAge(newClient.getAge());
		updateClient.setSex(newClient.getSex());
		clientService.updateClient(updateClient);
		logger.info("Client with id{} has updated from VK", updateClient.getId());
	}

	@Scheduled(fixedRate = 10_000)
	private void handleRequestsFromVk() {
		try {
			Optional<List<String>> newMassages = vkUtil.getNewMassages();
			if (newMassages.isPresent()) {
				for (String message : newMassages.get()) {
					try {
						Client newClient = vkUtil.parseClientFromMessage(message);
						SocialNetwork socialNetwork = newClient.getSocialNetworks().get(0);
						if (Optional.ofNullable(socialNetworkService.getSocialNetworkByLink(socialNetwork.getLink())).isPresent()) {
							updateClient(newClient);
						} else {
							addClient(newClient);
						}
					} catch (ParseClientException e) {
						logger.error(e.getMessage());
					}
				}
			}
		} catch (VKAccessTokenException ex) {
			logger.error(ex.getMessage());
		}
	}

	@Scheduled(fixedRate = 10_000)
	private void handleRequestsFromVkCommunityMessages() {
		Optional<List<Long>> newUsers = vkUtil.getUsersIdFromCommunityMessages();
		if (newUsers.isPresent()) {
			for (Long id : newUsers.get()) {
				Optional<Client> newClient = vkUtil.getClientFromVkId(id);
				if (newClient.isPresent()) {
					SocialNetwork socialNetwork = newClient.get().getSocialNetworks().get(0);
					if (!(Optional.ofNullable(socialNetworkService.getSocialNetworkByLink(socialNetwork.getLink())).isPresent())) {
						addClient(newClient.get());
					}
				}
			}
		}
	}
}
