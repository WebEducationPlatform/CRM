package com.ewp.crm.controllers.rest.admin;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.exceptions.email.MessageTemplateException;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.User;
import com.ewp.crm.service.impl.MessageTemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import java.util.Optional;

@RestController
public class AdminEmailRestController {

    private final MessageTemplateServiceImpl messageTemplateService;
    private final ImageConfig imageConfig;

    @Autowired
    public AdminEmailRestController(MessageTemplateServiceImpl MessageTemplateService,
                                    ImageConfig imageConfig) {
        this.messageTemplateService = MessageTemplateService;
        this.imageConfig = imageConfig;
    }

    @ResponseBody
    @GetMapping(value = "/admin/image/{file}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    public byte[] getImage(@PathVariable("file") String file) throws IOException {
        Path fileLocation = Paths.get(imageConfig.getPathForImages() + file + ".png");
        return Files.readAllBytes(fileLocation);
    }


    @PostMapping(value = {"/admin/editMessageTemplate"})
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    public HttpStatus editETemplate(@RequestParam("templateName") String templateName,
                                    @RequestParam("templateText") String templateText,
                                    @RequestParam("theme") String themeTemplate,
                                    @RequestParam String otherTemplateText) {
        //TODO Убрать хардкод
        String msgTemplateDefaultTextBody = "%bodyText%";
        if (templateText.contains(msgTemplateDefaultTextBody) ^ otherTemplateText.contains(msgTemplateDefaultTextBody)) {
            throw new MessageTemplateException(msgTemplateDefaultTextBody + "должен присутствовать или остутствовать всех типах сообщения!");
        }

        Optional<MessageTemplate> messageTemplateOpt = messageTemplateService.getByName(templateName);
        MessageTemplate messageTemplate;
        String text =templateText.replaceAll("(\\s+)|(</?pre>)|(&nbsp;)|(</?p>)|(%bodyText%)","");
        String otherText = otherTemplateText.replaceAll("(\\s+)|(%bodyText%)","");
        if (text.length() == 0 || otherText.length() == 0) {
            throw new MessageTemplateException("Заполните шаблоны для всех типов сообщения: email/vk,sms,facebook,slack");
        }
        if (themeTemplate.length() == 0) {
            throw new MessageTemplateException("Заполните тему сообщения");
        }
        if (!messageTemplateOpt.isPresent()) {
            messageTemplate = new MessageTemplate(templateName, templateText, otherTemplateText, themeTemplate);
        } else {
            messageTemplate = messageTemplateOpt.get();
            messageTemplate.setTemplateText(templateText);
            messageTemplate.setOtherText(otherTemplateText);
            messageTemplate.setTheme(themeTemplate);
        }

        messageTemplateService.update(messageTemplate);

        return HttpStatus.OK;
    }

    @ResponseBody
    @PostMapping(value = "/admin/savePicture")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    public ResponseEntity savePicture(@RequestParam("0") MultipartFile file,
                                      @RequestParam String templateName,
                                      @AuthenticationPrincipal User currentAdmin) {
        try {
            BufferedImage image = ImageIO.read(new BufferedInputStream(file.getInputStream()));
            String fileName = file.getOriginalFilename().replaceFirst("[.][^.]+$", "") + ".png";
            String path = "images/templateName_" + templateName + "/" + fileName;
            File fileTarget = new File((path).replaceAll("/", "\\" + File.separator));
            if (fileTarget.exists()) {
                ImageIO.write(image, "png", fileTarget);
            } else {
                boolean mkdirs = fileTarget.mkdirs();
                if (mkdirs) {
                    ImageIO.write(image, "png", fileTarget);
                }
            }
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(currentAdmin.getId());
    }

}
