package za.co.entelect.services.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import za.co.entelect.comms.config.MailConfig;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.config.JPAConfig;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * A Config class to scan all the Components for the root context. Excludes the Controllers which will be scanned for
 * the servlet context.
 */
@Configuration
@ComponentScan(
    basePackages = "za.co.entelect",
    excludeFilters= {
        @ComponentScan.Filter(org.springframework.stereotype.Controller.class),
        @ComponentScan.Filter(Configuration.class)
    }
)
@Import({
    JPAConfig.class,
    MailConfig.class
})
@EnableAsync
@Slf4j
public class ServicesConfig {

    @Autowired
    private ConfigProperties config;

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        if (config.getHttpsProxyHost() != null && !config.getHttpsProxyHost().trim().equals("${https.proxyHost}") &&
            config.getHttpsProxyPort() != null && !config.getHttpsProxyPort().trim().equals("${https.proxyPort}")) {

            log.info("Configuring RestTemplate with proxy {}:{}", config.getHttpsProxyHost(), config.getHttpsProxyPort());

            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            InetSocketAddress address = new InetSocketAddress(
                                                config.getHttpsProxyHost(),
                                                Integer.parseInt(config.getHttpsProxyPort()));
            Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
            requestFactory.setProxy(proxy);

            restTemplate.setRequestFactory(requestFactory);
        }

        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory(HttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClientBuilder.create().build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
