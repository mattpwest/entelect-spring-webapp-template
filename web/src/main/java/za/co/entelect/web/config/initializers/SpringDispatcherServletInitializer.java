package za.co.entelect.web.config.initializers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import za.co.entelect.web.config.RootConfig;
import za.co.entelect.web.config.SpringMVCConfig;
import za.co.entelect.web.config.SpringSecurityConfig;
import za.co.entelect.web.config.filters.ErrorHandlerFilter;
import za.co.entelect.web.config.filters.RecaptchaResponseFilter;

import javax.servlet.Filter;

/**
 * This Initializer class replaces the need for a web.xml.
 */
@Order(1)
@Slf4j
public class SpringDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        WebApplicationContext ctx = super.createRootApplicationContext();
        ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) ctx.getEnvironment();
        MutablePropertySources sources = configurableEnvironment.getPropertySources();

        log.info(String.format(
            "\n/********************************************\\\n" +
                "* Starting Entelect Web\n" +
                "*\n" +
                "* Environment: %s\n" +
                "\\********************************************/\n"
            , configurableEnvironment.getProperty("ENTELECT_ENV")));
        return ctx;
    }

    @Override
    protected DispatcherServlet createDispatcherServlet(WebApplicationContext servletAppContext) {
        final DispatcherServlet dispatcherServlet = super.createDispatcherServlet(servletAppContext);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        return dispatcherServlet;
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
            SpringSecurityConfig.class,
            RootConfig.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{
            SpringSecurityConfig.class,
            SpringMVCConfig.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{
            new OpenEntityManagerInViewFilter(),
            new RecaptchaResponseFilter(),
            new ErrorHandlerFilter()
        };
    }
}
