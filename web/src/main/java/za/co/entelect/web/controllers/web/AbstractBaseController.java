package za.co.entelect.web.controllers.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.security.dto.CustomUser;
import za.co.entelect.web.dto.NavLocation;

@Slf4j
public abstract class AbstractBaseController {

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private AppUserService appUserService;

    @ModelAttribute("config")
    public ConfigProperties getConfigProperties() {
        return configProperties;
    }

    @ModelAttribute("currentUser")
    public AppUser getCurrentUser(@AuthenticationPrincipal CustomUser currentUser) {

        if (currentUser == null) {
            return null;
        }

        return appUserService.findOne(currentUser.getAppUser().getId());
    }

    protected Long parsedLongOrDefault(String intString, Long defaultValue) {
        try {
            return Long.parseLong(intString);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    protected void setNav(ModelMap model, NavLocation location) {
        if (NavLocation.ADMIN.equals(location)) {
            model.put("navAdmin", true);
        } else if (NavLocation.HOME.equals(location)) {
            model.put("navHome", true);
        } else if (NavLocation.PROFILE.equals(location)) {
            model.put("navProfile", true);
        }
    }

}
