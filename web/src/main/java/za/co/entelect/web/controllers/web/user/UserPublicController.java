package za.co.entelect.web.controllers.web.user;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import za.co.entelect.web.controllers.web.AbstractBaseController;
import za.co.entelect.web.dto.NavLocation;

/**
 * Controller for the User Profile page
 */
@Controller
@Slf4j
@RequestMapping(value = "/users")
public class UserPublicController extends AbstractBaseController {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView userList(ModelMap model) {
        setNav(model, NavLocation.PROFILE);
        return new ModelAndView("/appuser/list");
    }
}
