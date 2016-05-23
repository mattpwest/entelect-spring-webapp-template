package za.co.entelect.web.controllers.web.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.providers.exceptions.TokenExpiredException;
import za.co.entelect.services.providers.exceptions.TokenNotFoundException;
import za.co.entelect.web.controllers.web.AbstractBaseController;
import za.co.entelect.web.forms.ForgotPasswordForm;
import za.co.entelect.web.forms.ResetPasswordForm;
import za.co.entelect.web.security.SecurityHelper;
import za.co.entelect.web.validators.UserPasswordValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controller for serving the page where a user can request a password reset and for the destination page where the
 * user resets their password after following the password reset link mailed to them.
 */
@Controller
@Slf4j
public class ForgotPasswordController extends AbstractBaseController {

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserPasswordValidator userPasswordValidator;

    @InitBinder("resetPasswordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(userPasswordValidator);
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public ModelAndView forgotPassword(ModelMap modelMap) {
        modelMap.put("forgotPasswordForm", new ForgotPasswordForm());
        return new ModelAndView("/appuser/forgotPassword");
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ModelAndView sendPasswordReset(@Valid @ModelAttribute ForgotPasswordForm forgotPasswordForm,
                                          BindingResult binding,
                                          HttpServletRequest request,
                                          ModelMap model,
                                          RedirectAttributes redirectAttributes) {

        if (binding.hasErrors()) {
            model.put("forgotPasswordForm", forgotPasswordForm);
            return new ModelAndView("/appuser/forgotPassword", model);
        }

        AppUser user = appUserDao.findByEmail(forgotPasswordForm.getEmail());
        if (user != null) {
            appUserService.sendForgotPasswordToken(user, SecurityHelper.getTokenUri(request, "/forgotPassword/reset"));
        }
        // Otherwise pretend everything went fine, so attackers can't use this form to find user emails...

        redirectAttributes.addFlashAttribute("message", "A password reset link has been emailed to you.");

        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(value = "/forgotPassword/reset", method = RequestMethod.GET)
    public ModelAndView forgotPasswordReset(@RequestParam(required = true, value = "token") String token,
                                            ModelMap modelMap) {

        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        resetPasswordForm.setPasswordResetToken(token);
        modelMap.put("resetPasswordForm", resetPasswordForm);
        return new ModelAndView("/appuser/forgotPasswordReset");
    }

    @RequestMapping(value = "/forgotPassword/reset", method = RequestMethod.POST)
    public ModelAndView forgotPasswordResetComplete(@Valid @ModelAttribute ResetPasswordForm resetPasswordForm,
                                                    BindingResult binding,
                                                    ModelMap model,
                                                    RedirectAttributes redirectAttributes) {

        if (binding.hasErrors()) {
            model.put("resetPasswordForm", resetPasswordForm);
            return new ModelAndView("/appuser/forgotPasswordReset", model);
        }

        try {
            appUserService.resetPasswordWithToken(
                resetPasswordForm.getPasswordResetToken(),
                passwordEncoder.encode(resetPasswordForm.getPassword()));
        } catch (TokenNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error",
                "Password reset token not found. Please try re-requesting a password reset.");

            return new ModelAndView("redirect:/forgotPassword");
        } catch (TokenExpiredException ex) {
            redirectAttributes.addFlashAttribute("error",
                "Your password reset token has expired. Please try re-requesting a password reset.");

            return new ModelAndView("redirect:/forgotPassword");
        }

        redirectAttributes.addFlashAttribute("message", "Your password has been changed.");
        return new ModelAndView("redirect:/login");
    }
}
