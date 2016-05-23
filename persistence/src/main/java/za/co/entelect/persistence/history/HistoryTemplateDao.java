package za.co.entelect.persistence.history;

import org.springframework.data.repository.PagingAndSortingRepository;
import za.co.entelect.domain.entities.history.HistoryEntityRole;
import za.co.entelect.domain.entities.history.HistoryEventType;
import za.co.entelect.domain.entities.history.HistoryTemplate;

public interface HistoryTemplateDao extends PagingAndSortingRepository<HistoryTemplate, Long> {

    HistoryTemplate findOneByTypeAndRole(HistoryEventType type, HistoryEntityRole role);
}
