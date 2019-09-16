package com.ewp.crm.controllers;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.UserRoutes;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.dto.ClientDto;
import com.ewp.crm.models.dto.HrDtoForBoard;
import com.ewp.crm.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/hr")
@PreAuthorize("hasAnyAuthority('OWNER','HR','MENTOR')")
@PropertySource("file:./slackbot.properties")
public class HrController {

	@Value("${slackbot.domain}")
	private String slackBotDomain;
	@Value("${slackbot.access.protocol}")
	private String slackBotAccessProtocol;

	private final StatusService statusService;
	private final ClientService clientService;
	private final UserService userService;
	private final MessageTemplateService messageTemplateService;
	private final ProjectPropertiesService propertiesService;
	private final StudentStatusService studentStatus;
	private final RoleService roleService;

	@Autowired
	public HrController(StatusService statusService,
	                    ClientService clientService,
	                    UserService userService,
	                    MessageTemplateService messageTemplateService,
	                    ProjectPropertiesService propertiesService,
	                    StudentStatusService studentStatus,
	                    RoleService roleService) {
		this.statusService = statusService;
		this.clientService = clientService;
		this.userService = userService;
		this.messageTemplateService = messageTemplateService;
		this.propertiesService = propertiesService;
		this.studentStatus = studentStatus;
		this.roleService = roleService;
	}

	@GetMapping("/students")
	public ModelAndView showAllStudents() {
		HashMap<String, List<ClientDto>> lostStudentsInStatus = new HashMap<>();
		List<Status> statuses = statusService.getAllStatusesForStudents();
		statuses.forEach(status -> {
			StringBuilder url = new StringBuilder(slackBotAccessProtocol + slackBotDomain + "/student/get/is_lost?emails=");
			status.getClients().forEach(client -> {
				if (client.getEmail().isPresent()) {
					String email = client.getEmail().get();
					url.append(email).append(",");
				}
			});
			ResponseEntity<String[]> lostStudentsEmails = new RestTemplate().getForEntity(url.toString(), String[].class);
			List<String> lostStudentsEmailsList = Arrays.asList(Objects.requireNonNull(lostStudentsEmails.getBody()));
			if (!lostStudentsEmailsList.isEmpty()) {
				List<ClientDto.ClientTransformer> clientList = clientService.getClientsDtoByEmails(lostStudentsEmailsList);
				List<ClientDto> clients = new ArrayList<>();
				clientList.forEach(clientTransformer ->
						clients.add(new ClientDto(
								clientTransformer.getClient_id(), clientTransformer.getFirst_name(), clientTransformer.getLast_name(),
								"", clientTransformer.getClient_email())));
				//lostStudentsEmailsList.forEach(email -> clientList.add(emailClientHashMap.get(email)));
				lostStudentsInStatus.put(status.getName(), clients);
			}
		});
		ModelAndView modelAndView = new ModelAndView("main-client-table-hr");
		SocialProfile socialProfile = new SocialProfile();
		modelAndView.addObject("slackBotDomain", slackBotDomain);
		modelAndView.addObject("slackBotAccessProtocol", slackBotAccessProtocol);
		modelAndView.addObject("statuses", lostStudentsInStatus);
		modelAndView.addObject("projectProperties", propertiesService.get());
		modelAndView.addObject("users", userService.getAll());
		modelAndView.addObject("socialNetworkTypes", socialProfile.getAllSocialNetworkTypes());
		modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
		modelAndView.addObject("studentStatuses", studentStatus.getAll());
		return modelAndView;
	}

	@GetMapping("/managers")
	public ModelAndView showAllManagers() {
		final ModelAndView modelAndView = new ModelAndView("hr-table");

        modelAndView.addObject("hrManagers",
                userService.getByRole(roleService.getRoleByName("HR"))
                        .stream().map(HrDtoForBoard::new)
                        .collect(Collectors.toList()));
        modelAndView.addObject("userRoutesTypes", UserRoutes.UserRouteType.values());

        return modelAndView;
    }

    @PostMapping("/refresh")
	public ResponseEntity refreshInfoInBot() {
	    StringBuilder url = new StringBuilder(slackBotAccessProtocol + slackBotDomain + "/update-channels");
	    ResponseEntity<String> fromBot = new RestTemplate().getForEntity(url.toString(), String.class);
		return fromBot;
    }

}
