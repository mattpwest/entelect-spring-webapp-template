package za.co.entelect.web.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.data.rest.core.ValidationErrors;
import org.springframework.validation.Errors;
import za.co.entelect.UnitTest;
import za.co.entelect.services.providers.RecaptchaService;
import za.co.entelect.services.providers.exceptions.RecaptchaServiceException;
import za.co.entelect.web.forms.captcha.RecaptchaUserRegistrationForm;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class RecaptchaFormValidatorTest {

    private HttpServletRequest mockHttpRequest = mock(HttpServletRequest.class);
    private RecaptchaService mockRecaptchaService = mock(RecaptchaService.class);
    private RecaptchaFormValidator classUnderTest = new RecaptchaFormValidator(mockHttpRequest, mockRecaptchaService);

    @Before
    public void setUp() {
        reset(mockHttpRequest);
        reset(mockRecaptchaService);
    }

    @Test
    public void testSuccessCase() {
        when(mockHttpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(mockRecaptchaService.isResponseValid(any(String.class), any(String.class))).thenReturn(true);

        RecaptchaUserRegistrationForm form = new RecaptchaUserRegistrationForm();
        form.setRecaptchaResponse("123");
        Errors errors = new ValidationErrors("form", form, null);

        classUnderTest.validate(form, errors);

        Assert.assertFalse(errors.hasErrors());
    }

    @Test
    public void testRecaptchaRespondsWithFailure() {
        when(mockHttpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(mockRecaptchaService.isResponseValid(any(String.class), any(String.class))).thenReturn(false);

        RecaptchaUserRegistrationForm form = new RecaptchaUserRegistrationForm();
        form.setRecaptchaResponse("123");
        Errors errors = new ValidationErrors("form", form, null);

        classUnderTest.validate(form, errors);

        Assert.assertTrue(errors.hasFieldErrors("recaptchaResponse"));
    }

    @Test
    public void testRecaptchaExceptionFails() {
        when(mockHttpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(mockRecaptchaService.isResponseValid(any(String.class), any(String.class)))
            .thenThrow(new RecaptchaServiceException("error", null));

        RecaptchaUserRegistrationForm form = new RecaptchaUserRegistrationForm();
        form.setRecaptchaResponse("123");
        Errors errors = new ValidationErrors("form", form, null);

        classUnderTest.validate(form, errors);

        Assert.assertTrue(errors.hasGlobalErrors());
    }

    @Test
    public void testNoRecaptchaResponseFails() {
        when(mockHttpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(mockRecaptchaService.isResponseValid(any(String.class), any(String.class))).thenReturn(true);

        RecaptchaUserRegistrationForm form = new RecaptchaUserRegistrationForm();
        Errors errors = new ValidationErrors("form", form, null);

        classUnderTest.validate(form, errors);

        Assert.assertTrue(errors.hasFieldErrors("recaptchaResponse"));
    }

    @Test
    public void testRemoteIpForProxy() {
        when(mockHttpRequest.getRemoteAddr()).thenReturn(null);
        when(mockHttpRequest.getHeader(eq("x-forwarded-for"))).thenReturn("127.0.0.1");
        when(mockRecaptchaService.isResponseValid(any(String.class), any(String.class))).thenReturn(true);

        RecaptchaUserRegistrationForm form = new RecaptchaUserRegistrationForm();
        form.setRecaptchaResponse("123");
        Errors errors = new ValidationErrors("form", form, null);

        classUnderTest.validate(form, errors);

        Assert.assertFalse(errors.hasErrors());
    }
}
