package za.co.entelect.services.providers.workers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.entelect.config.ConfigProperties;

/**
 * Example worker
 */
@Service
@Slf4j
@Profile("workers")
public class ExampleWorker {

    @Autowired
    protected ConfigProperties config;

    @Scheduled(cron = "${jobs.example.cron}")
    @Transactional
    public void doWork() {
        log.info("Example worker just did nothing.");
    }
}
