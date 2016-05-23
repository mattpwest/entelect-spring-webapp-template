package za.co.entelect.services.providers.history;

import za.co.entelect.domain.entities.history.HistoryEntity;

public interface SubjectHistoryEventBuilder extends HistoryEventBuilder {

    SubjectHistoryEventBuilder object(HistoryEntity object);
    SubjectHistoryEventBuilder actor(HistoryEntity actor);
    SubjectHistoryEventBuilder staticText(String staticText);
}
