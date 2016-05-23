package za.co.entelect.web.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import za.co.entelect.services.security.exceptions.EmailNotVerifiedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public LoginFailureHandler() {
        this.setDefaultFailureUrl("/login");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        if (exception.getClass().isAssignableFrom(EmailNotVerifiedException.class) ||
                    ((exception.getCause() != null) &&
                        exception.getCause().getClass().isAssignableFrom(EmailNotVerifiedException.class))) {
            response.sendRedirect("/verification");
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
