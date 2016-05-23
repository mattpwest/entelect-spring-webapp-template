package za.co.entelect;

import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import za.co.entelect.comms.config.MailConfig;
import za.co.entelect.comms.exceptions.MailDataException;
import za.co.entelect.comms.services.MailTestService;
import za.co.entelect.IntegrationTest;
import za.co.entelect.domain.entities.email.Email;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MailConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class EmailTest {

    static {
        System.setProperty("ENTELECT_ENV", "test");
    }

    @Autowired
    protected MailTestService mailTestService;

    protected Email queueTestEmail() throws MailDataException {
        return mailTestService.sendTestEmail();
    }

    protected Email queueTestEmail(Email.Priority priority) throws MailDataException {
        return mailTestService.sendTestEmail(priority);
    }
}
