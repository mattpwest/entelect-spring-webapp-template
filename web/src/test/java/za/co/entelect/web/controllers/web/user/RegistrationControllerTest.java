package za.co.entelect.web.controllers.web.user;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.providers.dto.forms.user.UserRegistrationForm;
import za.co.entelect.services.providers.exceptions.EmailInUseException;
import za.co.entelect.test.ControllerBase;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class RegistrationControllerTest extends ControllerBase {

    private static final String TEST_NAME = "Yugi";
    private static final String TEST_SURNAME = "Test";
    private static final String TEST_DISPLAY_NAME = "Testy";
    private static final String TEST_EMAIL = "testy@example.com";
    private static final String TEST_PASSWORD = "123";

    @Autowired
    private AppUserService appUserService;

    private AppUser testUser;

    @Before
    public void setUpTest() {
        reset(appUserService);

        testUser = new AppUser();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("SIMPLE"));
    }

    @Test
    public void testViewRegistration() throws Exception {
        mockMvc.perform(get("/register")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/register"));
    }

    @Test
    public void testRegisterWithInvalidInput() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/register"));
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        mockMvc.perform(post("/register")
                .param("firstName", TEST_NAME)
                .param("lastName", TEST_SURNAME)
                .param("displayName", TEST_DISPLAY_NAME)
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .param("passwordConfirmation", TEST_PASSWORD)
                .param("termsAndConditionsAccepted", "true")

                // In reality this will be: g-recaptcha-response, but filters to fix it likely won't work in test
                .param("recaptchaResponse", "abc")
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/verification"));

        verify(appUserService).registerUser(any(UserRegistrationForm.class), any(String.class));
    }

    @Test
    public void testRegisterFailureWithEmailInUse() throws Exception {
        doThrow(new EmailInUseException()).when(appUserService).registerUser(any(UserRegistrationForm.class), any(String.class));

        mockMvc.perform(post("/register")
                .param("firstName", TEST_NAME)
                .param("lastName", TEST_SURNAME)
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .param("passwordConfirmation", TEST_PASSWORD)
                .param("termsAndConditionsAccepted", "true")
                .param("recaptchaResponse", "abc") // In reality this will be: g-recaptcha-response
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/register"));
    }
}
