package za.co.entelect.services.providers.history.impl;

import org.junit.BeforeClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import za.co.entelect.domain.entities.history.*;
import za.co.entelect.domain.entities.user.AppUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class HistoryServiceBase {

    // Test data
    protected static AppUser user;

    @BeforeClass
    public static void testDataSetup() {
        createTestUser();
    }

    protected Page<HistoryEvent> generateTestHistoryEvents() {
        List<HistoryEvent> historyEvents = new ArrayList<>(5);

        HistoryEvent event = generateTestHistoryEvent(AppUser.class);
        historyEvents.add(event);

        return new PageImpl<>(historyEvents);
    }

    protected <T extends HistoryEntity> HistoryEvent generateTestHistoryEvent(Class<T> entityClass) {
        if (entityClass.equals(AppUser.class)) {
            return generateUserEvent();
        }

        return null;
    }

    protected HistoryEvent generateUserEvent() {
        return createEvent(user, HistoryEventType.CREATED_USER, HistoryEntityRole.OBJECT);
    }

    protected HistoryEventEntity createActor() {
        HistoryEventEntity historyEventEntity = new HistoryEventEntity();
        historyEventEntity.setEntityId(user.getId());
        historyEventEntity.setEntityType(user.getEntityType());
        historyEventEntity.setEntityRole(HistoryEntityRole.ACTOR);
        return historyEventEntity;
    }

    protected HistoryEvent createEvent(HistoryEntity entity, HistoryEventType eventType, HistoryEntityRole entityRole) {
        HistoryEvent event = new HistoryEvent();
        event.setTimestamp(LocalDateTime.now());
        event.setType(eventType);

        HistoryEventEntity historyEventEntity = new HistoryEventEntity();
        historyEventEntity.setEntityId(entity.getId());
        historyEventEntity.setEntityType(entity.getEntityType());
        historyEventEntity.setEntityRole(entityRole);

        event.getEntities().add(historyEventEntity);

        return event;
    }

    protected static void createTestUser() {
        user = new AppUser();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("Test");
    }
}
