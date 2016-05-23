package za.co.entelect.services.providers.history.impl;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.history.HistoryEntity;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.services.providers.history.HistoryEntityLoader;

@Component
public class HistoryEntityLoaderImpl implements HistoryEntityLoader {

    @Autowired
    @Setter
    private AppUserDao appUserDao;

    public HistoryEntity loadEntity(String entityType, Long entityId) {
        if (entityType.equals(AppUser.class.getSimpleName())) {
            return appUserDao.findOne(entityId);
        }

        return null;
    }
}
