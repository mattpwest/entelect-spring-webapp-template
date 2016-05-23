package za.co.entelect.services;

import com.icegreen.greenmail.spring.GreenMailBean;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import za.co.entelect.EmailTest;
import za.co.entelect.comms.exceptions.MailDataException;
import za.co.entelect.domain.entities.email.Email;
import za.co.entelect.persistence.email.EmailDao;

public class QueuedMailServiceImplTest extends EmailTest {

    static {
        System.setProperty("mail.strategy", "queued");
    }

    @Autowired
    private EmailDao emailDao;

    @Autowired
    private GreenMailBean greenMailBean;

    @Test
    public void testQueueMail() throws MailDataException {
        long numEmailsSent = greenMailBean.getReceivedMessages().length;
        long numEmails = emailDao.count();

        Email mail = queueTestEmail();

        long numEmailsAfterQueue = emailDao.count();
        long numEmailsSentAfterCall = greenMailBean.getReceivedMessages().length;

        Assert.assertNotNull("Mail was not created in DB.", mail);
        Assert.assertTrue("Number of mails in DB did not increase.", numEmailsAfterQueue > numEmails);
        Assert.assertNull("Mail sent date is not null in DB.", mail.getSentAt());
        Assert.assertFalse("Number of mails received by SMTP server has increased.",
            numEmailsSentAfterCall > numEmailsSent);
    }
}
