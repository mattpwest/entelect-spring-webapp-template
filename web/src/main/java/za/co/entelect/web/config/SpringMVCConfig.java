package za.co.entelect.web.config;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.GzipResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.web.converters.LocalDateFormatter;
import za.co.entelect.web.converters.LocalDateTimeFormatter;

import java.util.concurrent.TimeUnit;

@EnableWebMvc
@Configuration
@ComponentScan(
    basePackages = "za.co.entelect.web",
    excludeFilters = {
        @ComponentScan.Filter(pattern = "za[.]co[.]entelect[.]web[.]config[.].*", type = FilterType.REGEX)
    }
)
@EnableSpringDataWebSupport
@Import({
    ThymeLeafConfig.class,
    SpringRESTConfig.class
})
public class SpringMVCConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private ConfigProperties config;

    @Autowired
    private LocalDateFormatter localDateFormatter;

    @Autowired
    private LocalDateTimeFormatter localDateTimeFormatter;

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setMessageInterpolator(new ResourceBundleMessageInterpolator());
        return bean;
    }

    @Override
    public Validator getValidator(){
        return validator();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (config.getTemplatesMinified()) {
            registry
                .addResourceHandler("/assets/" + config.getAssetsTimestamp() + "/**")
                .addResourceLocations("/assets/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                .resourceChain(true)
                .addResolver(new GzipResourceResolver())
                .addResolver(new PathResourceResolver());
        } else {
            registry
                .addResourceHandler("/assets/" + config.getAssetsTimestamp() + "/**")
                .addResourceLocations("/assets/")
                .setCacheControl(CacheControl.noCache())
                .resourceChain(true)
                .addResolver(new GzipResourceResolver())
                .addResolver(new PathResourceResolver());
        }

        // Always serve uploaded resources as a backup in-case we're running without an Apache in front...
        /*
        registry
            .addResourceHandler("/uploaded-resources/**")
            .addResourceLocations("file:///" + config.getResourceUploadPath())
            .resourceChain(true)
            .addResolver(new GzipResourceResolver())
            .addResolver(new PathResourceResolver());
        */
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(localDateFormatter);
        registry.addFormatter(localDateTimeFormatter);
    }
}

