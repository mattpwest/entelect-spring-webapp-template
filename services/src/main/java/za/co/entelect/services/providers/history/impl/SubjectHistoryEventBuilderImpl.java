package za.co.entelect.services.providers.history.impl;

import za.co.entelect.domain.entities.history.HistoryEntity;
import za.co.entelect.domain.entities.history.HistoryEventType;
import za.co.entelect.services.providers.history.HistoryService;


public class SubjectHistoryEventBuilderImpl extends HistoryEventBuilderImpl {
    public SubjectHistoryEventBuilderImpl(HistoryService historyService, HistoryEventType eventType,
                                          HistoryEntity subject) {
        super(historyService, eventType);
        this.subject(subject);
    }
}
