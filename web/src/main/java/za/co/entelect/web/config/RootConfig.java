package za.co.entelect.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import za.co.entelect.services.config.ServicesConfig;

@Configuration
@Import({
    ServicesConfig.class
})
public class RootConfig {
}
