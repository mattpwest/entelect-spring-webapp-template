package za.co.entelect.web.controllers.web.user.profile;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import za.co.entelect.domain.entities.history.HistoryEntity;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.web.controllers.web.AbstractBaseHistoryController;
import za.co.entelect.web.dto.NavLocation;
import za.co.entelect.web.exceptions.EntityNotFoundException;
import za.co.entelect.web.forms.ProfileForm;

import java.util.Map;

public abstract class AbstractProfileController extends AbstractBaseHistoryController {

    protected final PageRequest TOP_THREE = new PageRequest(0, 3);

    @Autowired
    @Getter
    protected AppUserService appUserService;

    @ModelAttribute("user")
    public AppUser getRules(@PathVariable Map<String, String> pathVariables) {
        if (!pathVariables.containsKey("userId")) {
            return null;
        }

        String userId = pathVariables.get("userId");
        AppUser user = appUserService.findOne(parsedLongOrDefault(userId, 0L));
        if (user == null) {
            throw new EntityNotFoundException(String.format("User with id %s not found.", userId));
        }

        return user;
    }

    @Override
    protected HistoryEntity getEntity(Long id) {
        return appUserService.findOne(id);
    }

    @Override
    protected String getControllerRootUri() {
        return "/users/";
    }

    protected void setupViewModel(ModelMap model) {
        setNav(model, NavLocation.PROFILE);

        AppUser user = (AppUser) model.get("user");
        if (user == null) {
            user = (AppUser) model.get("currentUser");
            model.put("user", user);
        }

        setupHistoryViewModel(model, user, 0);
    }

    protected void updateUser(AppUser user, ProfileForm profileForm) {
        user.setFirstName(profileForm.getFirstName());
        user.setLastName(profileForm.getLastName());
    }

    protected void updateForm(AppUser user, ProfileForm profileForm) {
        profileForm.setUserId(user.getId());
        profileForm.setFirstName(user.getFirstName());
        profileForm.setLastName(user.getLastName());
    }
}
