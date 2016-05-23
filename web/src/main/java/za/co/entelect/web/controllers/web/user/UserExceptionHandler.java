package za.co.entelect.web.controllers.web.user;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import za.co.entelect.web.exceptions.EntityNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice(basePackages = {"za.co.entelect.web.controllers.web.user"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public RedirectView handleEntityNotFound(EntityNotFoundException ex,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {

        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("error", ex.getMessage());
        }

        return new RedirectView("/users");
    }
}
