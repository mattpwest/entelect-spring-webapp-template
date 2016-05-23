package za.co.entelect.web.controllers.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import za.co.entelect.web.dto.NavLocation;

@Controller
@Slf4j
@RequestMapping(value = "")
public class HomeController extends AbstractBaseController {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView newsList(ModelMap model) {
        setNav(model, NavLocation.HOME);

        return new ModelAndView("/home", model);
    }

}
