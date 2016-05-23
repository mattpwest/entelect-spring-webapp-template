package za.co.entelect.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.projection.ProjectionFactory;
import org.thymeleaf.spring4.SpringTemplateEngine;
import za.co.entelect.comms.services.MailService;
import za.co.entelect.comms.services.MailTestService;
import za.co.entelect.config.EnvironmentConfiguration;
import za.co.entelect.persistence.email.EmailDao;
import za.co.entelect.persistence.email.TemplateDao;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.persistence.user.PasswordResetTokenDao;
import za.co.entelect.persistence.user.RoleDao;
import za.co.entelect.persistence.user.VerificationTokenDao;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.providers.RecaptchaService;
import za.co.entelect.services.providers.email.TemplateService;
import za.co.entelect.services.providers.history.HistoryService;
import za.co.entelect.services.providers.history.HistoryTemplateService;
import za.co.entelect.services.security.ProjectSecurityService;
import za.co.entelect.services.security.UserSecurityService;
import za.co.entelect.services.security.impl.ProjectSecurityServiceImpl;
import za.co.entelect.services.security.impl.UserDetailsServiceImpl;
import za.co.entelect.services.security.impl.UserSecurityServiceImpl;
import za.co.entelect.web.forms.captcha.RecaptchaForm;
import za.co.entelect.web.validators.RecaptchaFormValidator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import(EnvironmentConfiguration.class)
public class TestRootConfig {

    @Bean
    public AppUserDao appUserDao() {
        return mock(AppUserDao.class);
    }

    @Bean
    public EmailDao emailDao() {
        return mock(EmailDao.class);
    }

    @Bean
    public PasswordResetTokenDao passwordResetTokenDao() {
        return mock(PasswordResetTokenDao.class);
    }

    @Bean
    public RoleDao roleDao() {
        return mock(RoleDao.class);
    }

    @Bean
    public VerificationTokenDao verificationTokenDao() {
        return mock(VerificationTokenDao.class);
    }

    @Bean
    public MailService mailService() {
        return mock(MailService.class);
    }

    @Bean
    public MailTestService mailTestService() {
        return mock(MailTestService.class);
    }

    @Bean
    public UserDetailsServiceImpl userDetailsService() {
        return mock(UserDetailsServiceImpl.class);
    }

    @Bean
    public AppUserService appUserService() {
        return mock(AppUserService.class);
    }

    @Bean
    public UserSecurityService userSecurity() {
        return new UserSecurityServiceImpl();
    }

    @Bean
    public ProjectSecurityService teamSecurity() {
        return new ProjectSecurityServiceImpl();
    }

    @Bean
    public RecaptchaService recaptchaService() {
        RecaptchaService recaptchaService = mock(RecaptchaService.class);
        when(recaptchaService.isResponseValid(any(String.class), any(String.class))).thenReturn(true);
        return recaptchaService;
    }

    @Bean
    public RecaptchaFormValidator recaptchaFormValidator() {
        RecaptchaFormValidator recaptchaFormValidator = mock(RecaptchaFormValidator.class);
        when(recaptchaFormValidator.supports(any(RecaptchaForm.class.getClass()))).thenReturn(true);
        return recaptchaFormValidator;
    }

    @Bean
    public TemplateDao templateDao() {
        return mock(TemplateDao.class);
    }

    @Bean
    public SpringTemplateEngine emailTemplateEngine() {
        return mock(SpringTemplateEngine.class);
    }

    @Bean
    public ProjectionFactory projectionFactory() {
        return mock(ProjectionFactory.class);
    }

    @Bean
    public HistoryService historyService() {
        return mock(HistoryService.class);
    }

    @Bean
    public HistoryTemplateService historyTemplateService() {
        return mock(HistoryTemplateService.class);
    }

    @Bean
    public TemplateService templateService() {
        return mock(TemplateService.class);
    }
}
