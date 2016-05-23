package za.co.entelect.web.controllers.web.error;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import za.co.entelect.web.controllers.web.AbstractBaseController;

@Controller
public class ErrorHandlerController extends AbstractBaseController {

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public ModelAndView errorDisplay(ModelMap model) {
        return new ModelAndView("/error/error", model);
    }

    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public ModelAndView pageNotFoundDisplay(ModelMap model) {
        return new ModelAndView("/error/404", model);
    }

}
