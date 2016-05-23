package za.co.entelect.web.controllers.web.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import za.co.entelect.config.ConfigProperties;

@Controller
public class FallBackErrorHandlerController {

    @Autowired
    private ConfigProperties config;

    @ModelAttribute("config")
    public ConfigProperties getConfigProperties() {
        return config;
    }

    /*
     * Very simple error page for handling view rendering errors without the risk of endless recursion.
     */
    @RequestMapping(value = "/viewError", method = RequestMethod.GET)
    public ModelAndView viewErrorDisplay(ModelMap model) {
        return new ModelAndView("/error/viewError", model);
    }

}
