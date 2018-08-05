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
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@EnableAsync
@PropertySources(value = {
        @PropertySource("classpath:application.properties"),
        @PropertySource(value = "file:./javamentortest.properties", encoding = "Cp1251")
})
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
    private Environment env;


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
        this.env = environment;
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

    public void validatorTestResult(String parseContent, Client client) throws MessagingException {
        Pattern pattern2 = Pattern.compile("\\d[:]\\s\\d\\s");
        Matcher m = pattern2.matcher(parseContent);

        Multimap<Integer, String> allQuestions = MultimapBuilder.treeKeys().linkedListValues().build();
        allQuestions.put(1, null);
        allQuestions.put(1, env.getRequiredProperty("question.one.one"));
        allQuestions.put(1, env.getRequiredProperty("question.one.two"));
        allQuestions.put(1, env.getRequiredProperty("question.one.three"));
        allQuestions.put(1, env.getRequiredProperty("question.one.four"));

        allQuestions.put(2, null);
        allQuestions.put(2, env.getRequiredProperty("question.two.one"));
        allQuestions.put(2, env.getRequiredProperty("question.two.two"));
        allQuestions.put(2, env.getRequiredProperty("question.two.three"));
        allQuestions.put(2, env.getRequiredProperty("question.two.four"));
        allQuestions.put(2, env.getRequiredProperty("question.two.five"));

        allQuestions.put(3, null);
        allQuestions.put(3, env.getRequiredProperty("question.three.one"));
        allQuestions.put(3, env.getRequiredProperty("question.three.two"));
        allQuestions.put(3, env.getRequiredProperty("question.three.three"));
        allQuestions.put(3, env.getRequiredProperty("question.three.four"));

        allQuestions.put(4, null);
        allQuestions.put(4, env.getRequiredProperty("question.four.one"));
        allQuestions.put(4, env.getRequiredProperty("question.four.two"));
        allQuestions.put(4, env.getRequiredProperty("question.four.three"));
        allQuestions.put(4, env.getRequiredProperty("question.four.four"));

        allQuestions.put(5, null);
        allQuestions.put(5, env.getRequiredProperty("question.five.one"));
        allQuestions.put(5, env.getRequiredProperty("question.five.two"));
        allQuestions.put(5, env.getRequiredProperty("question.five.three"));
        allQuestions.put(5, env.getRequiredProperty("question.five.four"));

        allQuestions.put(6, null);
        allQuestions.put(6, env.getRequiredProperty("question.six.one"));
        allQuestions.put(6, env.getRequiredProperty("question.six.two"));
        allQuestions.put(6, env.getRequiredProperty("question.six.three"));
        allQuestions.put(6, env.getRequiredProperty("question.six.four"));

        Map<Integer, Integer> rightAnswer = new HashMap<>();
        rightAnswer.put(1, 2);
        rightAnswer.put(2, 1);
        rightAnswer.put(3, 3);
        rightAnswer.put(4, 2);
        rightAnswer.put(5, 3);
        rightAnswer.put(6, 4);


        Map<Integer, String> questionText = new HashMap<>();
        questionText.put(1, env.getRequiredProperty("question.one"));
        questionText.put(2, env.getRequiredProperty("question.two"));
        questionText.put(3, env.getRequiredProperty("question.three"));
        questionText.put(4, env.getRequiredProperty("question.four"));
        questionText.put(5, env.getRequiredProperty("question.five"));
        questionText.put(6, env.getRequiredProperty("question.six"));

        Map<Integer, String> questionExplanation = new HashMap<>();
        questionExplanation.put(1, env.getRequiredProperty("explanation.question.one"));
        questionExplanation.put(2, env.getRequiredProperty("explanation.question.two"));
        questionExplanation.put(3, env.getRequiredProperty("explanation.question.three"));
        questionExplanation.put(4, env.getRequiredProperty("explanation.question.four"));
        questionExplanation.put(5, env.getRequiredProperty("explanation.question.five"));
        questionExplanation.put(6, env.getRequiredProperty("explanation.question.six"));


        Map<Integer, Integer> wrong = new HashMap<>();
        int countOfRight = 0;
        while (m.find()) {
            String tmp = m.group();
            String indexString = tmp.substring(0, tmp.indexOf(":"));
            tmp = tmp.replaceAll("([0-9][:])|\\s", "");

            int index = Integer.valueOf(indexString);
            int answer = Integer.valueOf(tmp);

            boolean ans = (answer == rightAnswer.get(index));
            if (!ans) {
                wrong.put(index, answer);
            } else {
                countOfRight++;
            }

        }

        double fdf = (countOfRight / 6.0) * 100;
        String percentage = String.format("%1$,.0f", fdf);

        final Context ctx = new Context();
        ctx.setVariable("name", client.getName());
        ctx.setVariable("percentageOfRightAnswer", percentage);
        ctx.setVariable("mapOfWrongAnswer", wrong);
        ctx.setVariable("rightQuestionText", questionText);
        ctx.setVariable("rightQuestionExplanation", questionExplanation);

        ctx.setVariable("allQuestions", allQuestions);
        ctx.setVariable("rightAnswer", rightAnswer);

        final String htmlContent = htmlTemplateEngine.process("check-test-email-template2", ctx);


        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setFrom("Java-Mentor.ru");
        mimeMessageHelper.setTo(client.getEmail());
        mimeMessageHelper.setSubject("Test complete!");
        mimeMessageHelper.setText(htmlContent, true);


        javaMailSender.send(mimeMessage);
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
