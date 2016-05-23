package za.co.entelect.services.security.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.user.Role;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.services.security.dto.CustomUser;
import za.co.entelect.services.security.exceptions.AccountDisabledException;
import za.co.entelect.services.security.exceptions.EmailNotVerifiedException;

import java.util.ArrayList;
import java.util.List;


@Service ("userDetailsService")
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
    @Setter
	private AppUserDao appUserDao;
	
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser appUser = appUserDao.findByEmail(username);

        if (appUser == null) {
            String msg = String.format("Login failed: User not found: '%s'.", username);
            log.info(msg);
            throw new UsernameNotFoundException(msg);
        }

        if (!appUser.getVerified()) {
            String msg = String.format("Login failed: User email '%s' has not been verified.", username);
            log.info(msg);
            throw new EmailNotVerifiedException();
        }

        if (!appUser.getEnabled()) {
            String msg = String.format("Login failed: User email '%s' has been disabled by an administrator.", username);
            log.info(msg);
            throw new AccountDisabledException();
        }

        return buildSpringUserFromAppUser(appUser);
	}
	
	public UserDetails buildSpringUserFromAppUser(AppUser appUser) {
        String username = appUser.getEmail();
		String password = appUser.getPassword();

		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for(Role role : appUser.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return new CustomUser(username, password, authorities, appUser);
	}

}
