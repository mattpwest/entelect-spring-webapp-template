package za.co.entelect.web.security;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import za.co.entelect.UnitTest;
import za.co.entelect.services.security.exceptions.AccountDisabledException;
import za.co.entelect.services.security.exceptions.EmailNotVerifiedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class LoginFailureHandlerTest {
    LoginFailureHandler classUnderTest = new LoginFailureHandler();

    @Test
    public void testWithEmailNotVerifiedExceptionRedirects() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        classUnderTest.onAuthenticationFailure(request, response, new EmailNotVerifiedException());

        verify(response, times(1)).sendRedirect("/verification");
    }

    @Test(expected = NullPointerException.class)
    public void testWithOtherExceptionUsesDefaultProcessing() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        classUnderTest.onAuthenticationFailure(request, response, new AccountDisabledException());

        // Unfortunately I could find no way to avoid the super call crashing, so this will have to do as verification
        // that we took the other path through the code...
    }
}
