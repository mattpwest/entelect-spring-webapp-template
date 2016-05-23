package za.co.entelect.web.controllers.web.user;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.providers.exceptions.TokenExpiredException;
import za.co.entelect.services.providers.exceptions.TokenNotFoundException;
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
import static org.mockito.Mockito.times;
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

public class ForgotPasswordControllerTest extends ControllerBase {

    @Autowired
    private AppUserDao appUserDaoMock;

    @Autowired
    private AppUserService appUserService;

    private AppUser testUser;
    private UserDetails userDetails;

    @Before
    public void setUpTest() {
        reset(appUserDaoMock);
        reset(appUserService);

        testUser = new AppUser();
        testUser.setId(1L);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("SIMPLE"));

        userDetails = new CustomUser("test@example.com", "123", authorities, testUser);
    }

    @Test
    public void testForgotPasswordPage() throws Exception {
        when(appUserService.findOne(1L)).thenReturn(testUser);

        mockMvc.perform(get("/forgotPassword")
                    .with(csrf())
                    .with(user(userDetails)))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/forgotPassword"));
    }

    @Test
    public void testForgotPasswordFormInvalid() throws Exception {
        mockMvc.perform(post("/forgotPassword")
                        .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/forgotPassword"));

        verify(appUserDaoMock, times(0)).findByEmail(any(String.class));
    }

    @Test
    public void testForgotPasswordFormSubmission() throws Exception {
        when(appUserDaoMock.findByEmail(eq("test@example.com"))).
            thenReturn(testUser);

        mockMvc.perform(post("/forgotPassword")
                    .param("email", "test@example.com")
                    .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("message", is(not(nullValue()))))
            .andExpect(redirectedUrl("/login"));

        verify(appUserDaoMock).findByEmail(eq("test@example.com"));
        verify(appUserService).sendForgotPasswordToken(eq(testUser), any(String.class));
    }

    @Test
    public void testForgotPasswordFormSubmissionWithNonExistentEmail() throws Exception {
        when(appUserDaoMock.findByEmail(eq("test@example.com"))).
            thenReturn(null);

        mockMvc.perform(post("/forgotPassword")
            .param("email", "test@example.com")
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("message", is(not(nullValue()))))
            .andExpect(redirectedUrl("/login"));

        verify(appUserDaoMock).findByEmail(eq("test@example.com"));
        verify(appUserService, times(0)).sendForgotPasswordToken(eq(testUser), any(String.class));
    }

    @Test
    public void testForgotPasswordResetPage() throws Exception {
        when(appUserService.findOne(1L)).thenReturn(testUser);

        mockMvc.perform(get("/forgotPassword/reset")
                        .param("token", "123")
                        .with(csrf())
                        .with(user(userDetails)))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/forgotPasswordReset"));
    }

    @Test
    public void testForgotPasswordResetWithInvalidInput() throws Exception {
        mockMvc.perform(post("/forgotPassword/reset")
                            .param("password", "123")
                            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/forgotPasswordReset"));
    }

    @Test
    public void testForgotPasswordResetTokenNotFound() throws Exception {
        doThrow(TokenNotFoundException.class).
            when(appUserService).resetPasswordWithToken(eq("ABCDEF"), any(String.class));

        mockMvc.perform(post("/forgotPassword/reset")
                            .param("password", "123")
                            .param("passwordConfirmation", "123")
                            .param("passwordResetToken", "ABCDEF")
                            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("error", is(not(nullValue()))))
            .andExpect(redirectedUrl("/forgotPassword"));
    }

    @Test
    public void testForgotPasswordResetTokenExpired() throws Exception {
        doThrow(TokenExpiredException.class).
            when(appUserService).resetPasswordWithToken(eq("ABCDEF"), any(String.class));

        mockMvc.perform(post("/forgotPassword/reset")
                            .param("password", "123")
                            .param("passwordConfirmation", "123")
                            .param("passwordResetToken", "ABCDEF")
                            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("error", is(not(nullValue()))))
            .andExpect(redirectedUrl("/forgotPassword"));
    }

    @Test
    public void testForgotPasswordReset() throws Exception {
        mockMvc.perform(post("/forgotPassword/reset")
                            .param("password", "123")
                            .param("passwordConfirmation", "123")
                            .param("passwordResetToken", "ABCDEF")
                            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("message", is(not(nullValue()))))
            .andExpect(redirectedUrl("/login"));

        verify(appUserService).resetPasswordWithToken(eq("ABCDEF"), any(String.class));
    }
}
