package za.co.entelect.comms.services;

import za.co.entelect.domain.entities.email.Email;

import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * Service for testing e-mail in the local configuration.
 */
public interface MailTestService {

    Email sendTestEmail();
    Email sendTestEmail(Email.Priority priority);
    List<MimeMessage> getEmails();
}
