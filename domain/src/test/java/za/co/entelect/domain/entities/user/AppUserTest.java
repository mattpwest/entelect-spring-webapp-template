package za.co.entelect.domain.entities.user;

import org.junit.Before;
import org.junit.experimental.categories.Category;
import za.co.entelect.UnitTest;
import za.co.entelect.domain.AbstractEntityTest;

import java.time.LocalDateTime;

@Category(UnitTest.class)
public class AppUserTest extends AbstractEntityTest<AppUser> {

    private AppUser testUser;

    @Override
    protected AppUser getBeanInstance() {
        return new AppUser();
    }

    @Override
    protected Object[] getPrefabInstances() {
        VerificationToken verify1 = new VerificationToken();
        verify1.setToken("123");
        verify1.setExpiresAt(LocalDateTime.now());

        VerificationToken verify2 = new VerificationToken();
        verify2.setToken("456");
        verify2.setExpiresAt(LocalDateTime.now());

        PasswordResetToken pass1 = new PasswordResetToken();
        pass1.setToken("123");
        pass1.setExpiresAt(LocalDateTime.now());

        PasswordResetToken pass2 = new PasswordResetToken();
        pass2.setToken("456");
        pass2.setExpiresAt(LocalDateTime.now());

        AppUser user1 = new AppUser();
        user1.setEmail("user1");
        user1.setFirstName("user1");
        user1.setLastName("user1");
        user1.setVerificationToken(verify1);
        user1.setPasswordResetToken(pass1);

        AppUser user2 = new AppUser();
        user1.setEmail("user2");
        user1.setFirstName("user2");
        user1.setLastName("user2");
        user2.setVerificationToken(verify2);
        user2.setPasswordResetToken(pass2);

        return new Object[] {
            verify1, verify2,
            pass1, pass2,
            user1, user2
        };
    }

    @Override
    protected String[] getIgnoredProperties() {
        return new String[] {
            "logo",
            "banner",
            "verificationToken",
            "passwordResetToken",
            "location",
            "internetProvider",
            "internetType",
            "competitor"
        };
    }

    @Before
    public void setUp() {
        testUser = new AppUser();
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("Test");
    }
}
