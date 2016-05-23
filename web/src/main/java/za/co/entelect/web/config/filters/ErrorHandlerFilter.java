package za.co.entelect.web.config.filters;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.support.WebApplicationContextUtils;
import za.co.entelect.config.ConfigProperties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Hack to handle view rendering exceptions without showing a stack trace when not running in development mode.
 */
@Slf4j
public class ErrorHandlerFilter implements Filter {

    @Setter
    private ConfigProperties config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        config = (ConfigProperties) WebApplicationContextUtils
            .getRequiredWebApplicationContext(filterConfig.getServletContext())
            .getBean("configProperties");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
                throws IOException, ServletException {

        if (config.isDevOn()) {
            filterChain.doFilter(request, response);
        } else {
            try {
                filterChain.doFilter(request, response);
            } catch (Exception ex) {
                log.error("A view rendering exception has occurred.", ex);

                request.getRequestDispatcher("/viewError").forward(request, response);
            }
        }
    }

    @Override
    public void destroy() {

    }

}
