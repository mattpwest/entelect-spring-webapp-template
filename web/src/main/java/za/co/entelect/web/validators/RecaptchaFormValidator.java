package za.co.entelect.web.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;
import za.co.entelect.services.providers.RecaptchaService;
import za.co.entelect.services.providers.exceptions.RecaptchaServiceException;
import za.co.entelect.web.forms.captcha.RecaptchaForm;

import javax.servlet.http.HttpServletRequest;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class RecaptchaFormValidator implements Validator {

    private static final String ERROR_RECAPTCHA_INVALID = "recaptcha.error.invalid";
    private static final String ERROR_RECAPTCHA_REQUIRED = "recaptcha.error.required";
    private static final String ERROR_RECAPTCHA_UNAVAILABLE = "recaptcha.error.unavailable";

    private final HttpServletRequest httpServletRequest;
    private final RecaptchaService recaptchaService;

    @Autowired
    public RecaptchaFormValidator(HttpServletRequest httpServletRequest, RecaptchaService recaptchaService) {
        this.httpServletRequest = httpServletRequest;
        this.recaptchaService = recaptchaService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return RecaptchaForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String remoteIp = getRemoteIp(httpServletRequest);
        log.info("Validating Recaptcha response for request from {}", remoteIp);

        RecaptchaForm form = (RecaptchaForm) target;
        try {
            if (form.getRecaptchaResponse() == null || form.getRecaptchaResponse().isEmpty()) {
                log.debug("Failed: No Recaptcha response in form.");
                errors.rejectValue("recaptchaResponse", ERROR_RECAPTCHA_REQUIRED);
            } else if (!recaptchaService.isResponseValid(form.getRecaptchaResponse(), remoteIp)) {
                log.debug("Failed: Recaptcha response is invalid.");
                errors.reject(ERROR_RECAPTCHA_INVALID);
                errors.rejectValue("recaptchaResponse", ERROR_RECAPTCHA_INVALID);
            }
        } catch (RecaptchaServiceException e) {
            log.debug("Failed: Recaptcha service was unavailable.", e);
            errors.reject(ERROR_RECAPTCHA_UNAVAILABLE);
        }

        log.debug("Success - Recaptcha response is valid.");
    }

    private String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
