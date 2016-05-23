package za.co.entelect.persistence.history;

import org.springframework.data.repository.PagingAndSortingRepository;
import za.co.entelect.domain.entities.history.HistoryEventEntity;

public interface HistoryEventEntityDao extends PagingAndSortingRepository<HistoryEventEntity, Long> {

}
