package za.co.entelect.services.providers.history.impl;

import za.co.entelect.domain.entities.history.HistoryEntity;
import za.co.entelect.domain.entities.history.HistoryEntityRole;
import za.co.entelect.domain.entities.history.HistoryEvent;
import za.co.entelect.domain.entities.history.HistoryEventEntity;
import za.co.entelect.domain.entities.history.HistoryEventType;
import za.co.entelect.services.providers.history.ActorHistoryEventBuilder;
import za.co.entelect.services.providers.history.HistoryService;
import za.co.entelect.services.providers.history.ObjectHistoryEventBuilder;
import za.co.entelect.services.providers.history.SubjectHistoryEventBuilder;

import java.time.LocalDateTime;

abstract class HistoryEventBuilderImpl implements ActorHistoryEventBuilder,
    SubjectHistoryEventBuilder,
    ObjectHistoryEventBuilder {

    private HistoryService historyService;
    private HistoryEventType eventType;
    private HistoryEntity actor;
    private HistoryEntity object;
    private HistoryEntity subject;
    private String staticText;

    public HistoryEventBuilderImpl(HistoryService historyService, HistoryEventType eventType) {
        this.historyService = historyService;
        this.eventType = eventType;
    }

    @Override
    public HistoryEventBuilderImpl object(HistoryEntity object) {
        this.object = object;
        return this;
    }

    @Override
    public HistoryEventBuilderImpl actor(HistoryEntity actor) {
        this.actor = actor;
        return this;
    }

    @Override
    public HistoryEventBuilderImpl subject(HistoryEntity subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public HistoryEventBuilderImpl staticText(String staticText) {
        this.staticText = staticText;
        return this;
    }

    @Override
    public HistoryEvent save() {
        HistoryEvent event = new HistoryEvent();
        event.setType(eventType);
        event.setTimestamp(LocalDateTime.now());

        if (actor != null) {
            HistoryEventEntity actorEntity = new HistoryEventEntity();
            actorEntity.setEntityId(actor.getId());
            actorEntity.setEntityType(actor.getEntityType());
            actorEntity.setEntityRole(HistoryEntityRole.ACTOR);
            actorEntity.setEvent(event);
            event.getEntities().add(actorEntity);
        }

        if (subject != null) {
            HistoryEventEntity subjectEntity = new HistoryEventEntity();
            subjectEntity.setEntityId(subject.getId());
            subjectEntity.setEntityType(subject.getEntityType());
            subjectEntity.setEntityRole(HistoryEntityRole.SUBJECT);
            subjectEntity.setEvent(event);
            event.getEntities().add(subjectEntity);
        }

        if (object != null) {
            HistoryEventEntity objectEntity = new HistoryEventEntity();
            objectEntity.setEntityId(object.getId());
            objectEntity.setEntityType(object.getEntityType());
            objectEntity.setEntityRole(HistoryEntityRole.OBJECT);
            objectEntity.setEvent(event);
            event.getEntities().add(objectEntity);
        }

        if (staticText != null) {
            event.setStaticText(staticText);
        }

        HistoryEvent result = historyService.saveHistoryEvent(event);
        historyService = null;
        return result;
    }
}
