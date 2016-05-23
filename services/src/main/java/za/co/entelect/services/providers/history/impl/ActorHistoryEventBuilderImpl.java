package za.co.entelect.services.providers.history.impl;


import za.co.entelect.domain.entities.history.HistoryEntity;
import za.co.entelect.domain.entities.history.HistoryEventType;
import za.co.entelect.services.providers.history.HistoryService;

public class ActorHistoryEventBuilderImpl extends HistoryEventBuilderImpl {
    public ActorHistoryEventBuilderImpl(HistoryService historyService,
                                        HistoryEventType eventType,
                                        HistoryEntity actor) {
        super(historyService, eventType);
        this.actor(actor);
    }
}
