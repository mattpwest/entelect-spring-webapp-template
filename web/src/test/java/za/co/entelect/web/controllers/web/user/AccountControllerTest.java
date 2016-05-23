package za.co.entelect.web.controllers.web.user;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.security.dto.CustomUser;
import za.co.entelect.test.ControllerBase;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class AccountControllerTest extends ControllerBase {

    @Autowired
    private AppUserService appUserService;

    private AppUser testUser;
    private UserDetails userDetails;

    @Before
    public void setUpTest() {
        reset(appUserService);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("SIMPLE"));
        testUser = new AppUser();
        testUser.setId(1L);
        userDetails = new CustomUser("test@example.com", "123", authorities, testUser);
    }

    @Test
    public void testViewAccount() throws Exception {
        when(appUserService.findOne(1L)).thenReturn(testUser);

        mockMvc.perform(get("/account")
                .with(csrf())
                .with(user(userDetails)))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/account"));
    }

    @Test
    public void testResetPasswordWithInvalidInput() throws Exception {
        mockMvc.perform(post("/resetPassword")
                            .param("password", "123")
                            .with(csrf())
                            .with(user(userDetails)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("error", is(not(nullValue()))))
            .andExpect(redirectedUrl("/account"));
    }

    @Test
    public void testResetPasswordWithPasswordMismatch() throws Exception {
        mockMvc.perform(post("/resetPassword")
                            .param("password", "123")
                            .param("passwordConfirmation", "456")
                            .with(csrf())
                            .with(user(userDetails)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("error", is(not(nullValue()))))
            .andExpect(redirectedUrl("/account"));
    }

    @Test
    public void testResetPassword() throws Exception {
        mockMvc.perform(post("/resetPassword")
                            .param("password", "123")
                            .param("passwordConfirmation", "123")
                            .with(csrf())
                            .with(user(userDetails)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("message", is(not(nullValue()))))
            .andExpect(redirectedUrl("/account"));

        verify(appUserService).resetPassword(eq(testUser), any(String.class));
    }
}
