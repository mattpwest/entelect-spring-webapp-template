package za.co.entelect.services.providers.history.impl;

import za.co.entelect.domain.entities.history.HistoryEntity;
import za.co.entelect.domain.entities.history.HistoryEventType;
import za.co.entelect.services.providers.history.HistoryService;

public class ObjectHistoryEventBuilderImpl extends HistoryEventBuilderImpl {
    public ObjectHistoryEventBuilderImpl(HistoryService historyService, HistoryEventType eventType,
                                         HistoryEntity object) {
        super(historyService, eventType);
        this.object(object);
    }


}
