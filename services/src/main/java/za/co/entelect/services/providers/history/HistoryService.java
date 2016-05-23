package za.co.entelect.services.providers.history;

import org.springframework.data.domain.Page;
import za.co.entelect.domain.entities.history.HistoryEntity;
import za.co.entelect.domain.entities.history.HistoryEvent;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.services.security.dto.HistoryEventDto;

public interface HistoryService {

    Page<HistoryEventDto> findHistoryEvents(HistoryEntity entity);
    Page<HistoryEventDto> findHistoryEvents(HistoryEntity entity, int pageNo);

    HistoryEvent saveHistoryEvent(HistoryEvent event);

    // Events
    ObjectHistoryEventBuilder registered(AppUser user);

    ObjectHistoryEventBuilder created(AppUser user);
}
