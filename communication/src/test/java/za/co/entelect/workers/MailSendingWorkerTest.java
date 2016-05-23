package za.co.entelect.workers;

import com.icegreen.greenmail.spring.GreenMailBean;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import za.co.entelect.EmailTest;
import za.co.entelect.comms.exceptions.MailDataException;
import za.co.entelect.comms.workers.MailSendingWorker;
import za.co.entelect.domain.entities.email.Email;
import za.co.entelect.persistence.email.EmailDao;

public class MailSendingWorkerTest extends EmailTest {

    static {
        System.setProperty("mail.strategy", "queued");
    }

    @Autowired
    private EmailDao emailDao;

    @Autowired
    private GreenMailBean greenMailBean;

    @Autowired
    private MailSendingWorker mailSendingWorker;

    @Test
    public void testWorker() throws MailDataException {
        long numEmailsSent = greenMailBean.getReceivedMessages().length;
        Email mail = queueTestEmail();
        long numEmailsAfterQueue = greenMailBean.getReceivedMessages().length;

        Assert.assertNull("Mail sent at time was not null.", mail.getSentAt());
        Assert.assertTrue("Number of mails received has increased after queueing.",
            numEmailsSent == numEmailsAfterQueue);

        mailSendingWorker.sendQueuedEmails();
        long numEmailsAfterSend = greenMailBean.getReceivedMessages().length;
        mail = emailDao.findOne(mail.getId());

        Assert.assertNotNull("Mail sent at time is still null.", mail.getSentAt());
        Assert.assertTrue("Number of mails received did not increase after sending.",
            numEmailsAfterSend > numEmailsSent);
    }

    @Test
    public void testEmailPriority() throws Exception {
        long numEmailsSent = greenMailBean.getReceivedMessages().length;
        Email mail = queueTestEmail(Email.Priority.NORMAL);
        Email mail2 = queueTestEmail(Email.Priority.HIGH);
        long numEmailsAfterQueue = greenMailBean.getReceivedMessages().length;

        Assert.assertNull("Mail sent at time was not null.", mail.getSentAt());
        Assert.assertTrue("Number of mails received has increased after queueing.",
            numEmailsSent == numEmailsAfterQueue);

        mailSendingWorker.sendQueuedEmails();
        long numEmailsAfterSend = greenMailBean.getReceivedMessages().length;

        Assert.assertTrue("Number of mails received did not increase after sending.",
            numEmailsAfterSend > numEmailsSent);

        Assert.assertEquals("Entelect WebApp Template: TEST_SUBJECT:HIGH", greenMailBean.getReceivedMessages()[0].getSubject());
        Assert.assertEquals("Entelect WebApp Template: TEST_SUBJECT:NORMAL", greenMailBean.getReceivedMessages()[1].getSubject());
    }
}
