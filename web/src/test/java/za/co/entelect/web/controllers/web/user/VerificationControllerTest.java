package za.co.entelect.web.controllers.web.user;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.user.VerificationToken;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.persistence.user.VerificationTokenDao;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class VerificationControllerTest extends ControllerBase {

    private static final String TEST_EMAIL = "testy@example.com";
    private static final String TEST_TOKEN = "ABCDEF123456";

    @Autowired
    private AppUserService appUserServiceMock;

    @Autowired
    private AppUserDao appUserDaoMock;

    @Autowired
    private VerificationTokenDao verificationTokenDaoMock;

    private AppUser testUser;
    private UserDetails userDetails;

    @Before
    public void setUpTest() {
        reset(appUserServiceMock);
        reset(appUserDaoMock);
        reset(verificationTokenDaoMock);

        testUser = new AppUser();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("SIMPLE"));

        userDetails = new CustomUser(TEST_EMAIL, "123", authorities, testUser);
    }

    @Test
    public void testViewVerification() throws Exception {
        mockMvc.perform(get("/verification")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/verification"));
    }

    @Test
    public void testViewVerificationResend() throws Exception {
        mockMvc.perform(get("/verification/resend")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/verificationResend"));
    }

    @Test
    public void testVerificationResendWithInvalidInput() throws Exception {
        mockMvc.perform(post("/verification/resend")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/verificationResend"));

        verify(appUserDaoMock, times(0)).findByEmail(TEST_EMAIL);
    }

    @Test
    public void testVerificationResendWithEmailNotFound() throws Exception {
        when(appUserDaoMock.findByEmail(TEST_EMAIL)).
            thenReturn(null);

        mockMvc.perform(post("/verification/resend")
                .param("email", TEST_EMAIL)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/verificationResend"))
            .andExpect(model().attribute("message", is(not(nullValue()))));

        verify(appUserServiceMock, times(0)).resendVerificationEmail(eq(testUser), any(String.class));
    }

    @Test
    public void testVerificationResend() throws Exception {
        when(appUserDaoMock.findByEmail(TEST_EMAIL)).
            thenReturn(testUser);

        mockMvc.perform(post("/verification/resend")
                .param("email", TEST_EMAIL)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/verificationResend"))
            .andExpect(model().attribute("message", is(not(nullValue()))));

        verify(appUserServiceMock).
            resendVerificationEmail(eq(testUser), any(String.class));
    }

    @Test
    public void testEmailVerifyTokenNotFound() throws Exception {
        doThrow(new TokenNotFoundException()).when(appUserServiceMock).verifyUser(TEST_TOKEN);

        mockMvc.perform(get("/verification/verify")
                .param("token", TEST_TOKEN))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/verification/resend"))
            .andExpect(flash().attribute("message", is(not(nullValue()))));
    }

    @Test
    public void testEmailVerifyTokenExpired() throws Exception {
        doThrow(new TokenExpiredException()).when(appUserServiceMock).verifyUser(TEST_TOKEN);

        VerificationToken token = new VerificationToken();
        token.setToken(TEST_TOKEN);
        token.setUser(testUser);
        when(verificationTokenDaoMock.findByToken(TEST_TOKEN)).thenReturn(token);

        mockMvc.perform(get("/verification/verify")
                .param("token", TEST_TOKEN))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"))
            .andExpect(flash().attribute("error", is(not(nullValue()))));

        verify(verificationTokenDaoMock).findByToken(TEST_TOKEN);
        verify(appUserServiceMock).resendVerificationEmail(eq(testUser), any(String.class));
    }

    @Test
    public void testEmailVerify() throws Exception {
        mockMvc.perform(get("/verification/verify")
                .param("token", TEST_TOKEN))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"))
            .andExpect(flash().attribute("message", is(not(nullValue()))));

        verify(appUserServiceMock).verifyUser(TEST_TOKEN);
    }
}
