package com.ewp.crm.controllers.rest;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.exceptions.email.MessageTemplateException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.User;
import com.ewp.crm.service.email.MailSendService;
import com.ewp.crm.service.impl.MessageTemplateServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
	private final MessageTemplateServiceImpl messageTemplateService;
	private final ClientService clientService;
	private final ImageConfig imageConfig;


	@Autowired
	public EmailRestController(MailSendService mailSendService, MessageTemplateServiceImpl MessageTemplateService, ClientService clientService, ImageConfig imageConfig) {
		this.mailSendService = mailSendService;
		this.messageTemplateService = MessageTemplateService;
		this.clientService = clientService;
		this.imageConfig = imageConfig;
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "/rest/sendEmail", method = RequestMethod.POST)
	public ResponseEntity sendEmail(@RequestParam("clientId") Long clientId, @RequestParam("templateId") Long templateId,
	                                @RequestParam(value = "body",required = false) String body) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		mailSendService.prepareAndSend(clientId, templateId, body, principal);
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
	@RequestMapping(value = {"/admin/editMessageTemplate"}, method = RequestMethod.POST)
	public ResponseEntity editETemplate(@RequestParam("templateId") Long templateId, @RequestParam("templateText") String templateText,
	                                    @RequestParam String otherTemplateText) {
		//TODO Убрать хардкод
		if(templateText.contains("%bodyText%") ^ otherTemplateText.contains("%bodyText%")) {
			throw new MessageTemplateException("%bodyText% должен присутствовать/остутствовать на обоих типах сообщения");
		}
		MessageTemplate MessageTemplate = messageTemplateService.get(templateId);
		MessageTemplate.setTemplateText(templateText);
		MessageTemplate.setOtherText(otherTemplateText);
		messageTemplateService.update(MessageTemplate);
		return ResponseEntity.ok().build();
	}

	@ResponseBody
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
	@RequestMapping(value = "/admin/savePicture", method = RequestMethod.POST)
	public ResponseEntity savePicture(@RequestParam("0") MultipartFile file) throws IOException {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		BufferedImage image = ImageIO.read(new BufferedInputStream(file.getInputStream()));
		String fileName = file.getOriginalFilename().replaceFirst("[.][^.]+$", "") + ".png";
		File outputFile = new File(imageConfig.getPathForImages() + currentAdmin.getId() + "_" + fileName);
		ImageIO.write(image, "png", outputFile);
		return ResponseEntity.ok(currentAdmin.getId());
	}

	@ResponseBody
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
	@RequestMapping(value = "/admin/image/{file}", method = RequestMethod.GET)
	public byte[] getImage(@PathVariable("file") String file) throws IOException {
		Path fileLocation = Paths.get(imageConfig.getPathForImages() + file + ".png");
		return Files.readAllBytes(fileLocation);
	}

}
