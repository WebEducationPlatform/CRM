package com.ewp.crm.service.email;

import com.ewp.crm.models.MailNotification;
import com.ewp.crm.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailNotificationService {

	private JavaMailSender emailSender;

	@Autowired
	public MailNotificationService(JavaMailSender javaMailSender) {
		this.emailSender = javaMailSender;
	}

	@Async
	public void sendNotificationMessage(User userToNotify){
		String subject = "Оповещение из CRM";
		String to = userToNotify.getEmail();
		MailNotification mail = new MailNotification();
		mail.setContent("fgreg");
		mail.setTo("extr1811@gmail.com");
		mail.setFrom("extremum1811@gmail.com");
		mail.setSubject("hui");
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject(mail.getSubject());
		message.setText(mail.getContent());
		message.setTo(mail.getTo());
		message.setFrom(mail.getFrom());

		emailSender.send(message);
	}
}
