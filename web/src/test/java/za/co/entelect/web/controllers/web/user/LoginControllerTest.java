package za.co.entelect.web.controllers.web.user;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.services.security.dto.CustomUser;
import za.co.entelect.test.ControllerBase;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class LoginControllerTest extends ControllerBase {

    private AppUser testUser;
    private UserDetails userDetails;

    @Before
    public void setUpTest() {
        testUser = new AppUser();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("SIMPLE"));

        userDetails = new CustomUser("test@example.com", "123", authorities, testUser);
    }

    @Test
    public void testViewLogin() throws Exception {
        mockMvc.perform(get("/login")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/login"));
    }

    @Test
    public void testViewLoginWithErrorDisplay() throws Exception {
        Exception ex = new Exception("Danger, danger!");
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.putValue("SPRING_SECURITY_LAST_EXCEPTION", ex);

        mockMvc.perform(get("/login")
                .session(mockHttpSession)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/login"))
            .andExpect(model().attribute("error", is(not(nullValue()))));
    }

    @Test
    public void testLoginPassed() throws Exception {
        mockMvc.perform(get("/loginPassed")
                    .with(user(userDetails)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testLoginFailed() throws Exception {
        mockMvc.perform(get("/loginFailed"))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/login"))
            .andExpect(model().attribute("error", is(not(nullValue()))));
    }
}
