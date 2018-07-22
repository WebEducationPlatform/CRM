package com.ewp.crm.service.email;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.exceptions.email.MessageTemplateException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.Message;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.MessageService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.validation.constraints.Null;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@EnableAsync
public class MailSendService {

	private static Logger logger = LoggerFactory.getLogger(MailSendService.class);

	private final JavaMailSender javaMailSender;
	private final TemplateEngine htmlTemplateEngine;
	private final ImageConfig imageConfig;
	private final ClientService clientService;
	private final ClientHistoryService clientHistoryService;
	private final MessageService messageService;
	private final MessageTemplateService messageTemplateService;
	private String emailLogin;


	@Autowired
	public MailSendService(JavaMailSender javaMailSender, @Qualifier("thymeleafTemplateEngine") TemplateEngine htmlTemplateEngine,
	                       ImageConfig imageConfig, Environment environment, ClientService clientService, ClientHistoryService clientHistoryService, MessageService messageService, MessageTemplateService messageTemplateService) {
		this.javaMailSender = javaMailSender;
		this.htmlTemplateEngine = htmlTemplateEngine;
		this.imageConfig = imageConfig;
		this.clientService = clientService;
		this.clientHistoryService = clientHistoryService;
		this.messageService = messageService;
		this.messageTemplateService = messageTemplateService;
		checkConfig(environment);
	}

	private void checkConfig(Environment environment) {
		try {
			this.emailLogin = environment.getRequiredProperty("spring.mail.username");
			String password = environment.getRequiredProperty("spring.mail.password");
			if (emailLogin.isEmpty() || password.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (IllegalStateException | NullPointerException e) {
			logger.error("Mail configs have not initialized. Check application.properties file");
			System.exit(-1);
		}
	}

	public void prepareAndSend(Long clientId, Long templateId, String body, User principal) {
		String templateFile = "emailStringTemplate";
		Client client = clientService.getClientByID(clientId);
		String recipient = client.getEmail();
		String fullName = client.getName() + " " + client.getLastName();
		Map<String, String> params = new HashMap<>();
		//TODO в конфиг
		params.put("%fullName%", fullName);
		params.put("%bodyText%", body);
		params.put("%dateOfSkypeCall%", body);
		final Context ctx = new Context();
		String templateText = messageTemplateService.get(templateId).getTemplateText();
		templateText = templateText.replaceAll("\n", "");
		ctx.setVariable("templateText", templateText);
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			mimeMessageHelper.setSubject("Java Mentor");
			mimeMessageHelper.setTo(recipient);
			mimeMessageHelper.setFrom(emailLogin);
			StringBuilder htmlContent = new StringBuilder(htmlTemplateEngine.process(templateFile, ctx));
			for (Map.Entry<String, String> entry : params.entrySet()) {
				htmlContent = new StringBuilder(htmlContent.toString().replaceAll(entry.getKey(), entry.getValue()));
			}
			mimeMessageHelper.setText(htmlContent.toString(), true);
			Pattern pattern = Pattern.compile("(?<=cid:)\\S*(?=\\|)");
			//inline картинки присоединяются к тексту сообщения с помочью метода addInline(в какое место вставлять, что вставлять).
			//Добавлять нужно в тег data-th-src="|cid:XXX|" где XXX - имя загружаемого файла
			//Регулярка находит все нужные теги, а потом циклом добавляем туда нужные файлы.
			Matcher matcher = pattern.matcher(templateText);
			while (matcher.find()) {
				InputStreamSource inputStreamSource = new FileSystemResource(new File(imageConfig.getPathForImages() + matcher.group() + ".png"));
				mimeMessageHelper.addInline(matcher.group(), inputStreamSource, "image/jpeg");
			}
			javaMailSender.send(mimeMessage);
			Client clientEmail = clientService.getClientByEmail(recipient);
			Message message = messageService.addMessage(Message.Type.EMAIL, htmlContent.toString());
			client.addHistory(clientHistoryService.createHistory(principal, clientEmail, message));
			clientService.updateClient(client);
		} catch (Exception e) {
			logger.error("Can't send mail to {}", recipient);
			throw new MessageTemplateException(e.getMessage());
		}
	}


	@Async
	public void sendNotificationMessage(User userToNotify, String notificationMessage) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject("Оповещение из CRM");
		message.setText(notificationMessage);
		message.setFrom(emailLogin);
		message.setTo(userToNotify.getEmail());
		javaMailSender.send(message);
	}
}
