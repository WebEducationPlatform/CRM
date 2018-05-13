package com.ewp.crm.service.email;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.exceptions.email.MessageTemplateException;
import com.ewp.crm.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MailSendService {

	private static Logger logger = LoggerFactory.getLogger(MailSendService.class);

	private final JavaMailSender javaMailSender;
	private final TemplateEngine htmlTemplateEngine;
	private final ImageConfig imageConfig;
	private String emailLogin;


	@Autowired
	public MailSendService(JavaMailSender javaMailSender, @Qualifier("thymeleafTemplateEngine") TemplateEngine htmlTemplateEngine,
	                       ImageConfig imageConfig, Environment environment) {
		this.javaMailSender = javaMailSender;
		this.htmlTemplateEngine = htmlTemplateEngine;
		this.imageConfig = imageConfig;
		checkConfig(environment);
	}

	private void checkConfig(Environment environment) {
		this.emailLogin = environment.getProperty("spring.mail.username");
		String password = environment.getProperty("spring.mail.password");
		if (emailLogin == null || "".equals(emailLogin) || (password == null || "".equals(password))) {
			logger.error("Mail configs have not initialized. Check application.properties file");
			System.exit(-1);
		}
	}

	public void prepareAndSend(String recipient, Map<String, String> params, String templateText, String templateFile) {
		recipient = emailLogin; //Удалить после тестов!!
		final Context ctx = new Context();
		templateText = templateText.replaceAll("\n", "");
		ctx.setVariable("templateText", templateText);
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			mimeMessageHelper.setSubject("Java Mentor");
			mimeMessageHelper.setTo(recipient);
			mimeMessageHelper.setFrom(emailLogin);
			StringBuilder htmlContent =  new StringBuilder(htmlTemplateEngine.process(templateFile, ctx));
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
				mimeMessageHelper.addInline(matcher.group(), inputStreamSource,"image/jpeg");
			}
			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			logger.error("Can't send mail to {}", recipient);
			throw new MessageTemplateException(e.getMessage());
		}
	}

	@Async
	public void sendNotificationMessage(User userToNotify) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject("Оповещение из CRM");
		message.setText("Вас упомянули в комментариях под карточкой");
		message.setFrom(emailLogin);
		message.setTo(userToNotify.getEmail());
		javaMailSender.send(message);
	}
}
