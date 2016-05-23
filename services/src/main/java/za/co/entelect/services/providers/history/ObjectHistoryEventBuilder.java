package za.co.entelect.services.providers.history;

import za.co.entelect.domain.entities.history.HistoryEntity;

public interface ObjectHistoryEventBuilder extends HistoryEventBuilder {

    ObjectHistoryEventBuilder subject(HistoryEntity subject);
    ObjectHistoryEventBuilder actor(HistoryEntity actor);
    ObjectHistoryEventBuilder staticText(String staticText);
}
