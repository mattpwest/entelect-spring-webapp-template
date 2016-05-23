package za.co.entelect.web.controllers.web.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.security.dto.CustomUser;
import za.co.entelect.web.controllers.web.AbstractBaseController;
import za.co.entelect.services.providers.dto.forms.user.ResetPasswordForm;

import javax.validation.Valid;

/**
 * Account management controller. Used to change user details, passwords, etc.
 */
@Controller
@Slf4j
public class AccountController extends AbstractBaseController {

    @Autowired
    private AppUserService appUserService;

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public ModelAndView userAccount(@AuthenticationPrincipal CustomUser activeUser,
                                    ModelMap model) {
        AppUser userDetails = activeUser.getAppUser();
        model.put("user", userDetails);
        model.put("resetPasswordForm", new ResetPasswordForm());

        return new ModelAndView("/appuser/account", model);
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ModelAndView passwordReset(@AuthenticationPrincipal CustomUser activeUser,
                                      @Valid @ModelAttribute ResetPasswordForm resetPasswordForm,
                                      BindingResult binding,
                                      RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            log.info(String.format("Password reset for %s failed due to invalid input.",
                activeUser.getAppUser().getEmail()));

            redirectAttributes.addFlashAttribute("error", "Password reset failed.");

            return new ModelAndView("redirect:/account");
        }

        if (!resetPasswordForm.getPassword().equals(resetPasswordForm.getPasswordConfirmation())) {
            redirectAttributes.addFlashAttribute("error", "Passwords must match.");
            return new ModelAndView("redirect:/account");
        }

        redirectAttributes.addFlashAttribute("message", "Password has been changed.");
        appUserService.resetPassword(activeUser.getAppUser(), resetPasswordForm.getPassword());
        return new ModelAndView("redirect:/account");
    }
}
