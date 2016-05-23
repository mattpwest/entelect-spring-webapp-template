package za.co.entelect.web.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String redirectTo = "/accessDenied";

        if (authentication == null || !authentication.isAuthenticated()) {
            // Denied due to not being authenticated at all
            redirectTo = "/login";
        } else if ((requestUri.startsWith("/profile") || requestUri.startsWith("/account"))
                    && (!isFullyAuthenticated(authentication))) {

            // Denied due to remember-me
            redirectTo = "/relogin";
        }
        // else denied due to insufficient authorization

        // Set the pre-login URL and redirect
        HttpSession session = request.getSession();
        if (session != null) {
            session.setAttribute("url_prior_login", requestUri);
        }

        response.sendRedirect(redirectTo);
    }

    protected boolean isFullyAuthenticated(Authentication authentication) {
        return !(authentication instanceof AnonymousAuthenticationToken ||
                    authentication instanceof RememberMeAuthenticationToken);
    }
}
