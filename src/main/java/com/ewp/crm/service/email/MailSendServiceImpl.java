package com.ewp.crm.service.email;

import com.ewp.crm.configs.inteface.MailConfig;
import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@EnableAsync
@PropertySources(value = {
        @PropertySource("classpath:application.properties"),
        @PropertySource(value = "file:./javamentortest.properties", encoding = "Cp1251"),
        @PropertySource(value = "file:./monthly-report.properties", encoding = "Cp1251")
})

//@PropertySource(value = "file:./monthly-report.properties", encoding = "Cp1251")

public class MailSendServiceImpl implements MailSendService {

    private static Logger logger = LoggerFactory.getLogger(MailSendServiceImpl.class);

    private final JavaMailSender javaMailSender;
    private final TemplateEngine htmlTemplateEngine;
    private final ClientService clientService;
    private final ClientHistoryService clientHistoryService;
    private final MessageService messageService;
    private final MailConfig mailConfig;
    private String emailLogin;
    private final Environment env;
    private final ProjectPropertiesService projectPropertiesService;


    @Autowired
    public MailSendServiceImpl(JavaMailSender javaMailSender,
                               @Qualifier("thymeleafTemplateEngine") TemplateEngine htmlTemplateEngine,
                               Environment environment,
                               @Lazy ClientService clientService,
                               ClientHistoryService clientHistoryService,
                               MessageService messageService,
                               MailConfig mailConfig, ProjectPropertiesService projectPropertiesService) {
        this.javaMailSender = javaMailSender;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.clientService = clientService;
        this.clientHistoryService = clientHistoryService;
        this.messageService = messageService;
        this.env = environment;
        this.mailConfig = mailConfig;
        this.projectPropertiesService = projectPropertiesService;
        checkConfig(environment);
    }

    private void checkConfig(Environment environment) {
        try {
            this.emailLogin = environment.getRequiredProperty("spring.mail.username");
            String password = environment.getRequiredProperty("spring.mail.password");
            if (emailLogin.isEmpty() || password.isEmpty()) {
                throw new NoSuchFieldException();
            }
        } catch (IllegalStateException | NoSuchFieldException e) {
            logger.error("Mail configs have not initialized. Check application.properties file", e);
            System.exit(1);
        }
    }

    public void sendEmailInAllCases(Client client) {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper mimeMessageHelper;
        ProjectProperties properties = projectPropertiesService.getOrCreate();
        MessageTemplate template = properties.getAutoAnswerTemplate();
        Optional<String> emailOptional = client.getEmail();
        if (emailOptional.isPresent() && !emailOptional.get().isEmpty()) {
            try {
                mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.setFrom(env.getProperty("messaging.mailing.set-from-Java-Mentor"));
                mimeMessageHelper.setTo(emailOptional.get());
                mimeMessageHelper.setSubject(env.getProperty("messaging.mailing.set-subject-personal-mentor"));
                mimeMessageHelper.setText(template.getTemplateText(), true);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            javaMailSender.send(mimeMessage);
        } else {
            logger.error("Can not send message! client id {} email not found or empty", client.getId());
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
        Optional<String> emailOptional = client.getEmail();
        if (emailOptional.isPresent() && !emailOptional.get().isEmpty()) {
            final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(env.getProperty("messaging.mailing.set-from-Java-Mentor"));
            mimeMessageHelper.setTo(emailOptional.get());
            mimeMessageHelper.setSubject(env.getProperty("messaging.mailing.set-subject-personal-mentor"));
            mimeMessageHelper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } else {
            logger.warn("Can not send message! client id {} email not found or empty", client.getId());
        }
    }

    public void prepareAndSend(Long clientId, String templateText, String body, User principal) {
        String templateFile = "emailStringTemplate";
        Optional<Client> client = clientService.getClientByID(clientId);
        if (client.isPresent()) {
            Optional<String> emailOptional = client.get().getEmail();
            if (emailOptional.isPresent() && !emailOptional.get().isEmpty()) {
                String recipient = client.get().getEmail().get();
                String fullName = client.get().getName() + " " + client.get().getLastName();
                Map<String, String> params = new HashMap<>();
                if (client.get().getContractLinkData() != null) {
                    String link = client.get().getContractLinkData().getContractLink();
                    params.put("%contractLink%", link);
                }
                //TODO в конфиг
                params.put("%fullName%", fullName);
                params.put("%bodyText%", body);
                params.put("%dateOfSkypeCall%", body);
                final Context ctx = new Context();
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
                        String path = (matcher.group()).replaceAll("/", "\\" + File.separator);
                        File file = new File(path);
                        if (file.exists()) {
                            InputStreamSource inputStreamSource = new FileSystemResource(file);
                            mimeMessageHelper.addInline(matcher.group(), inputStreamSource, "image/jpeg");
                        } else {
                            logger.error("Can not send message! Template attachment file {} not found. Fix email template.", file.getCanonicalPath());
                            return;
                        }
                    }
                    javaMailSender.send(mimeMessage);
                    if (principal != null) {
                        Optional<Client> clientEmail = clientService.getClientByEmail(recipient);
                        Optional<Message> message = messageService.addMessage(Message.Type.EMAIL, htmlContent.toString(), principal.getFullName());
                        if (clientEmail.isPresent() && message.isPresent()) {
                            clientHistoryService.createHistory(principal, clientEmail.get(), message.get()).ifPresent(client.get()::addHistory);
                            clientService.updateClient(client.get());
                        } else {
                            logger.error("Can't send mail to {}", recipient);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Can't send mail to {}", recipient, e);
                }
            }
        } else {
            logger.error("Can not send message! client id {} email not found or empty", client.get().getId());
        }
    }

    @Override
    public void sendSimpleNotification(Long clientId, String templateText) {
        prepareAndSend(clientId, templateText, "", null);
    }

    @Async
    public void sendNotificationMessage(User userToNotify, String notificationMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(env.getProperty("messaging.mailing.set-subject-crm-notification"));
        message.setText(notificationMessage);
        message.setFrom(emailLogin);
        message.setTo(userToNotify.getEmail());
        javaMailSender.send(message);
    }

    @Override
    public void sendReportToJavaMentorEmail(String report) {
        User user = new User();
        String javaMentorEmail = env.getRequiredProperty("report.mail");
        user.setEmail(javaMentorEmail);
        sendNotificationMessage(user, report);
    }
}
