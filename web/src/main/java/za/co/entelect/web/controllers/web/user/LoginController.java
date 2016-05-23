package za.co.entelect.web.controllers.web.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import za.co.entelect.web.controllers.web.AbstractBaseController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Controller for the login page and login success and failure redirects.
 */
@Controller
@Slf4j
public class LoginController extends AbstractBaseController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView userLogin(HttpServletRequest request, ModelMap model) {
        log.info("Showing /appuser/login");

        HttpSession session = request.getSession();
        Exception ex = (Exception) session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        if (ex != null) {
            model.put("error", ex.getMessage());
        }

        return new ModelAndView("/appuser/login", model);
    }

    @RequestMapping(value = "/relogin", method = RequestMethod.GET)
    public ModelAndView userReLogin(RedirectAttributes redirectAttributes) {
        String message = "Private information cannot be accessed with 'remember me' authentication. " +
            "Please provide your username and password to proceed...";
        redirectAttributes.addFlashAttribute("error", message);
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(value = "/logoutSuccess", method = RequestMethod.GET)
    public ModelAndView logoutSuccess(RedirectAttributes redirectAttributes) {
        String message = "You have successfully logged out.";
        redirectAttributes.addFlashAttribute("message", message);
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(value = "/loginPassed", method = RequestMethod.GET)
    public ModelAndView loginPassed() {
        log.info("Redirecting to profile page after successful login");
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "/loginFailed", method = RequestMethod.GET)
    public ModelAndView loginFailed(ModelMap model) {
        log.info("Login failed, showing login page again...");

        model.put("error", "Incorrect username / password. Please try again.");

        return new ModelAndView("/appuser/login", model);
    }
}
