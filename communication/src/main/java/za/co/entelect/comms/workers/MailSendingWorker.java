package za.co.entelect.comms.workers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import za.co.entelect.comms.MailSender;
import za.co.entelect.comms.exceptions.MailSendingException;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.domain.entities.email.Email;
import za.co.entelect.persistence.email.EmailDao;

import java.util.Date;
import java.util.List;

@Slf4j
public class MailSendingWorker {

    @Autowired
    private EmailDao emailDao;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private ConfigProperties config;

    @Scheduled(cron = "${jobs.sendEmail.cron}")
    public void sendQueuedEmails() {
        Date startTime = new Date();
        List<Email> emails = emailDao.findBySentAtIsNullOrderByPriorityAsc();

        int mailOk = 0;
        int mailFail = 0;
        for (Email email : emails) {
            Date currentTime = new Date();
            long secondsBusy = (currentTime.getTime() - startTime.getTime()) / 1000;
            if (secondsBusy >= config.getSmtpTimeout()) {
                log.warn(String.format("Sending queued mails took too long... timing out with %d mails remaining.",
                    emails.size() - mailOk));
                break;
            }

            if (mailOk >= config.getSmtpRatePerMinute()) {
                log.warn(String.format("Already sent the max amount of emails per rate with %d mails remaining.",
                    emails.size() - mailOk));
                break;
            }

            try {
                mailSender.sendEmail(email);
                mailOk++;
            } catch (MailSendingException ex) {
                log.error("Failed to send queued mail.", ex);
                mailFail++;
            }
        }

        log.info(String.format("Sent %d of %d queued mails with %d errors.", mailOk, emails.size(), mailFail));
    }
}
