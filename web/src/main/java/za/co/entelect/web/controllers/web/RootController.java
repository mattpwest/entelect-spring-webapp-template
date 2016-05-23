package za.co.entelect.web.controllers.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class RootController extends AbstractBaseController {

    @RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView accessDenied() {
        return new ModelAndView("/accessDenied");
    }
}
