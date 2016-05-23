package za.co.entelect.comms.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import za.co.entelect.comms.exceptions.MailDataException;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.email.Email;
import za.co.entelect.persistence.email.EmailDao;

/**
 * Queues a mail for later sending by a worker.
 */
@Slf4j
public class QueuedMailServiceImpl implements MailService {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ConfigProperties config;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private EmailDao emailDao;

    @Override
    @Transactional
    public Email queueMail(final AppUser user, final String template, final String subject, final Object data, Email.Priority priority)
        throws MailDataException {

        log.info(String.format("Queuing mail to %s with template %s.", user.getEmail(), template));

        Email mail = buildMail(user, template, subject, data, priority);

        mail.setSubject(config.getSmtpSubjectPrefix()+" "+subject);

        emailDao.save(mail);
        return mail;
    }

    @Override
    public Email queueMail(String emailAddress, AppUser user, String template, String subject, Object data, Email.Priority priority) throws MailDataException {
        log.info(String.format("Queuing mail to %s with template %s.", user.getEmail(), template));

        Email mail = buildMail(emailAddress, user, template, subject, data, priority);

        mail.setSubject(config.getSmtpSubjectPrefix()+" "+subject);

        emailDao.save(mail);
        return mail;
    }

    @Override
    public Email buildMail(AppUser user, String template, String subject, Object data, Email.Priority priority) throws MailDataException {
        Email mail = new Email();
        mail.setUser(user);
        mail.setTemplate(template);
        mail.setSubject(subject);
        mail.setPriority(priority);
        mail.setEmail(user.getEmail());
        setJsonData(mail, data);

        return mail;
    }

    @Override
    public Email buildMail(String emailAddress, AppUser user, String template, String subject, Object data, Email.Priority priority) throws MailDataException {
        Email mail = new Email();
        mail.setEmail(emailAddress);
        mail.setUser(user);
        mail.setTemplate(template);
        mail.setSubject(subject);
        mail.setPriority(priority);
        setJsonData(mail, data);

        return mail;
    }

    private void setJsonData(Email mail, Object data) throws MailDataException {
        try {
            mail.setDataJson(mapper.writeValueAsString(data));
        } catch (Exception ex) {
            String msg = String.format("Failed to queue e-mail - data mapping failed for: %s", data.toString());
            log.debug(msg, ex);
            throw new MailDataException(msg, ex);
        }
    }
}
