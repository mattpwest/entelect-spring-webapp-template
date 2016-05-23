package za.co.entelect.web.controllers.web.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import za.co.entelect.comms.services.MailTestService;
import za.co.entelect.web.controllers.web.AbstractBaseController;
import za.co.entelect.web.dto.NavLocation;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

@Controller
@Profile("greenMail")
@RequestMapping(value = "/admin/dev/email")
@Slf4j
public class EmailTestController extends AbstractBaseController {

    @Autowired
    private MailTestService mailTestService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView showTestEmails(ModelMap model) {
        setNav(model, NavLocation.ADMIN);

        model.put("mails", getMessages());
        return new ModelAndView("/email/test", model);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ModelAndView sendTestEmail(ModelMap model) {
        mailTestService.sendTestEmail();
        model.put("mails", getMessages());
        return new ModelAndView("/email/test", model);
    }

    private List<MimeMessageParser> getMessages() {
        List<MimeMessageParser> emails = new ArrayList<>();
        for (MimeMessage msg : mailTestService.getEmails()) {
            MimeMessageParser parser = new MimeMessageParser(msg);
            try {
                parser.parse();
            } catch (Exception ex) {
                log.warn("Parsing mail body failed.");
            }
            emails.add(parser);
        }
        return emails;
    }
}
