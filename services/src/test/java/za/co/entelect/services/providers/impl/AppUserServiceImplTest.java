package za.co.entelect.services.providers.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import za.co.entelect.UnitTest;
import za.co.entelect.comms.exceptions.MailDataException;
import za.co.entelect.comms.services.MailService;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.domain.entities.email.Email;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.user.PasswordResetToken;
import za.co.entelect.domain.entities.user.Role;
import za.co.entelect.domain.entities.user.VerificationToken;
import za.co.entelect.persistence.email.EmailDao;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.persistence.user.PasswordResetTokenDao;
import za.co.entelect.persistence.user.RoleDao;
import za.co.entelect.persistence.user.VerificationTokenDao;
import za.co.entelect.services.providers.dto.forms.user.UserCreationForm;
import za.co.entelect.services.providers.dto.forms.user.UserRegistrationForm;
import za.co.entelect.services.providers.exceptions.EmailInUseException;
import za.co.entelect.services.providers.exceptions.TokenExpiredException;
import za.co.entelect.services.providers.exceptions.TokenNotFoundException;
import za.co.entelect.services.providers.history.HistoryService;
import za.co.entelect.services.providers.history.ObjectHistoryEventBuilder;
import za.co.entelect.services.providers.impl.AppUserServiceImpl;

import java.time.LocalDateTime;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class AppUserServiceImplTest {

    private static final String TEST_USER_EMAIL = "test@example.com";
    private static final String TEST_FIRST_NAME = "Name";
    private static final String TEST_LAST_NAME = "Surname";
    private static final String TEST_DISPLAY_NAME = "DisplayName";
    private static final String TEST_VERIFY_URI = "http://localhost:8080/verify";
    private static final String TEST_VERIFY_TOKEN = "ABCDEF1234567890ABCDEF1234567890";
    private static final String TEST_PASSWORD_URI = "http://localhost:8080/forgotPassword";
    private static final String TEST_PASSWORD_TOKEN = "01A23B45C67E89F01A23B45C67E89F";

    private AppUserServiceImpl appUserService = new AppUserServiceImpl();
    private AppUserDao appUserDao = mock(AppUserDao.class);
    private PasswordResetTokenDao passwordResetTokenDao = mock(PasswordResetTokenDao.class);
    private RoleDao roleDao = mock(RoleDao.class);
    private VerificationTokenDao verificationTokenDao = mock(VerificationTokenDao.class);
    private MailService mailService = mock(MailService.class);
    private ConfigProperties configProperties = mock(ConfigProperties.class);
    private HistoryService historyService = mock(HistoryService.class);
    private ObjectHistoryEventBuilder historyEventBuilder = mock(ObjectHistoryEventBuilder.class);
    private EmailDao emailDao = mock(EmailDao.class);

    private UserCreationForm testUserForm;
    private UserRegistrationForm testUserRegisterForm;
    private AppUser testUser;
    private AppUser testUserWithAdmin;
    private Role testRole;
    private VerificationToken verifyToken;
    private PasswordResetToken passwordToken;

    @Before
    public void testSetUp() {
        // Reset mocks
        reset(appUserDao);
        reset(passwordResetTokenDao);
        reset(roleDao);
        reset(verificationTokenDao);
        reset(mailService);
        reset(configProperties);
        reset(historyService);
        reset(historyEventBuilder);
        reset(emailDao);

        // Reinitialise the service under test
        appUserService = new AppUserServiceImpl();
        appUserService.setAppUserDao(appUserDao);
        appUserService.setPasswordResetTokenDao(passwordResetTokenDao);
        appUserService.setRoleDao(roleDao);
        appUserService.setVerificationTokenDao(verificationTokenDao);
        appUserService.setMailService(mailService);
        appUserService.setConfig(configProperties);
        appUserService.setHistoryService(historyService);
        appUserService.setEmailDao(emailDao);
        appUserService.setPasswordEncoder(new BCryptPasswordEncoder());

        // Setup default test data
        verifyToken = new VerificationToken();
        verifyToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        verifyToken.setToken(TEST_VERIFY_TOKEN);

        testUser = new AppUser();
        testUser.setVerified(false);

        passwordToken = new PasswordResetToken();
        passwordToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        passwordToken.setToken(TEST_PASSWORD_TOKEN);

        verifyToken.setUser(testUser);
        passwordToken.setUser(testUser);
        testUser.setEmail(TEST_USER_EMAIL);
        testUser.setVerificationToken(verifyToken);
        testUser.setPasswordResetToken(passwordToken);

        testUserWithAdmin = new AppUser();
        testUserWithAdmin.setEmail(TEST_USER_EMAIL);
        testUserWithAdmin.setVerificationToken(verifyToken);
        testUserWithAdmin.setPasswordResetToken(passwordToken);

        testRole = new Role();

        testUserForm = new UserCreationForm();
        testUserForm.setEmail(TEST_USER_EMAIL);

        testUserRegisterForm = new UserRegistrationForm();
        testUserRegisterForm.setEmail(TEST_USER_EMAIL);
        testUserRegisterForm.setPassword("123");

        // History service mocks
        when(historyService.created(any(AppUser.class))).thenReturn(historyEventBuilder);
        when(historyService.registered(any(AppUser.class))).thenReturn(historyEventBuilder);

        when(historyEventBuilder.actor(any())).thenReturn(historyEventBuilder);
        when(historyEventBuilder.subject(any())).thenReturn(historyEventBuilder);
    }

    @Test(expected = EmailInUseException.class)
    public void testCreateUserAlreadyExists() throws EmailInUseException {
        when(appUserDao.findByEmail(TEST_USER_EMAIL)).thenReturn(testUser);

        appUserService.createUser(testUserForm, testUserWithAdmin,
            TEST_VERIFY_URI, TEST_PASSWORD_URI);
    }

    @Test
    public void testCreateUser() throws Exception {
        when(appUserDao.findByEmail(TEST_USER_EMAIL)).thenReturn(null);
        when(appUserDao.save(any(AppUser.class))).thenReturn(testUser);

        appUserService.createUser(testUserForm, testUserWithAdmin,
            TEST_VERIFY_URI, TEST_PASSWORD_URI);

        verify(appUserDao).save(any(AppUser.class));
        verify(mailService, times(2)).queueMail(any(AppUser.class), any(String.class), any(String.class), any(Object.class), any(Email.Priority.class));
    }

    @Test(expected = EmailInUseException.class)
    public void testRegisterUserAlreadyExists() throws EmailInUseException {
        when(appUserDao.findByEmail(TEST_USER_EMAIL)).thenReturn(testUser);

        appUserService.registerUser(testUserRegisterForm, TEST_VERIFY_URI);
    }

    @Test
    public void testRegisterUser() throws EmailInUseException, MailDataException {
        when(appUserDao.save(any(AppUser.class))).thenReturn(testUser);

        appUserService.registerUser(testUserRegisterForm, TEST_VERIFY_URI);

        verify(appUserDao).save(testUser);
        verify(mailService).queueMail(eq(testUser), eq("verification"), eq("Account Verification"), any(Object.class), any(Email.Priority.class));

        Assert.assertEquals("New user should not be verified.", false, testUser.getVerified());
        Assert.assertNotNull("New user should have a verification token.", testUser.getVerificationToken());
        Assert.assertFalse("Token should be populated.", testUser.getVerificationToken().getToken().isEmpty());
    }

    @Test(expected = TokenNotFoundException.class)
    public void testVerifyUserWithNonExistentTokenThrows()
        throws TokenNotFoundException, TokenExpiredException {
        appUserService.verifyUser(TEST_VERIFY_TOKEN);
    }

    @Test(expected = TokenExpiredException.class)
    public void testVerifyUserWithExpiredTokenThrows()
        throws TokenNotFoundException, TokenExpiredException {
        verifyToken.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(verificationTokenDao.findByToken(TEST_VERIFY_TOKEN)).thenReturn(verifyToken);

        appUserService.verifyUser(TEST_VERIFY_TOKEN);
    }

    @Test
    public void testVerifyUser()
        throws TokenNotFoundException, TokenExpiredException {
        when(verificationTokenDao.findByToken(TEST_VERIFY_TOKEN)).thenReturn(verifyToken);

        appUserService.verifyUser(TEST_VERIFY_TOKEN);

        verify(appUserDao).save(testUser);
        verify(verificationTokenDao).delete(verifyToken);
        Assert.assertTrue("User should be verified now.", testUser.getVerified());
        Assert.assertNull("Verification token should be removed.", testUser.getVerificationToken());
    }

    @Test
    public void testResendVerificationEmailDoesNothingIfVerified()
        throws TokenNotFoundException, TokenExpiredException, MailDataException {
        testUser.setVerified(true);

        when(verificationTokenDao.findByToken(TEST_VERIFY_TOKEN)).thenReturn(verifyToken);

        appUserService.resendVerificationEmail(testUser, TEST_VERIFY_URI);

        verify(appUserDao, times(0)).
            save(testUser);

        verify(mailService, times(0)).
            queueMail(eq(testUser), eq("verification"), eq("Account Verification"), any(Object.class), any(Email.Priority.class));
    }

    @Test
    public void testResendVerificationEmailRegeneratesExpiredTokens()
        throws TokenNotFoundException, TokenExpiredException, MailDataException {
        verifyToken.setExpiresAt(LocalDateTime.now().minusHours(1));

        appUserService.resendVerificationEmail(testUser, TEST_VERIFY_URI);

        verify(appUserDao).save(testUser);
        Assert.assertNotEquals("Verification token should have changed.",
            testUser.getVerificationToken().getToken(), TEST_VERIFY_TOKEN);
    }

    @Test
    public void testResendVerificationEmailGeneratesMissingTokens()
        throws TokenNotFoundException, TokenExpiredException, MailDataException {
        testUser.setVerificationToken(null);

        appUserService.resendVerificationEmail(testUser, TEST_VERIFY_URI);

        verify(appUserDao).save(testUser);
        Assert.assertNotNull("Verification token should no longer be null.", testUser.getVerificationToken());
    }

    @Test
    public void testResendVerificationEmailQueuesMail()
        throws TokenNotFoundException, TokenExpiredException, MailDataException {
        appUserService.resendVerificationEmail(testUser, TEST_VERIFY_URI);

        verify(mailService).queueMail(eq(testUser), eq("verification"), eq("Account Verification"), any(Object.class), any(Email.Priority.class));
    }

    @Test
    public void testSendForgotPasswordTokenGeneratesMissingToken() {
        testUser.setPasswordResetToken(null);

        appUserService.sendForgotPasswordToken(testUser, TEST_PASSWORD_URI);

        verify(appUserDao).save(testUser);
        Assert.assertNotNull("Password reset token should have been generated.", testUser.getPasswordResetToken());
    }

    @Test
    public void testSendForgotPasswordTokenRegeneratesExpiredToken() {
        passwordToken.setExpiresAt(LocalDateTime.now().minusHours(1));

        appUserService.sendForgotPasswordToken(testUser, TEST_PASSWORD_URI);

        verify(appUserDao).save(testUser);
        Assert.assertNotEquals("Password reset token should have changed.",
            testUser.getPasswordResetToken().getToken(), TEST_PASSWORD_TOKEN);
    }

    @Test
    public void testSendForgotPasswordTokenQueuesMail() throws MailDataException {
        appUserService.sendForgotPasswordToken(testUser, TEST_PASSWORD_URI);

        verify(mailService).queueMail(eq(testUser), eq("forgotPassword"), eq("Password Reset"), any(Object.class), any(Email.Priority.class));
    }

    @Test
    public void testResetPassword() {
        testUser.setPassword("123");

        appUserService.resetPassword(testUser, "456");

        verify(appUserDao).save(testUser);
        Assert.assertNotEquals("Password should have changed.", "123", testUser.getPassword());
        Assert.assertNotEquals("Password should have been encoded.", "456", testUser.getPassword());
    }

    @Test(expected = TokenNotFoundException.class)
    public void testResetPasswordWithTokenThrowsForMissingToken()
        throws TokenNotFoundException, TokenExpiredException {
        when(passwordResetTokenDao.findByToken(TEST_PASSWORD_TOKEN)).thenReturn(null);

        appUserService.resetPasswordWithToken(TEST_PASSWORD_TOKEN, "123");
    }

    @Test(expected = TokenExpiredException.class)
    public void testResetPasswordWithTokenThrowsForExpiredToken()
        throws TokenNotFoundException, TokenExpiredException {
        passwordToken.setExpiresAt(LocalDateTime.now().minusHours(1));
        when(passwordResetTokenDao.findByToken(TEST_PASSWORD_TOKEN)).thenReturn(passwordToken);

        appUserService.resetPasswordWithToken(TEST_PASSWORD_TOKEN, "123");
    }

    @Test
    public void testResetPasswordWithToken()
        throws TokenNotFoundException, TokenExpiredException {
        when(passwordResetTokenDao.findByToken(TEST_PASSWORD_TOKEN)).thenReturn(passwordToken);

        appUserService.resetPasswordWithToken(TEST_PASSWORD_TOKEN, "123");

        verify(appUserDao).save(testUser);
        verify(passwordResetTokenDao).delete(passwordToken);
        Assert.assertEquals("Password should have been set.", "123", testUser.getPassword());
        Assert.assertNull("Password token should have been deleted.", testUser.getPasswordResetToken());
    }

    @Test
    public void testUpdateUserRolesRemoveRoles() throws Exception {
        when(appUserDao.findOne(any(Long.class))).thenReturn(testUserWithAdmin);

        appUserService.updateUserRoles(1L, new Long[0]);

        verify(appUserDao).save(any(AppUser.class));
    }

    @Test
    public void testUpdateUserRolesAddRoles() throws Exception {
        when(appUserDao.findOne(any(Long.class))).thenReturn(testUser);
        when(roleDao.findOne(any(Long.class))).thenReturn(testRole);

        appUserService.updateUserRoles(1L, new Long[]{1L});

        verify(appUserDao).save(any(AppUser.class));
    }
}
