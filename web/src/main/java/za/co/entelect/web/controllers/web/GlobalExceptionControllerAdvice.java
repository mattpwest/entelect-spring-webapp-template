package za.co.entelect.web.controllers.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.support.RequestContextUtils;
import za.co.entelect.config.ConfigProperties;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionControllerAdvice {

    @Autowired
    RootController rootController;

    @Autowired
    ConfigProperties config;

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex) {
        return "forward:/accessDenied";
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ModelAndView handleAllExceptions(Exception ex,
                                            HttpServletRequest request) {
        log.error(String.format("An unhandled error occurred on page %s.", request.getRequestURL()) ,ex);

        if (config.isDevOn()) {
            FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
            flashMap.put("exceptionName", ex.getClass().getSimpleName());
            flashMap.put("exception", ex.getMessage());

            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            flashMap.put("stackTrace", errors.toString());
        }
        return new ModelAndView("redirect:/error");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handle404(NoHandlerFoundException ex,
                                  HttpServletRequest request) {
        log.error(String.format("URL %s. was not found and caused an exception", request.getRequestURL()));

        return new ModelAndView("forward:/404");
    }
}
