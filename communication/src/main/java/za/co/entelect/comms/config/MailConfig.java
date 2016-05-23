package za.co.entelect.comms.config;

import com.icegreen.greenmail.spring.GreenMailBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.spring4.SpringTemplateEngine;
import za.co.entelect.comms.services.InstantMailServiceImpl;
import za.co.entelect.comms.services.MailService;
import za.co.entelect.comms.services.MailTestService;
import za.co.entelect.comms.services.MailTestServiceImpl;
import za.co.entelect.comms.services.QueuedMailServiceImpl;
import za.co.entelect.comms.template.DbTemplateResolver;
import za.co.entelect.comms.workers.MailSendingWorker;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.config.EnvironmentConfiguration;
import za.co.entelect.config.JPAConfig;

import java.util.Properties;

@Configuration
@ComponentScan(
    basePackages = "za.co.entelect",
    excludeFilters = {
        @ComponentScan.Filter(Configuration.class)
    }
)
@EnableScheduling
@Import({
    EnvironmentConfiguration.class,
    JPAConfig.class
})
public class MailConfig {

    @Autowired
    private ConfigProperties config;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getSmtpHost());
        mailSender.setPort(config.getSmtpPort());
        mailSender.setProtocol(config.getSmtpProtocol());
        mailSender.setUsername(config.getSmtpUsername());
        mailSender.setPassword(config.getSmtpPassword());
        mailSender.setDefaultEncoding("UTF-8");
        if ("smtps".equalsIgnoreCase(config.getSmtpProtocol())) {
            Properties mailProperties = new Properties();
            mailProperties.setProperty("mail.smtps.auth", "true");
            mailProperties.setProperty("mail.smtp.ssl.enable", "true");
            mailProperties.setProperty("mail.transport.protocol", "smtps");
            mailProperties.setProperty("mail.debug", "true");
            mailProperties.setProperty("mail.mime.charset", "utf8");
            mailSender.setJavaMailProperties(mailProperties);
        }
        return mailSender;
    }

    // Thymeleaf is used to pull email templates from the DB
    @Bean
    public DbTemplateResolver emailDbTemplateResolver() {
        DbTemplateResolver resolver = new DbTemplateResolver();
        resolver.setTemplateMode("HTML5");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(config.getTemplatesCacheable());
        resolver.setOrder(1);
        return resolver;
    }

    @Bean
    public SpringTemplateEngine emailTemplateEngine(DbTemplateResolver emailDbTemplateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(emailDbTemplateResolver);
        return templateEngine;
    }

    // Local profile runs a test GreenMail server
    @Bean
    @Profile("greenMail")
    public GreenMailBean greenMailBean() {
        GreenMailBean greenMailBean = new GreenMailBean();
        greenMailBean.setHostname(config.getSmtpHost());
        greenMailBean.setPortOffset(config.getDevMailPortOffset());
        greenMailBean.setAutostart(true);
        return greenMailBean;
    }

    @Bean
    @Profile("greenMail")
    public MailTestService mailTestService() {
        return new MailTestServiceImpl();
    }

    @Bean
    public MailService mailService() {
        if (config.getMailStrategy().equalsIgnoreCase("queued")) {
            return new QueuedMailServiceImpl();
        } else {
            return new InstantMailServiceImpl();
        }
    }

    @Bean
    @Profile("workers")
    public MailSendingWorker mailSendingWorker() {
        if (config.getMailStrategy().equalsIgnoreCase("queued")) {
            return new MailSendingWorker();
        }

        return null;
    }
}
