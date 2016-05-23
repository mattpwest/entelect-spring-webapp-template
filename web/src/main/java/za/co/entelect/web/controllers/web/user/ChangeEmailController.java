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
import za.co.entelect.comms.exceptions.MailDataException;
import za.co.entelect.comms.services.MailService;
import za.co.entelect.domain.entities.email.Email;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.user.ChangeEmailVerificationToken;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.providers.dto.VerificationTokenEmailDto;
import za.co.entelect.services.providers.exceptions.TokenNotFoundException;
import za.co.entelect.web.controllers.web.AbstractBaseController;
import za.co.entelect.web.forms.ChangeEmailForm;
import za.co.entelect.web.security.SecurityHelper;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class ChangeEmailController extends AbstractBaseController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private MailService mailService;

    @RequestMapping(value = "/changeEmail", method = RequestMethod.POST)
    public ModelAndView changeEmailAddress(@ModelAttribute("changeEmailForm") ChangeEmailForm changeEmailForm,
                                           BindingResult binding,
                                           HttpServletRequest request,
                                           RedirectAttributes redirectAttributes,
                                           ModelMap model) {
        AppUser currentUser = (AppUser) model.get("currentUser");
        if (binding.hasErrors()) {
            log.info(String.format("Changing of email failed for %s %s", currentUser.getFirstName(), currentUser
                .getLastName()));
            redirectAttributes.addFlashAttribute("error", "Invalid email address entered");
            return new ModelAndView("redirect: /profile/edit");
        }

        if (currentUser.getChangeEmailEnabled()) {
            if (appUserService.findByEmail(changeEmailForm.getChangeEmail()) != null) {
                redirectAttributes.addFlashAttribute("error", "Email address already in use");
                return new ModelAndView("redirect: /profile/edit");
            }

            ChangeEmailVerificationToken token = appUserService.createChangeEmailToken(changeEmailForm.getChangeEmail
                (), currentUser);
            try {
                mailService.queueMail(token.getNewEmail(), currentUser, "changeEmailVerification", "DGL Account " +
                        "Verification",
                    new VerificationTokenEmailDto(currentUser.getFirstName(), token.getToken(), SecurityHelper
                        .getTokenUri(request, "/changeEmail/verify")), Email.Priority.HIGH);

                mailService.queueMail(currentUser, "cancelChangeEmailVerification", "DGL Account Verification",
                    new VerificationTokenEmailDto(currentUser.getFirstName(), token.getToken(), SecurityHelper
                        .getTokenUri(request, "/changeEmail/cancel")), Email.Priority.HIGH);
            } catch (MailDataException ex) {
                log.warn(String.format("Failed to queue verification e-mail for %s.", currentUser.getEmail()), ex);
            }
        } else {
            log.info(String.format("Changing of email failed for %s %s: Change email not enabled",
                currentUser.getFirstName(), currentUser.getLastName()));
            redirectAttributes.addFlashAttribute("error", "Email address change not enabled for this user");
            return new ModelAndView("redirect: /profile/edit");
        }

        redirectAttributes.addFlashAttribute("message", "A verification mail has been sent to your new email address," +
            " please use this to complete the update.");
        return new ModelAndView("redirect: /profile/edit");
    }

    @RequestMapping(value = "/changeEmail/verify", method = RequestMethod.GET)
    public ModelAndView verifyChangedEmail(@RequestParam("token") String token,
                                           HttpServletRequest request,
                                           RedirectAttributes redirectAttributes) {

        try {
            ChangeEmailVerificationToken verificationToken = appUserService.changeEmail(token);
            if (verificationToken != null) {
                if (!verificationToken.isUsed()) {
                    AppUser user = verificationToken.getUser();
                    try {
                        mailService.queueMail(verificationToken.getNewEmail(), user, "verification", "DGL Account " +
                                "Verification",
                            new VerificationTokenEmailDto(user.getFirstName(), verificationToken.getToken(),
                                SecurityHelper.getTokenUri(request, "/changeEmail/verify")), Email.Priority.HIGH);
                    } catch (MailDataException ex) {
                        log.warn(String.format("Failed to queue verification e-mail for %s.", user.getEmail()), ex);
                    }
                    redirectAttributes.addFlashAttribute("message", "Verification token has expired. Please use the " +
                        "new link sent to your email address.");
                } else {
                    redirectAttributes.addFlashAttribute("error", "Verification token has been used or canceled by " +
                        "user already.");
                    return new ModelAndView("redirect:/login");
                }
            }
        } catch (TokenNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", "Verification token not found. Please try to change your " +
                "email address again.");

            return new ModelAndView("redirect: /profile/edit");
        }

        redirectAttributes.addFlashAttribute("message", "Verification successful: you may now sign in.");
        return new ModelAndView("redirect:/login");
    }


    @RequestMapping(value = "/changeEmail/cancel", method = RequestMethod.GET)
    public ModelAndView cancelChangedEmail(@RequestParam("token") String token,
                                           HttpServletRequest request,
                                           RedirectAttributes redirectAttributes) {

        appUserService.cancelEmailChange(token);

        redirectAttributes.addFlashAttribute("message", "Verification token canceled, please consider changing your " +
            "password as someone other than yourself may have accessed your account.");
        return new ModelAndView("redirect:/login");
    }
}
