package za.co.entelect.services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import za.co.entelect.config.EnvironmentConfiguration;
import za.co.entelect.domain.entities.user.Role;
import za.co.entelect.persistence.history.HistoryEventDao;
import za.co.entelect.persistence.history.HistoryEventEntityDao;
import za.co.entelect.persistence.history.HistoryTemplateDao;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.persistence.user.RoleDao;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.providers.history.HistoryService;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import(EnvironmentConfiguration.class)
public class TestServiceConfig {

    @Bean
    public AppUserDao appUserDaoMock() {
        return mock(AppUserDao.class);
    }

    @Bean
    public AppUserService appUserService() {
        return mock(AppUserService.class);
    }

    @Bean
    public HistoryEventDao historyEventDao() {
        return mock(HistoryEventDao.class);
    }

    @Bean
    public HistoryTemplateDao historyTemplateDao() {
        return mock(HistoryTemplateDao.class);
    }

    @Bean
    public HistoryEventEntityDao historyEventEntityDao() {
        return mock(HistoryEventEntityDao.class);
    }

    @Bean
    public RoleDao roleDao() {
        RoleDao roleDao = mock(RoleDao.class);

        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ROLE_ADMIN");

        when(roleDao.findByName(eq("ROLE_ADMIN"))).thenReturn(adminRole);

        return roleDao;
    }

    @Bean
    public HistoryService historyService() {
        return mock(HistoryService.class);
    }
}
