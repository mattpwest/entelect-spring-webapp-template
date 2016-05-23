package za.co.entelect.services.providers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import za.co.entelect.domain.Exceptions.UserNotBannedException;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.user.ChangeEmailVerificationToken;
import za.co.entelect.dto.AppUserLiteDTO;
import za.co.entelect.services.providers.dto.forms.user.UserCreationForm;
import za.co.entelect.services.providers.dto.forms.user.UserRegistrationForm;
import za.co.entelect.services.providers.exceptions.EmailInUseException;
import za.co.entelect.services.providers.exceptions.TokenExpiredException;
import za.co.entelect.services.providers.exceptions.TokenNotFoundException;

import java.util.List;

public interface AppUserService extends CRUDService<AppUser, Long> {

    AppUser createUser(UserCreationForm form, AppUser createdBy, String verifyUri, String passwordUri)
                throws EmailInUseException;

    /**
     * Purely for use in generating random users more quickly. Doesn't send out emails.
     */
    AppUser createUserNoEmails(UserCreationForm form, AppUser createdBy, String verifyUri, String passwordUri, String
        passwordHash)
        throws EmailInUseException;

    AppUser registerUser(UserRegistrationForm form, String verifyUri) throws EmailInUseException;

    void verifyUser(String verificationToken)
        throws TokenNotFoundException, TokenExpiredException;

    void resendVerificationEmail(AppUser user, String verifyUri);

    void resetPassword(AppUser user, String rawPassword);

    void resetPasswordWithToken(String token, String newPasswordEncoded)
        throws TokenNotFoundException, TokenExpiredException;

    void sendForgotPasswordToken(AppUser user, String forgotPasswordUri);

    void updateUserRoles(Long userId, Long[] roles);

    boolean isAdmin(AppUser appUser);


    Page<AppUserLiteDTO> findAllLiteWithRoles(Pageable pageable);
    Page<AppUserLiteDTO> findAllLiteWithRolesByName(String name, Pageable pageable);

    ChangeEmailVerificationToken createChangeEmailToken(String newEmail, AppUser currentUser);

    ChangeEmailVerificationToken changeEmail(String token) throws TokenNotFoundException;

    void cancelEmailChange(String token);

    AppUser findByEmail(String email);
}
