package za.co.entelect.web.controllers.web.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import za.co.entelect.domain.Exceptions.UserNotBannedException;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.user.Role;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.persistence.user.RoleDao;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.providers.dto.forms.user.UserCreationForm;
import za.co.entelect.services.providers.exceptions.EmailInUseException;
import za.co.entelect.web.controllers.web.AbstractBaseController;
import za.co.entelect.web.dto.NavLocation;
import za.co.entelect.web.dto.user.AppUserDescriptionDTO;
import za.co.entelect.web.forms.user.BanForm;
import za.co.entelect.web.forms.user.UserEditDescriptionForm;
import za.co.entelect.web.forms.user.UserRolesForm;
import za.co.entelect.web.security.SecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controller for the administrators to manage users.
 */
@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(value = "/admin/users")
@Slf4j
public class UserAdminController extends AbstractBaseController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private RoleDao roleDao;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView adminUserList(@ModelAttribute("form") UserCreationForm form,
                                      @ModelAttribute("banForm") BanForm banForm,
                                      @ModelAttribute("editForm") UserEditDescriptionForm editForm,
                                      @PageableDefault(page = 0, value = 10) Pageable pageable,
                                      ModelMap model) {

        setNav(model, NavLocation.ADMIN);

        return new ModelAndView("/appuser/admin/list", model);
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ModelAndView resetUserPassword(@RequestParam(value = "userId", required = true) Long userId,
                                          HttpServletRequest request,
                                          RedirectAttributes redirectAttributes) {

        AppUser appUser = appUserDao.findOne(userId);

        if (appUser == null) {
            log.warn(String.format("Sending password reset e-mail failed - user ID %d not found.", userId));
            redirectAttributes.addFlashAttribute("error", "Sending password reset e-mail failed - user ID not found.");
            return new ModelAndView("redirect:/admin/users");
        }

        appUserService.sendForgotPasswordToken(appUser, SecurityHelper.getTokenUri(request, "/forgotPassword/reset"));

        redirectAttributes.addFlashAttribute("message",
            String.format("A password reset token has been mailed to user '%s'.", appUser.getEmail()));
        return new ModelAndView("redirect:/admin/users");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ModelAndView deleteUser(@RequestParam(value = "userId", required = true) Long userId,
                                          HttpServletRequest request,
                                          RedirectAttributes redirectAttributes) {

        AppUser appUser = appUserDao.findOne(userId);

        if (appUser == null) {
            log.warn(String.format("Failed to delete user - user with ID %d not found.", userId));
            redirectAttributes.addFlashAttribute("error", "Failed to delete user - user ID not found.");
            return new ModelAndView("redirect:/admin/users");
        }

        appUserDao.delete(appUser);

        redirectAttributes.addFlashAttribute("message",
            String.format("User account '%s' has been deleted.", appUser.getEmail()));
        return new ModelAndView("redirect:/admin/users");
    }

    @RequestMapping(value = "/disable", method = RequestMethod.POST)
    public ModelAndView disableUser(@RequestParam(value = "userId", required = true) Long userId,
                                   RedirectAttributes redirectAttributes) {

        AppUser appUser = appUserDao.findOne(userId);

        if (appUser == null) {
            log.warn(String.format("Failed to disable user - user with ID %d not found.", userId));
            redirectAttributes.addFlashAttribute("error", "Failed to disable user - user ID not found.");
            return new ModelAndView("redirect:/admin/users");
        }

        appUser.setEnabled(false);
        appUserDao.save(appUser);

        redirectAttributes.addFlashAttribute("message",
            String.format("User account '%s' has been disabled.", appUser.getEmail()));
        return new ModelAndView("redirect:/admin/users");
    }

    @RequestMapping(value = "/enable", method = RequestMethod.POST)
    public ModelAndView enableUser(@RequestParam(value = "userId", required = true) Long userId,
                                   RedirectAttributes redirectAttributes) {

        AppUser appUser = appUserDao.findOne(userId);

        if (appUser == null) {
            log.warn(String.format("Failed to enable user - user with ID %d not found.", userId));
            redirectAttributes.addFlashAttribute("error", "Failed to enable user - user ID not found.");
            return new ModelAndView("redirect:/admin/users");
        }

        appUser.setEnabled(true);
        appUserDao.save(appUser);

        redirectAttributes.addFlashAttribute("message",
            String.format("User account '%s' has been enabled.", appUser.getEmail()));
        return new ModelAndView("redirect:/admin/users");
    }

    @RequestMapping(value = "/{userId}/roles", method = RequestMethod.GET)
    public ModelAndView userRolesView(@PathVariable("userId") Long userId,
                                      ModelMap model,
                                      RedirectAttributes redirectAttributes) {

        setNav(model, NavLocation.ADMIN);

        AppUser user = appUserDao.findOne(userId);
        if (user == null) {
            log.warn(String.format("Failed to show user roles - user with ID %d not found.", userId));
            redirectAttributes.addFlashAttribute("error", "Failed to update user - user ID not found.");
            return new ModelAndView("redirect:/admin/users");
        }
        model.put("user", user);

        Iterable<Role> roles = roleDao.findAll();
        model.put("roles", roles);

        UserRolesForm form = new UserRolesForm();
        form.setRoles(new Long[user.getRoles().size()]);
        int index = 0;
        for (Role userRole : user.getRoles()) {
            form.getRoles()[index++] = userRole.getId();
        }
        model.put("userRolesForm", form);

        return new ModelAndView("/appuser/admin/roles");
    }

    @RequestMapping(value = "/{userId}/roles", method = RequestMethod.POST)
    public ModelAndView updateUserRoles(@PathVariable("userId") Long userId,
                                        @ModelAttribute UserRolesForm userRolesForm,
                                        RedirectAttributes redirectAttributes) {

        appUserService.updateUserRoles(userId, userRolesForm.getRoles());

        redirectAttributes.addFlashAttribute("message", "Updated user roles.");
        return new ModelAndView("redirect:/admin/users");
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView createUser(@Valid @ModelAttribute("form") UserCreationForm form,
                                   BindingResult binding,
                                   @PageableDefault(page = 0, value = 10) Pageable pageable,
                                   HttpServletRequest request,
                                   ModelMap model,
                                   RedirectAttributes redirectAttributes) {

        if (binding.hasErrors()) {
            Page<AppUser> users = appUserDao.findAll(pageable);
            model.put("page", users);
            model.put("createUserFailed", true);
            model.put("banForm" , new BanForm());
            model.put("editForm", new UserEditDescriptionForm());
            setNav(model, NavLocation.ADMIN);
            return new ModelAndView("/appuser/admin/list", model);
        }

        AppUser creator = (AppUser) model.get("currentUser");

        try {
            appUserService.createUser(form,
                creator,
                SecurityHelper.getTokenUri(request, "/verification/verify"),
                SecurityHelper.getTokenUri(request, "/forgotPassword/reset")
            );
        } catch (EmailInUseException ex) {
            Page<AppUser> users = appUserDao.findAll(pageable);
            model.put("page", users);
            model.put("createUserFailed", true);
            model.put("banForm" , new BanForm());
            model.put("editForm", new UserEditDescriptionForm());
            model.put("error", String.format("Email '%s' has already been used.", form.getEmail()));
            setNav(model, NavLocation.ADMIN);
            return new ModelAndView("/appuser/admin/list", model);
        }

        redirectAttributes.addFlashAttribute("message", String.format("User %s created.", form.getEmail()));
        return new ModelAndView("redirect:/admin/users");
    }

    @RequestMapping(value = "{userId}/enableChangeEmail", method = RequestMethod.GET)
    public ModelAndView enableEmailChange(@PathVariable(value = "userId") Long userId,
                                          RedirectAttributes redirectAttributes) {
        AppUser appUser = appUserDao.findOne(userId);
        if (appUser == null) {
            log.warn(String.format("Failed to enable user - user with ID %d not found.", userId));
            redirectAttributes.addFlashAttribute("error", "Failed to enable user - user ID not found.");
            return new ModelAndView("redirect:/admin/users");
        }
        appUser.setChangeEmailEnabled(true);
        appUserDao.save(appUser);

        redirectAttributes.addFlashAttribute("message",
            String.format("Change of email address has been enabled for '%s'.", appUser.getEmail()));
        return new ModelAndView("redirect:/admin/users");
    }
}
