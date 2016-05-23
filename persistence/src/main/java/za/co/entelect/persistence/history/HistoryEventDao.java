package za.co.entelect.persistence.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import za.co.entelect.domain.entities.history.HistoryEvent;

public interface HistoryEventDao extends PagingAndSortingRepository<HistoryEvent, Long> {

    @Query("SELECT e.event FROM HistoryEventEntity e WHERE e.entityType = :entityType AND e.entityId = :entityId ORDER BY e.event.timestamp DESC ")
    Page<HistoryEvent> findEventsByEntityTypeAndEntityId(@Param("entityType") String entityType,
                                                         @Param("entityId") Long entityId,
                                                         Pageable page);
}
