package za.co.entelect.services.providers.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.entelect.comms.exceptions.MailDataException;
import za.co.entelect.comms.services.MailService;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.domain.entities.email.Email;
import za.co.entelect.dto.AppUserLiteDTO;
import za.co.entelect.persistence.email.EmailDao;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.persistence.user.ChangeEmailVerificationTokenDao;
import za.co.entelect.persistence.user.PasswordResetTokenDao;
import za.co.entelect.persistence.user.RoleDao;
import za.co.entelect.persistence.user.VerificationTokenDao;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.providers.dto.ResetPasswordTokenEmailDto;
import za.co.entelect.services.providers.dto.VerificationTokenEmailDto;
import za.co.entelect.services.providers.dto.forms.user.UserCreationForm;
import za.co.entelect.services.providers.dto.forms.user.UserRegistrationForm;
import za.co.entelect.services.providers.exceptions.EmailInUseException;
import za.co.entelect.services.providers.exceptions.TokenExpiredException;
import za.co.entelect.services.providers.exceptions.TokenNotFoundException;
import za.co.entelect.services.providers.history.HistoryService;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.user.ChangeEmailVerificationToken;
import za.co.entelect.domain.entities.user.PasswordResetToken;
import za.co.entelect.domain.entities.user.Role;
import za.co.entelect.domain.entities.user.VerificationToken;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class AppUserServiceImpl extends CRUDServiceImpl<AppUser, Long> implements AppUserService {

    @Autowired
    @Setter
    private AppUserDao appUserDao;

    @Autowired
    @Setter
    private RoleDao roleDao;

    @Autowired
    @Setter
    private PasswordResetTokenDao passwordResetTokenDao;

    @Autowired
    @Setter
    private VerificationTokenDao verificationTokenDao;

    @Autowired
    @Setter
    private MailService mailService;

    @Autowired
    @Setter
    private ConfigProperties config;

    @Autowired
    @Setter
    private HistoryService historyService;

    @Autowired
    @Setter
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Setter
    private ChangeEmailVerificationTokenDao changeEmailVerificationTokenDao;

    @Autowired
    @Setter
    private EmailDao emailDao;

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private Role adminRole;


    @PostConstruct
    public void setUp() {
        adminRole = roleDao.findByName("ROLE_ADMIN");
    }

    @Override
    @Transactional
    public AppUser createUser(UserCreationForm form, AppUser createdBy, String verifyUri, String passwordUri)
        throws EmailInUseException {

        if (appUserDao.findByEmail(form.getEmail()) != null) {
            throw new EmailInUseException();
        }

        AppUser user = createUserFromForm(form);
        user.setPassword("NoPasswordSet");
        user.setVerified(false);
        user.setVerificationToken(VerificationToken.generate(config.getSecurityTokensExpiryInHours()));
        user.setPasswordResetToken(PasswordResetToken.generate(config.getSecurityTokensExpiryInHours()));
        user = appUserDao.save(user);

        try {
            mailService.queueMail(user, "verification", "Account Verification",
                new VerificationTokenEmailDto(user.getFirstName(), user.getVerificationToken().getToken(), verifyUri), Email.Priority.HIGH);
        } catch (MailDataException ex) {
            log.warn(String.format("Failed to queue verification e-mail for %s.", user.getEmail()), ex);
        }

        try {
            mailService.queueMail(user, "forgotPassword", "Password Reset",
                new ResetPasswordTokenEmailDto(user.getFirstName(), user.getPasswordResetToken().getToken(), passwordUri), Email.Priority.HIGH);
        } catch (MailDataException ex) {
            log.warn(String.format("Failed to queue password reset token e-mail for %s.", user.getEmail()), ex);
        }

        historyService.created(user)
            .actor(createdBy)
            .save();

        return user;
    }

    @Override
    public AppUser createUserNoEmails(UserCreationForm form, AppUser createdBy, String verifyUri, String passwordUri,
                                      String passwordHash)
            throws EmailInUseException {

        if (appUserDao.findByEmail(form.getEmail()) != null) {
            throw new EmailInUseException();
        }

        AppUser user = createUserFromForm(form);
        user.setPassword(passwordHash);
        user.setVerified(true);
        user = appUserDao.save(user);

        historyService.created(user)
            .actor(createdBy)
            .save();

        return user;
    }

    @Override
    @Transactional
    public AppUser registerUser(UserRegistrationForm form, String verifyUri) throws EmailInUseException {
        AppUser existingUser = appUserDao.findByEmail(form.getEmail());
        if (existingUser != null) {
            throw new EmailInUseException("Email '" + form.getEmail() + "' has already been used.");
        }

        AppUser user = createUserFromForm(form);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setVerified(false);
        user.setVerificationToken(VerificationToken.generate(config.getSecurityTokensExpiryInHours()));
        log.info(String.format("Verification token has length: %d", user.getVerificationToken().getToken().length()));

        user = appUserDao.save(user);

        try {
            mailService.queueMail(user, "verification", "Account Verification",
                new VerificationTokenEmailDto(user.getFirstName(), user.getVerificationToken().getToken(), verifyUri), Email.Priority.HIGH);
        } catch (MailDataException ex) {
            log.warn(String.format("Failed to queue verification e-mail for %s.", user.getEmail()), ex);
        }

        historyService
            .registered(user)
            .save();

        return user;
    }

    @Override
    @Transactional
    public void verifyUser(String tokenString)
        throws TokenNotFoundException, TokenExpiredException {

        VerificationToken verificationToken = verificationTokenDao.findByToken(tokenString);

        if (verificationToken == null) {
            log.info(String.format("Attempt to verify with non-existent token: %s", tokenString));
            throw new TokenNotFoundException();
        }

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.info(String.format("Attempt to verify with token %s that expired at %s.",
                tokenString, verificationToken.getExpiresAt().format(DATE_TIME_FORMAT)));
            throw new TokenExpiredException();
        }

        verificationToken.getUser().setVerified(true);
        verificationToken.getUser().setVerificationToken(null);
        appUserDao.save(verificationToken.getUser());

        verificationTokenDao.delete(verificationToken);
    }

    @Override
    @Transactional
    public void resendVerificationEmail(AppUser user, String serverUri) {
        if (user.getVerified()) {
            return; // User already verified
        }

        LocalDateTime now = LocalDateTime.now();
        VerificationToken token = user.getVerificationToken();
        if (token != null && token.getExpiresAt().isAfter(now) && emailDao.findByTemplateAndSentAtIsNull("verification") != null) {
            log.warn("User {} attempted to resend verification email, but already has an unsent pending notification - not resending.");
            return;
        }

        if ((token == null) || (token.getExpiresAt().isBefore(now))) {
            user.setVerificationToken(VerificationToken.generate(config.getSecurityTokensExpiryInHours()));
            appUserDao.save(user);
        }

        try {
            mailService.queueMail(user, "verification", "Account Verification",
                new VerificationTokenEmailDto(user.getFirstName(), user.getVerificationToken().getToken(), serverUri), Email.Priority.HIGH);
        } catch (MailDataException ex) {
            log.warn(String.format("Failed to queue verification e-mail for %s.", user.getEmail()), ex);
        }
    }

    @Override
    @Transactional
    public void sendForgotPasswordToken(AppUser user, String forgotPasswordUri) {
        PasswordResetToken token = user.getPasswordResetToken();
        if ((token == null) || (token.getExpiresAt().isBefore(LocalDateTime.now()))) {
            token = PasswordResetToken.generate(config.getSecurityTokensExpiryInHours());
            user.setPasswordResetToken(token);
            appUserDao.save(user);
        }

        try {
            mailService.queueMail(user, "forgotPassword", "Password Reset",
                new ResetPasswordTokenEmailDto(user.getFirstName(), user.getPasswordResetToken().getToken(), forgotPasswordUri), Email.Priority.HIGH);
        } catch (MailDataException ex) {
            log.warn(String.format("Failed to queue password reset token e-mail for %s.", user.getEmail()), ex);
        }
    }

    @Override
    @Transactional
    public void resetPassword(AppUser user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        appUserDao.save(user);
    }

    @Override
    @Transactional
    public void resetPasswordWithToken(String tokenString, String newPasswordEncoded)
        throws TokenNotFoundException, TokenExpiredException {

        PasswordResetToken token = passwordResetTokenDao.findByToken(tokenString);
        if (token == null) {
            log.info(String.format("Attempt to reset forgotten password with non-existent token: %s", tokenString));
            throw new TokenNotFoundException();
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.info(String.format("Attempt to verify with token %s that expired at %s.",
                tokenString, token.getExpiresAt().format(DATE_TIME_FORMAT)));
            throw new TokenExpiredException();
        }

        AppUser user = token.getUser();
        user.setPassword(newPasswordEncoded);
        user.setPasswordResetToken(null);
        appUserDao.save(user);

        passwordResetTokenDao.delete(token);
    }

    @Override
    @Transactional
    public void updateUserRoles(Long userId, Long[] roles) {
        AppUser user = appUserDao.findOne(userId);
        if (user == null) {
            log.warn(String.format("Attempt to update non-existent user %d roles failed.", userId));
            return;
        }

        user.getRoles().clear();
        for (Long roleId : roles) {
            Role role = roleDao.findOne(roleId);
            user.getRoles().add(role);
        }

        appUserDao.save(user);
    }

    @Override
    public boolean isAdmin(AppUser user) {
        return hasRole(user, adminRole);
    }

    private boolean hasRole(AppUser user, Role targetRole) {
        if(user != null) {
            for (Role role : user.getRoles()) {
                if (role.equals(targetRole)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Page<AppUserLiteDTO> findAllLiteWithRoles(Pageable pageable) {
        Page page = appUserDao.findAllLite(pageable);
        addRoles(page);
        return page;
    }

    @Override
    public Page<AppUserLiteDTO> findAllLiteWithRolesByName(String name, Pageable pageable) {
        Page page = appUserDao.findAllLiteByName("%" + name + "%", pageable);
        addRoles(page);
        return page;
    }

    @Override
    @Transactional
    public AppUser findOne(Long appUserId) {
        return appUserDao.findOne(appUserId);
    }

    @Override
    @Transactional
    public ChangeEmailVerificationToken createChangeEmailToken(String newEmail, AppUser currentUser) {
        ChangeEmailVerificationToken token = ChangeEmailVerificationToken.generate(config.getSecurityTokensExpiryInHours());
        token.setNewEmail(newEmail);
        token.setOldEmail(currentUser.getEmail());
        token.setUser(currentUser);
        changeEmailVerificationTokenDao.save(token);
        return token;
    }

    @Override
    @Transactional
    public ChangeEmailVerificationToken changeEmail(String token) throws TokenNotFoundException {
        ChangeEmailVerificationToken verificationToken = changeEmailVerificationTokenDao.findByToken(token);
        if(verificationToken == null) {
            log.info(String.format("Attempt to verify with non-existent token: %s", token));
            throw new TokenNotFoundException();
        }

        if(LocalDateTime.now().isBefore(verificationToken.getExpiresAt()) && !verificationToken.isUsed()) {
            AppUser appuser = verificationToken.getUser();
            appuser.setEmail(verificationToken.getNewEmail());
            appuser.setChangeEmailEnabled(false);
            appUserDao.save(appuser);

            verificationToken.setUsed(true);
            changeEmailVerificationTokenDao.save(verificationToken);

            return null;
        }

        return verificationToken;
    }

    @Override
    public void cancelEmailChange(String token) {
        ChangeEmailVerificationToken verificationToken = changeEmailVerificationTokenDao.findByToken(token);
        if(verificationToken != null) {
            verificationToken.setUsed(true);
            changeEmailVerificationTokenDao.save(verificationToken);
        }
    }

    @Override
    public AppUser findByEmail(String email) {
        return appUserDao.findByEmail(email);
    }

    protected void addRoles(Page page) {
        List<AppUserLiteDTO> users = (List<AppUserLiteDTO>) page.getContent();

        for (AppUserLiteDTO appUserLite : users) {
            appUserLite.setRoles(roleDao.findRoleNamesByUser(appUserLite.getId()));
        }
    }

    protected AppUser createUserFromForm(UserCreationForm form) {
        AppUser user = new AppUser();
        user.setEmail(form.getEmail());
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        return user;
    }
}
