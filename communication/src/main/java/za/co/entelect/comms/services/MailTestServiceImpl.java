package za.co.entelect.comms.services;

import com.icegreen.greenmail.spring.GreenMailBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import za.co.entelect.comms.dto.AbstractMailParametersDto;
import za.co.entelect.comms.exceptions.MailException;
import za.co.entelect.domain.entities.email.Email;
import za.co.entelect.domain.entities.email.Template;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.persistence.email.TemplateDao;
import za.co.entelect.persistence.user.AppUserDao;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@Slf4j
public class MailTestServiceImpl implements MailTestService {
    private static final String TEMPLATE_NAME = "TEST";
    private static final String TEMPLATE_SUBJECT = "TEST_SUBJECT";
    private static final String TEMPLATE_BODY = "TEST_BODY";

    @Autowired
    private GreenMailBean greenMailBean;

    @Autowired
    private MailService mailService;

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private TemplateDao templateDao;

    private AppUser testUser;
    private Template template;

    @PostConstruct
    public void initialise() {
        testUser = appUserDao.findByEmail("testy@dogamingleague.co.za");
        template = templateDao.findByName(TEMPLATE_NAME);
    }

    @Override
    @Transactional
    public Email sendTestEmail() {
        return sendTestEmail(Email.Priority.NORMAL);
    }

    @Override
    @Transactional
    public Email sendTestEmail(Email.Priority priority) {
        if (testUser == null) {
            testUser = new AppUser();
            testUser.setEmail("testy@dogamingleague.co.za");
            testUser.setFirstName("Yugi");
            testUser.setLastName("Test");
            testUser.setPassword("123");
            testUser.setVerified(true);
            appUserDao.save(testUser);
        }

        if(template == null){
            template = new Template();
            template.setId(1L);
            template.setName(TEMPLATE_NAME);
            template.setSubject(TEMPLATE_SUBJECT);
            template.setBody(TEMPLATE_BODY);
            templateDao.save(template);
        }

        try {
            return mailService.queueMail(testUser, TEMPLATE_NAME, TEMPLATE_SUBJECT+
                ":"+priority, new AbstractMailParametersDto("Testy", "senderName", 1, 1, "entityName"), priority);
        } catch (MailException ex) {
            log.error("Failed to send test email.", ex);
            return null;
        }
    }

    public List<MimeMessage> getEmails() {
        List<MimeMessage> result = Arrays.asList(greenMailBean.getReceivedMessages());
        Collections.reverse(result);
        return result;
    }
}
