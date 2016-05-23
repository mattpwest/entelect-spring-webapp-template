package za.co.entelect.web.controllers.web.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import za.co.entelect.web.controllers.web.AbstractBaseController;

@Controller
@Slf4j
@Profile("dev")
public class TestController extends AbstractBaseController {

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public ModelAndView testViews(@RequestParam String view) {
        return new ModelAndView("/" + view, null);
    }

}
