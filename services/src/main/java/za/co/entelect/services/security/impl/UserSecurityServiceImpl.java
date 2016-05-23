package za.co.entelect.services.security.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.services.security.UserSecurityService;

@Service("userSecurity")
public class UserSecurityServiceImpl implements UserSecurityService {

    @Override
    public boolean isUserLoggedInUser(AppUser user) {
        String loggedInUsersEmailAddress;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        loggedInUsersEmailAddress = auth.getName();
        return user.getEmail().equals(loggedInUsersEmailAddress);
    }
}
