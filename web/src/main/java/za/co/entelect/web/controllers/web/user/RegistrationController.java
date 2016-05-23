package za.co.entelect.web.controllers.web.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.providers.exceptions.EmailInUseException;
import za.co.entelect.web.controllers.web.AbstractBaseController;
import za.co.entelect.web.forms.captcha.RecaptchaUserRegistrationForm;
import za.co.entelect.web.security.SecurityHelper;
import za.co.entelect.web.validators.RecaptchaFormValidator;
import za.co.entelect.web.validators.UserPasswordValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controller for the registration page.
 */
@Controller
@Slf4j
public class RegistrationController extends AbstractBaseController {

    @Autowired
    private ConfigProperties config;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private RecaptchaFormValidator recaptchaFormValidator;

    @Autowired
    private UserPasswordValidator userPasswordValidator;

    @InitBinder("form")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(recaptchaFormValidator, userPasswordValidator);
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView beginRegistration(@ModelAttribute("form") RecaptchaUserRegistrationForm form,
                                          ModelMap model) {
        log.info("Showing registration page.");

        return new ModelAndView("/appuser/register", model);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView completeRegistration(@Valid @ModelAttribute("form") RecaptchaUserRegistrationForm form,
                                             BindingResult binding,
                                             HttpServletRequest request,
                                             ModelMap model) {
        if (binding.hasErrors()) {
            log.info(String.format("Registration failed for %s", form.getEmail()));

            for (ObjectError err : binding.getAllErrors()) {
                log.debug(
                    String.format("Registration form validation failed due to %s: %s", err.getCode(), err
                        .getDefaultMessage()));
            }

            return new ModelAndView("/appuser/register", model);
        }

        if(!config.isRegistrationEnabled()){
            return new ModelAndView("redirect:/register");
        }

        try {
            appUserService.registerUser(form, SecurityHelper.getTokenUri(request, "/verification/verify"));
        } catch (EmailInUseException ex) {
            binding.rejectValue("email", "email.used", "Email already used. Perhaps you just need a password reset?");
            return new ModelAndView("/appuser/register", model);
        }

        return new ModelAndView("redirect:/verification");
    }
}
