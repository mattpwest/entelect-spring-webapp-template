package za.co.entelect.comms.services;

import za.co.entelect.comms.exceptions.MailDataException;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.email.Email;

public interface MailService {

    /**
     * Queues a mail to a user for sending at a later time.
     *
     * @param user User to send the mail to.
     * @param template Mail template to use for rendering the message text.
     * @param data Data object to provide data to the template.
     *
     * @return Queued mail.
     * @throws MailDataException Thrown if we are unable to map the data object to JSON for storage.
     */
    Email queueMail(AppUser user, String template, String subject, Object data, Email.Priority priority) throws MailDataException;

    /**
     * Queues a mail to an alternate email address for a user to send at a later time.
     *
     * @param emailAddress The alternate email address to use for the user.
     * @param user User to send the mail to.
     * @param template Mail template to use for rendering the message text.
     * @param data Data object to provide data to the template.
     *
     * @return Queued mail.
     * @throws MailDataException Thrown if we are unable to map the data object to JSON for storage.
     */
    Email queueMail(String emailAddress, AppUser user, String template, String subject, Object data, Email.Priority priority) throws MailDataException;

    /**
     * Builds an Email object.
     *
     * @param user User to send the mail to.
     * @param template Mail template to use for rendering the message text.
     * @param data Data object to provide data to the template.
     *
     * @return Email.
     * @throws MailDataException Thrown if we are unable to map the data object to JSON for storage.
     */
    Email buildMail(AppUser user, String template, String subject, Object data, Email.Priority priority) throws MailDataException;

    /**
     * Builds an Email object.
     *
     * @param user User to send the mail to.
     * @param template Mail template to use for rendering the message text.
     * @param data Data object to provide data to the template.
     *
     * @return Email.
     * @throws MailDataException Thrown if we are unable to map the data object to JSON for storage.
     */
    Email buildMail(String emailAddress, AppUser user, String template, String subject, Object data, Email.Priority priority) throws MailDataException;
}
