package com.ewp.crm;

import com.ewp.crm.configs.inteface.MailConfig;
import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings({"WeakerAccess", "OptionalGetWithoutIsPresent"})
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ParseMailToClientTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailConfig mailConfig;

    @Test
    public void fromEMailToDatabaseCheckClientIs() {
        try {
            String from = mailConfig.getMailFrom();
            String to = mailConfig.getLogin().replaceAll("%40", "@");
            String expectedClientEmail = "000test000@000test000.com";
            MimeMessage message = javaMailSender.createMimeMessage();
            String s = "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01//EN' 'http://www.w3.org/TR/html4/strict.dtd'>"
                    + "<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'><title>Пример веб-страницы</title>"
                    + "</head><body><p>Страница: http://java-mentor.com/index.html<br>Форма: Начать обучение<br>"
                    + "Name 5: Тест Тест<br>Телефон: " + "+00000000000" + "<br>Email 5: " + expectedClientEmail + "<br>"
                    + "Город: 777<br>Согласен: Согласен</p></body></html>";
            message.setContent(s, "text/html; charset=UTF-8");
            message.setSubject("Cообщение с сайта java-mentor.com");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setFrom(new InternetAddress(from));
            javaMailSender.send(message);
            await().atMost(30, TimeUnit.SECONDS).until(() -> clientService.getClientByEmail(expectedClientEmail).isPresent());
            Client actualClient = clientService.getClientByEmail(expectedClientEmail).get();
            String actualClientEmail = actualClient.getEmail().get();
            assertEquals(expectedClientEmail, actualClientEmail);
            clientService.delete(actualClient);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
