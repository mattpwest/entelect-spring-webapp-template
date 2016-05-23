package za.co.entelect.test;

import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import za.co.entelect.IntegrationTest;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.security.dto.CustomUser;
import za.co.entelect.test.config.TestRootConfig;
import za.co.entelect.web.config.SpringMVCConfig;
import za.co.entelect.web.config.SpringSecurityConfig;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestRootConfig.class, SpringMVCConfig.class, SpringSecurityConfig.class})
@WebAppConfiguration
public abstract class ControllerBase {

    static {
        System.setProperty("ENTELECT_ENV", "test");
    }

    protected static final String USER_NORMAL_EMAIL = "test@example.com";
    protected static final String USER_ADMIN_EMAIL = "admin@example.com";
    protected static final String TEST_PASSWORD = "123";

    protected UserDetails userNormal;
    protected UserDetails userAdmin;

    protected MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Before
    public void setUpTestContext() {
        reset(appUserService);

        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.
                    webAppContextSetup(webApplicationContext).
                    apply(springSecurity()).
                build();
        }

        setUpNormalUser();
        setUpAdminUser();
    }

    private void setUpAdminUser() {
        AppUser adminUser = new AppUser();
        adminUser.setId(1L);
        adminUser.setEmail(USER_ADMIN_EMAIL);
        adminUser.setFirstName("Normal");
        adminUser.setLastName("Normal");

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        userAdmin = new CustomUser(USER_ADMIN_EMAIL, TEST_PASSWORD, authorities, adminUser);

        when(appUserService.findOne(eq(1L))).thenReturn(adminUser);
    }

    private void setUpNormalUser() {
        AppUser normalUser = new AppUser();
        normalUser.setId(2L);
        normalUser.setEmail(USER_NORMAL_EMAIL);
        normalUser.setFirstName("Admin");
        normalUser.setLastName("Admin");

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        userNormal = new CustomUser(USER_NORMAL_EMAIL, TEST_PASSWORD, authorities, normalUser);

        when(appUserService.findOne(eq(2L))).thenReturn(normalUser);
    }
}
