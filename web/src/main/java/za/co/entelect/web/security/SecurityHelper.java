package za.co.entelect.web.security;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

public class SecurityHelper {
    public static String getTokenUri(HttpServletRequest request, String path) {
        return ServletUriComponentsBuilder.fromContextPath(request).path(path).toUriString();
    }
}
