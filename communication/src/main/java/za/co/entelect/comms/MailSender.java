package za.co.entelect.comms;

import za.co.entelect.comms.exceptions.MailDataException;
import za.co.entelect.comms.exceptions.MailSendingException;
import za.co.entelect.domain.entities.email.Email;

public interface MailSender {

    void sendEmail(Email mail) throws MailSendingException;

    String interpretEmail(Email mail, boolean isEmail) throws MailDataException;
}
