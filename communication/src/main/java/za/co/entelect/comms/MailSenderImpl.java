package za.co.entelect.comms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import za.co.entelect.comms.exceptions.MailDataException;
import za.co.entelect.comms.exceptions.MailSendingException;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.domain.entities.email.Email;
import za.co.entelect.domain.entities.email.Template;
import za.co.entelect.persistence.email.EmailDao;
import za.co.entelect.persistence.email.TemplateDao;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MailSenderImpl implements MailSender, ResourceLoaderAware {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Map<String, String> imageIds = new HashMap<>();

    private static final Map<String, Resource> images = new HashMap<>();

    private static int nextImageId = 1;

    @Autowired
    private ConfigProperties config;

    @Autowired
    private EmailDao emailDao;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    TemplateDao templateDao;

    @Autowired
    @Qualifier("emailTemplateEngine")
    private SpringTemplateEngine emailTemplateEngine;

    private ResourceLoader resourceLoader;
    private MimeMessage mimeMessage;

    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void sendEmail(Email mail) throws MailSendingException {
        log.info(String.format("Sending mail %d to %s with template %s.",
            mail.getId(), mail.getUser().getEmail(), mail.getTemplate()));
        try {
            interpretEmail(mail, true);
        }
        catch(MailDataException mde){
            throw new MailSendingException();
        }
        mailSender.send(mimeMessage);

        // Mark the mail as sent
        mail.setSentAt(LocalDateTime.now());
        emailDao.save(mail);
    }

    @Override
    public String interpretEmail(Email mail, boolean isEmail) throws MailDataException {
        try {
            Context mailContext = createMailContext(mail, isEmail);
            Map<String, Resource> images = loadTemplateImages(mail.getTemplate());

            // Map image0, image1, .., imageN to the actual cached internal image IDs
            int i = 0;
            for (String imageId : images.keySet()) {
                mailContext.setVariable("image" + i++, imageId);
            }
            String mailBody = emailTemplateEngine.process(mail.getTemplate(), mailContext);
            mimeMessage = buildMimeMessage(mail, mailBody, images);
            return mailBody;
        }
        catch(MailSendingException mse){
            String message = String.format("Failed to interpret mail. Could not deserialize data JSON: %s", mail.getDataJson());
            log.debug(message, mse);
            throw new MailDataException(message, mse);
        }
    }

    private Context createMailContext(Email mail, boolean isEmail) throws MailSendingException {
        Map<String, Object> model;
        try {
            model = convertValueObjectJsonToMap(mail.getDataJson());
            if(isEmail){
                model.put("emailTemplate",true);
            }
        } catch (Exception ex) {
            String message = String.format("Failed to send mail. Could not deserialize data JSON: %s", mail.getDataJson());
            log.debug(message, ex);
            throw new MailSendingException(message, ex);
        }

        Context context = new Context();
        context.setVariables(model);
        return context;
    }

    private Map<String, Object> convertValueObjectJsonToMap(String valueObjectJson) throws Exception {
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>(){};
        return mapper.readValue(valueObjectJson, typeRef);
    }

    private Map<String, Resource> loadTemplateImages(String template) throws MailSendingException {
        Map<String, Resource> result = new HashMap<>();
        Document doc;
        try {
            doc = Jsoup.parse(loadRawTemplateText(template), "UTF-8", "");
        } catch (IOException ex) {
            String msg = "Failed to read raw email template for image loading.";
            log.debug(msg, ex);
            throw new MailSendingException(msg, ex);
        }

        for (Element element : doc.select("img")) {
            element.removeAttr("th:src");
            String imgSrc = element.attr("src");

            String imageId;
            if (!imageIds.containsKey(imgSrc)) {
                imageId = "image" + nextImageId++;
                imageIds.put(imgSrc, imageId);

                File imgFile = new File(imgSrc);
                String imgFileName = imgFile.getName();

                String imagePath = "classpath:/za/co/entelect/mail/images/" + imgFileName;
                Resource imageResource = resourceLoader.getResource(imagePath);

                images.put(imageId, imageResource);
            } else {
                imageId = imageIds.get(imgSrc);
            }

            result.put(imageId, images.get(imageId));
        }

        return result;
    }

    private InputStream loadRawTemplateText(final String templateName) throws IOException {
        Template template = templateDao.findByName(templateName);
        if(template!=null) {
            return new ByteArrayInputStream(template.getBody().getBytes());
        }
        return null;
    }

    private MimeMessage buildMimeMessage(final Email mail, final String mailBody,
                                         final Map<String, Resource> imagesToInclude)
        throws MailSendingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message;
        try {
            message = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
            if(mail.getEmail() == null) {
                message.setTo(mail.getUser().getEmail());
            }
            else {
                message.setTo(mail.getEmail());
            }
            message.setSubject(mail.getSubject());
            message.setText(mailBody, true); // true = html
            message.setFrom(config.getSmtpFrom());

            for (String imgId : imagesToInclude.keySet()) {
                message.addInline(imgId, imagesToInclude.get(imgId));
            }
        } catch (MessagingException ex) {
            String msg = "Mail sending failed - could not build mime message.";
            log.debug(msg, ex);
            throw new MailSendingException(msg, ex);
        }

        return mimeMessage;
    }
}
