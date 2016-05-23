package za.co.entelect.web.controllers.web.user.profile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.web.dto.NavLocation;
import za.co.entelect.web.forms.ProfileForm;

import javax.validation.Valid;

@Controller
@Slf4j
@RequestMapping(value = "/profile")
public class UserPrivateProfileController extends AbstractProfileController {

    @Autowired
    private AppUserDao appUserDao;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView userPrivateProfile(ModelMap model) {
        setupViewModel(model);

        return new ModelAndView("/appuser/view", model);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ModelAndView editProfile(@ModelAttribute ProfileForm profileForm,
                                    ModelMap model) {

        AppUser currentUser = (AppUser) model.get("currentUser");

        updateForm(currentUser, profileForm);

        setupEditModel(model, currentUser);
        return new ModelAndView("/appuser/edit", model);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ModelAndView saveProfile(@Valid @ModelAttribute("profileForm") ProfileForm profileForm,
                                    BindingResult binding,
                                    RedirectAttributes redirectAttributes,
                                    ModelMap model) {

        AppUser currentUser = (AppUser) model.get("currentUser");

        if (binding.hasErrors()) {
            setupEditModel(model, currentUser);
            model.put("error", "Unable to save changes! Please check that fields contain valid input.");
            return new ModelAndView("/appuser/edit", model);
        }
        updateUser(currentUser, profileForm);
        appUserDao.save(currentUser);

        redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
        return new ModelAndView("redirect:/profile");
    }

    private void setupEditModel(ModelMap model, AppUser user) {
        setNav(model, NavLocation.PROFILE);

        model.put("user", user);

        setupHistoryViewModel(model, user, 0);
    }
}
