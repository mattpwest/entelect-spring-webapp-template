package za.co.entelect.services.providers.history;

import za.co.entelect.domain.entities.history.HistoryEvent;

public interface HistoryEventBuilder {

    HistoryEvent save();
}
