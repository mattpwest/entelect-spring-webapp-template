package za.co.entelect.services.providers;

public interface RecaptchaService {

    boolean isResponseValid(String response, String remoteIp);
}
