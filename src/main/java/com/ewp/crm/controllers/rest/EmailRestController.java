package com.ewp.crm.controllers.rest;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.EmailTemplate;
import com.ewp.crm.models.User;
import com.ewp.crm.service.email.MailSendService;
import com.ewp.crm.service.impl.EmailTemplateServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
public class EmailRestController {
	private final MailSendService mailSendService;
	private final EmailTemplateServiceImpl emailTemplateService;
	private final ClientService clientService;
	private ImageConfig imageConfig;


	@Autowired
	public EmailRestController(MailSendService mailSendService, EmailTemplateServiceImpl emailTemplateService, ClientService clientService, ImageConfig imageConfig) {
		this.mailSendService = mailSendService;
		this.emailTemplateService = emailTemplateService;
		this.clientService = clientService;
		this.imageConfig = imageConfig;
	}

	@RequestMapping(value = "/rest/sendEmail", method = RequestMethod.POST)
	public ResponseEntity sendEmail(@RequestParam("clientId") Long clientId, @RequestParam("templateId") Long templateId) {
		Client client = clientService.getClientByID(clientId);
		String fullName = client.getName() + " " + client.getLastName();
		Map<String, String> params = new HashMap<>();
		params.put("fullName",fullName);
		mailSendService.prepareAndSend(client.getEmail(),params,emailTemplateService.get(templateId).getTemplateText(),
				"emailStringTemplate");
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/rest/sendCustomEmailTemplate", method = RequestMethod.POST)
	public ResponseEntity addSocialNetworkType(@RequestParam("clientId") Long clientId, @RequestParam("body") String body) {
		Client client = clientService.getClientByID(clientId);
		Map<String, String> params = new HashMap<>();
		params.put("bodyText",body);
		mailSendService.prepareAndSend(client.getEmail(),params,emailTemplateService.get(1L).getTemplateText(),
				"emailStringTemplate");
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = {"/admin/editEmailTemplate"}, method = RequestMethod.POST)
	public ResponseEntity editETemplate(@RequestParam("templateId") Long templateId, @RequestParam("templateText") String templateText) {
		EmailTemplate emailTemplate = emailTemplateService.get(templateId);
		emailTemplate.setTemplateText(templateText);
		emailTemplateService.update(emailTemplate);
		return ResponseEntity.ok().build();
	}

	@ResponseBody
	@RequestMapping(value = "/admin/savePicture", method = RequestMethod.POST)
	public ResponseEntity savePicture(@RequestParam("0") MultipartFile file) throws IOException {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		BufferedImage image = ImageIO.read(new BufferedInputStream(file.getInputStream()));
		String fileName =  file.getOriginalFilename().replaceFirst("[.][^.]+$", "") + ".png";
		File outputFile = new File(imageConfig.getPathForImages() + currentAdmin.getId() + "_" +fileName);
		ImageIO.write(image, "png", outputFile);
		return ResponseEntity.ok(currentAdmin.getId());
	}
	@ResponseBody
	@RequestMapping(value = "/admin/image/{file}", method = RequestMethod.GET)
	public byte[] getImage(@PathVariable("file") String file) throws IOException {
		Path fileLocation = Paths.get(imageConfig.getPathForImages() + file + ".png");
		return Files.readAllBytes(fileLocation);
	}

}
