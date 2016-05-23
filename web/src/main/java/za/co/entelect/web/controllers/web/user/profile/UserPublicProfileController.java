package za.co.entelect.web.controllers.web.user.profile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.web.dto.NavLocation;
import za.co.entelect.web.forms.ProfileForm;

import javax.validation.Valid;

/**
 * Controller for the profile page.
 */
@Controller
@Slf4j
@RequestMapping(value = "/users")
public class UserPublicProfileController extends AbstractProfileController {

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ModelAndView userPublicProfile(ModelMap model) {

        setupViewModel(model);

        return new ModelAndView("/appuser/view", model);
    }

    @RequestMapping(value = "/{id}/history", method = RequestMethod.GET)
    public ModelAndView getHistoryPage(@PathVariable("id") Long id,
                                       @RequestParam("page") Integer page,
                                       ModelMap model) {
        return super.getHistoryPage(id, page, model);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{userId}/edit", method = RequestMethod.GET)
    public ModelAndView editUserProfile(@ModelAttribute ProfileForm profileForm,
                                        ModelMap model ) {

        AppUser userToBeEdited = (AppUser) model.get("user");
        updateForm(userToBeEdited, profileForm);
        setupEditModel(model);

        return new ModelAndView("/appuser/edit", model);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{userId}/save", method = RequestMethod.POST)
    public ModelAndView saveUserProfile(@Valid @ModelAttribute("profileForm") ProfileForm profileForm,
                                        BindingResult binding,
                                        RedirectAttributes redirectAttributes,
                                        ModelMap model) {

        AppUser user = (AppUser) model.get("user");

        if (binding.hasErrors()) {
            setupEditModel(model);
            model.put("error", "Unable to save changes! Please check that fields contain valid input.");
            return new ModelAndView("/appuser/view", model);
        }

        updateUser(user, profileForm);
        appUserService.save(user);

        redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
        return new ModelAndView("redirect:/admin/users");
    }

    private void setupEditModel(ModelMap model) {
        setNav(model, NavLocation.PROFILE);

        AppUser user = (AppUser) model.get("user");

        setupHistoryViewModel(model, user, 0);
    }
}
