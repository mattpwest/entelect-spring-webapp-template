package za.co.entelect.web.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;
import za.co.entelect.services.providers.dto.forms.user.UserRegistrationForm;
import za.co.entelect.web.forms.ResetPasswordForm;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserPasswordValidator implements Validator{

    private static final String ERROR_PASSWORD_MISMATCH = "validation.constraints.password.message";
    private static final String ERROR_PASSWORD_REQUIRED = "javax.validation.constraints.NotNull.message";

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationForm.class.isAssignableFrom(clazz) || ResetPasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String password, passwordConfirm;

        if(target instanceof UserRegistrationForm){
            UserRegistrationForm form = (UserRegistrationForm)target;
            password = form.getPassword();
            passwordConfirm = form.getPasswordConfirmation();
        }
        else{
            ResetPasswordForm form = (ResetPasswordForm)target;
            password = form.getPassword();
            passwordConfirm = form.getPasswordConfirmation();
        }
        if(StringUtils.isBlank(password)){
            errors.rejectValue("password", ERROR_PASSWORD_REQUIRED);
            return;
        }
        if(StringUtils.isBlank(passwordConfirm)){
            errors.rejectValue("passwordConfirmation", ERROR_PASSWORD_REQUIRED);
            return;
        }
        if(!password.equals(passwordConfirm)){
            errors.reject(ERROR_PASSWORD_MISMATCH);
            errors.rejectValue("password", ERROR_PASSWORD_MISMATCH);
            errors.rejectValue("passwordConfirmation", ERROR_PASSWORD_MISMATCH);
        }
    }
}
