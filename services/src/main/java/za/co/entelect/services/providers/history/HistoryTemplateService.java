package za.co.entelect.services.providers.history;

import za.co.entelect.domain.entities.history.HistoryEvent;
import za.co.entelect.domain.entities.history.HistoryEventEntity;

import java.util.Map;

public interface HistoryTemplateService {

    String renderEventMessage(HistoryEvent event,
                             HistoryEventEntity currentEntity,
                             Map<String, String> linksToOtherEntities);
}
