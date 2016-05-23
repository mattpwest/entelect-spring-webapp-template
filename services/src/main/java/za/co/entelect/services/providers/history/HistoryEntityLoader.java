package za.co.entelect.services.providers.history;

import za.co.entelect.domain.entities.history.HistoryEntity;

public interface HistoryEntityLoader {

    HistoryEntity loadEntity(String entityType, Long entityId);
}
