package com.ewp.crm.service.email;

import com.ewp.crm.exceptions.email.EmailTemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.persistence.Entity;
import java.util.Map;

@Service
public class MailSendService {
	private JavaMailSender javaMailSender;
	private TemplateEngine htmlTemplateEngine;

	@Autowired
	public MailSendService(JavaMailSender javaMailSender, @Qualifier("thymeleafTemplateEngine") TemplateEngine htmlTemplateEngine) {
		this.javaMailSender = javaMailSender;
		this.htmlTemplateEngine = htmlTemplateEngine;
	}

	public void prepareAndSend(String recipient, Map<String, String> params, String templateText, String templateFile) {
		final Context ctx = new Context();
		templateText = templateText.replaceAll("\n","");
		ctx.setVariable("templateText", templateText);
		for (Map.Entry<String, String> entry : params.entrySet()){
			ctx.setVariable(entry.getKey(),entry.getValue());
		}
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
			mimeMessageHelper.setSubject("Java Mentor");
			mimeMessageHelper.setTo("mailhomes@mail.ru");
			mimeMessageHelper.setFrom("JavaMentor");
			String htmlContent = htmlTemplateEngine.process(templateFile, ctx);
			mimeMessageHelper.setText(htmlContent, true);
			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			throw new EmailTemplateException(e.getMessage());
		}
	}
}
