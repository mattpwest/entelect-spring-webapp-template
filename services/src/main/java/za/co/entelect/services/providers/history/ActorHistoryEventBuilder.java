package za.co.entelect.services.providers.history;

import za.co.entelect.domain.entities.history.HistoryEntity;

public interface ActorHistoryEventBuilder extends HistoryEventBuilder {

    ActorHistoryEventBuilder object(HistoryEntity object);
    ActorHistoryEventBuilder subject(HistoryEntity subject);
    ActorHistoryEventBuilder staticText(String staticText);
}
