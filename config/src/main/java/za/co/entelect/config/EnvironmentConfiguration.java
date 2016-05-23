package za.co.entelect.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
@PropertySources({
    @PropertySource("classpath:/za/co/entelect/version/version.properties"),
    @PropertySource("classpath:/za/co/entelect/config/entelect-default.properties"),
    @PropertySource(value = "classpath:/za/co/entelect/config/entelect-${ENTELECT_ENV}.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:/za/co/entelect/config/entelect-${ENTELECT_ENV}-${ENTELECT_USER}.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "file:${CATALINA_HOME}/conf/entelect.properties", ignoreResourceNotFound = true)
})
public class EnvironmentConfiguration {

    private static final String MESSAGE_LOCATION = "classpath:/za/co/entelect/messages/";

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(true);
        return configurer;
    }

    @Bean
    public static MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
            MESSAGE_LOCATION + "messages",
            MESSAGE_LOCATION + "security"
        );
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    public ConfigProperties configProperties() {
        return new ConfigProperties();
    }
}
