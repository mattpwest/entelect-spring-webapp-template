package za.co.entelect.web.forms.captcha;

import lombok.Data;
import za.co.entelect.services.providers.dto.forms.user.UserRegistrationForm;

@Data
public class RecaptchaUserRegistrationForm extends UserRegistrationForm implements RecaptchaForm {
    private String recaptchaResponse;
}
