package za.co.entelect.web.validators;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.data.rest.core.ValidationErrors;
import org.springframework.validation.Errors;
import za.co.entelect.UnitTest;
import za.co.entelect.services.providers.dto.forms.user.ResetPasswordForm;
import za.co.entelect.services.providers.dto.forms.user.UserRegistrationForm;

@Category(UnitTest.class)
public class UserPasswordValidatorTest {

    private UserPasswordValidator classUnderTest = new UserPasswordValidator();

    @Test
    public void testInvalidForm() {
        Object form = new Object();
        Errors errors = new ValidationErrors("form", form, null);

        classUnderTest.validate(form, errors);

        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals(1, errors.getGlobalErrorCount());
    }

    @Test
    public void testValidResetPasswordForm() {
        ResetPasswordForm form = new ResetPasswordForm();
        form.setPassword("test123");
        form.setPasswordConfirmation("test123");
        Errors errors = new ValidationErrors("form", form, null);

        classUnderTest.validate(form, errors);

        Assert.assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidUserRegistrationForm() {
        UserRegistrationForm form = new UserRegistrationForm();
        form.setPassword("test123");
        form.setPasswordConfirmation("test123");
        Errors errors = new ValidationErrors("form", form, null);

        classUnderTest.validate(form, errors);

        Assert.assertFalse(errors.hasErrors());
    }

    @Test
    public void testBlankPasswordFails() {
        ResetPasswordForm form = new ResetPasswordForm();
        form.setPassword("");
        form.setPasswordConfirmation("test123");
        Errors errors = new ValidationErrors("form", form, null);

        classUnderTest.validate(form, errors);

        Assert.assertTrue(errors.hasFieldErrors("password"));
    }

    @Test
    public void testBlankPasswordConfirmationFails() {
        ResetPasswordForm form = new ResetPasswordForm();
        form.setPassword("test123");
        form.setPasswordConfirmation("");
        Errors errors = new ValidationErrors("form", form, null);

        classUnderTest.validate(form, errors);

        Assert.assertTrue(errors.hasFieldErrors("passwordConfirmation"));
    }

    @Test
    public void testPasswordMismatchFails() {
        ResetPasswordForm form = new ResetPasswordForm();
        form.setPassword("test123");
        form.setPasswordConfirmation("test321");
        Errors errors = new ValidationErrors("form", form, null);

        classUnderTest.validate(form, errors);

        Assert.assertTrue(errors.hasFieldErrors("password"));
        Assert.assertTrue(errors.hasFieldErrors("passwordConfirmation"));
        Assert.assertEquals(1, errors.getGlobalErrorCount());
    }
}
