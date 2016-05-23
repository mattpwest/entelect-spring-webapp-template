package za.co.entelect.services.security.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import za.co.entelect.domain.entities.user.AppUser;

import java.util.Collection;

public class CustomUser extends User {

    @Getter
    private AppUser appUser;

    public CustomUser(final String username,
                      final String password,
                      final Collection<? extends GrantedAuthority> authorities,
                      final AppUser appUser) {
        super(username, password, authorities);

        this.appUser = appUser;
    }
}
