package za.co.entelect.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;


@Slf4j
@Getter
@Setter
@ToString(exclude = {"jdbcPassword", "recaptchaSiteKey", "recaptchaSecretKey"})
@EqualsAndHashCode
public class ConfigProperties {

    @Autowired
    Environment environment;

    @PostConstruct
    public void logEffectiveConfiguration() {
        String activeProfiles = String.join(", ", environment.getActiveProfiles());
        log.info(String.format(
            "\n/********************************************\\\n" +
                "* Effective configuration:\n" +
                "* \t%s\n" +
                "* \n" +
                "* Active Spring profiles:\n" +
                "* \t%s\n" +
                "\\********************************************/\n"
            , this.toString(), activeProfiles));
    }

    @Value("${jdbc.driverClassName}")
    private String jdbcDriverClassName;

    @Value("${jdbc.dialect}")
    private String jdbcDialect;

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${jdbc.username}")
    private String jdbcUsername;

    @Value("${jdbc.password}")
    private String jdbcPassword;

    @Value("${jdbc.showSql}")
    private Boolean jdbcShowSql;

    @Value("${jdbc.pool.minSize}")
    private Integer jdbcPoolMinSize;

    @Value("${jdbc.pool.maxSize}")
    private Integer jdbcPoolMaxSize;

    @Value("${mail.strategy}")
    private String mailStrategy;

    @Value("${smtp.subject.prefix}")
    private String smtpSubjectPrefix;

    @Value("${smtp.host}")
    private String smtpHost;

    @Value("${smtp.protocol}")
    private String smtpProtocol;

    @Value("${smtp.port}")
    private Integer smtpPort;

    @Value("${smtp.username}")
    private String smtpUsername;

    @Value("${smtp.password}")
    private String smtpPassword;

    @Value("${smtp.from}")
    private String smtpFrom;

    @Value("${smtp.timeout}")
    private Long smtpTimeout;

    @Value("${smtp.rate.per.minute}")
    private Long smtpRatePerMinute;

    @Value("${templates.cacheable}")
    private Boolean templatesCacheable;

    @Value("${templates.minified}")
    private Boolean templatesMinified;

    @Value("${security.tokens.expiryInHours}")
    private int securityTokensExpiryInHours;

    @Value("${security.session.duration.seconds}")
    private int securitySessionDurationSeconds;

    @Value("${security.session.remember.seconds}")
    private int securitySessionRememberMeSeconds;

    @Value("${security.session.remember.key}")
    private String securitySessionRememberMeKey;

    @Value("${dev.mail.portOffset}")
    private int devMailPortOffset;

    @Value("${recaptcha.url}")
    private String recaptchaUrl;

    @Value("${recaptcha.siteKey}")
    private String recaptchaSiteKey;

    @Value("${recaptcha.secretKey}")
    private String recaptchaSecretKey;

    @Value("${dev.mode.on}")
    private boolean devOn;

    @Value("${registration.enabled}")
    private boolean registrationEnabled;

    @Value("${history.page.size}")
    private Integer historyPageSize;

    @Value("${history.message.unknown}")
    private String historyMessageUnknown;

    @Value("${gtm.tracking.code}")
    private String gtmTrackingCode;

    @Value("${jobs.sendEmail.cron}")
    private String jobsSendEmailCron;

    @Value("${jobs.example.cron}")
    private String jobsExampleCron;

    @Value("${https.proxyHost}")
    private String httpsProxyHost;

    @Value("${https.proxyPort}")
    private String httpsProxyPort;

    @Value("${version}")
    private String version;

    @Value("${builtAt}")
    private String builtAt;

    @Value("${baseURL}")
    private String baseUrl;

    public String getAssetsTimestamp() {
        if (templatesMinified) {
            return this.builtAt;
        } else {
            return "0";
        }
    }
}
