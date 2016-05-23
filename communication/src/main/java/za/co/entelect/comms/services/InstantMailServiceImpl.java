package za.co.entelect.comms.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import za.co.entelect.comms.MailSender;
import za.co.entelect.comms.exceptions.MailDataException;
import za.co.entelect.comms.exceptions.MailSendingException;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.email.Email;

/**
 * Sends out a queued mail immediately. Used for development.
 */
@Slf4j
public class InstantMailServiceImpl extends QueuedMailServiceImpl {

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private MailSender mailSender;

    @Override
    public Email queueMail(final AppUser user, final String template, final String subject, final Object data, Email.Priority priority) throws MailDataException {
        Email mail = super.queueMail(user, template, subject, data, priority);

        try {
            mailSender.sendEmail(mail);
        } catch (MailSendingException ex) {
            log.error("Instant mail failed to send. Message lost.", ex);
        }

        return mail;
    }

    @Override
    public Email queueMail(String emailAddress, AppUser user, String template, String subject, Object data, Email.Priority priority) throws MailDataException {
        Email mail = super.queueMail(emailAddress, user, template, subject, data, priority);

        try {
            mailSender.sendEmail(mail);
        } catch (MailSendingException ex) {
            log.error("Instant mail failed to send. Message lost.", ex);
        }

        return mail;
    }
}
