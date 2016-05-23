package za.co.entelect.services.providers.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.services.providers.RecaptchaService;
import za.co.entelect.services.providers.exceptions.RecaptchaServiceException;

import java.util.Collection;

@Service
@Slf4j
public class RecaptchaServiceImpl implements RecaptchaService {

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean isResponseValid(String response, String remoteIp) {
        RecaptchaResponse recaptchaResponse;

        try {
            recaptchaResponse = restTemplate.postForEntity(
                    configProperties.getRecaptchaUrl(),
                    createBody(configProperties.getRecaptchaSecretKey(), remoteIp, response),
                    RecaptchaResponse.class)
                .getBody();
        } catch (RestClientException e) {
            throw new RecaptchaServiceException("Recaptcha API not available due to exception", e);
        }

        return recaptchaResponse.success;
    }

    private MultiValueMap<String, String> createBody(String secret, String remoteIp, String response) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secret);
        form.add("remoteip", remoteIp);
        form.add("response", response);
        return form;
    }

    private static class RecaptchaResponse {

        @JsonProperty("success")
        private boolean success;

        @JsonProperty("error-codes")
        private Collection<String> errorCodes;
    }
}
