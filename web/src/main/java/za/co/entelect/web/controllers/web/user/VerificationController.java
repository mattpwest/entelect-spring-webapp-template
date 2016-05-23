package za.co.entelect.web.controllers.web.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.persistence.user.VerificationTokenDao;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.providers.exceptions.TokenExpiredException;
import za.co.entelect.services.providers.exceptions.TokenNotFoundException;
import za.co.entelect.web.controllers.web.AbstractBaseController;
import za.co.entelect.web.forms.VerificationResendForm;
import za.co.entelect.web.security.SecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controller for the account verification pages.
 */
@Controller
@Slf4j
public class VerificationController extends AbstractBaseController {

    private static final String VERIFICATION_RESEND_FORM_ATTR = "verificationForm";

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private VerificationTokenDao verificationTokenDao;

    @RequestMapping(value = "/verification", method = RequestMethod.GET)
    public ModelAndView emailVerification(ModelMap model) {
        return new ModelAndView("/appuser/verification", model);
    }

    @RequestMapping(value = "/verification/resend", method = RequestMethod.GET)
    public ModelAndView emailVerificationResend(@ModelAttribute VerificationResendForm verificationResendForm,
                                                ModelMap model) {
        return new ModelAndView("/appuser/verificationResend", model);
    }

    @RequestMapping(value = "/verification/resend", method = RequestMethod.POST)
    public ModelAndView emailVerificationResend(@Valid @ModelAttribute VerificationResendForm verificationResendForm,
                                                BindingResult binding,
                                                HttpServletRequest request,
                                                ModelMap model,
                                                RedirectAttributes redirectAttributes) {

        if (binding.hasErrors()) {
            model.put(VERIFICATION_RESEND_FORM_ATTR, verificationResendForm);
            return new ModelAndView("/appuser/verificationResend", model);
        }

        AppUser user = appUserDao.findByEmail(verificationResendForm.getEmail());
        if (user != null) {
            appUserService.resendVerificationEmail(user,
                SecurityHelper.getTokenUri(request, "/verification/verify"));
        }
        // Otherwise pretend everything went fine, so attackers can't use this form to find user emails...

        model.put("message", "An account verification link has been emailed to you." +
            "You need to complete verification before you will be able to sign in.");

        return new ModelAndView("/appuser/verificationResend", model);
    }

    @RequestMapping(value = "/verification/verify", method = RequestMethod.GET)
    public ModelAndView emailVerify(@RequestParam("token") String token,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        try {
            appUserService.verifyUser(token);
        } catch (TokenNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", "Verification token not found. Please try re-sending the " +
                "verification e-mail.");

            return new ModelAndView("redirect:/verification/resend");
        } catch (TokenExpiredException ex) {
            AppUser user = verificationTokenDao.findByToken(token).getUser();
            appUserService.resendVerificationEmail(user, SecurityHelper.getTokenUri(request, "/verification/verify"));

            redirectAttributes.addFlashAttribute("error",
                "Your verification token has expired so a new one has been mailed to you.");
            return new ModelAndView("redirect:/login");
        }

        redirectAttributes.addFlashAttribute("message", "Verification successful: you may now sign in.");
        return new ModelAndView("redirect:/login");
    }
}
