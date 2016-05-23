package za.co.entelect.services.security;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import za.co.entelect.UnitTest;
import za.co.entelect.services.security.impl.UserDetailsServiceImpl;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.user.Role;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.services.security.exceptions.EmailNotVerifiedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class UserDetailsServiceImplTest {

    private static final String TEST_USER_EMAIL = "test@example.com";

    private UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();

    private AppUserDao appUserDao = mock(AppUserDao.class);

    private AppUser user;

    private Role role;

    @Before
    public void setUpTest() {
        reset(appUserDao);

        userDetailsService.setAppUserDao(appUserDao);

        role = new Role();
        role.setName("SIMPLE");
        role.setDescription("Basic user role.");

        user = new AppUser();
        user.setEmail(TEST_USER_EMAIL);
        user.setPassword("123");
        user.setVerified(true);

        user.getRoles().add(role);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsernameThrowsIfUserNotFound()
                throws UsernameNotFoundException {
        when(appUserDao.findByEmail(TEST_USER_EMAIL)).thenReturn(null);

        userDetailsService.loadUserByUsername(TEST_USER_EMAIL);
    }

    @Test(expected = EmailNotVerifiedException.class)
    public void testLoadUserByUsernameThrowsIfUserNotVerified()
                throws EmailNotVerifiedException {
        user.setVerified(false);
        when(appUserDao.findByEmail(TEST_USER_EMAIL)).thenReturn(user);

        userDetailsService.loadUserByUsername(TEST_USER_EMAIL);
    }

    @Test
    public void testLoadUserByUsername() {
        when(appUserDao.findByEmail(TEST_USER_EMAIL)).thenReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_USER_EMAIL);

        Assert.assertNotNull("User details should be returned.", userDetails);
    }
}
