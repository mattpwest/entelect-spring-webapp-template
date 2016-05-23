package za.co.entelect.services.providers.history.impl;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.domain.entities.history.HistoryEntity;
import za.co.entelect.domain.entities.history.HistoryEvent;
import za.co.entelect.domain.entities.history.HistoryEventEntity;
import za.co.entelect.domain.entities.history.HistoryEventType;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.persistence.history.HistoryEventDao;
import za.co.entelect.services.security.dto.HistoryEventDto;
import za.co.entelect.services.providers.history.HistoryEntityLoader;
import za.co.entelect.services.providers.history.HistoryService;
import za.co.entelect.services.providers.history.HistoryTemplateService;
import za.co.entelect.services.providers.history.ObjectHistoryEventBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    @Setter
    private ConfigProperties config;

    @Autowired
    @Setter
    private HistoryEventDao historyEventDao;

    @Autowired
    @Setter
    private HistoryEntityLoader historyEntityLoader;

    @Autowired
    @Setter
    private HistoryTemplateService historyTemplateService;

    @Override
    public Page<HistoryEventDto> findHistoryEvents(HistoryEntity entity) {
        return findHistoryEvents(entity, 0);
    }

    @Override
    public Page<HistoryEventDto> findHistoryEvents(HistoryEntity entity, int pageNo) {
        PageRequest pageRequest = new PageRequest(pageNo, config.getHistoryPageSize());
        Page<HistoryEvent> page = historyEventDao.findEventsByEntityTypeAndEntityId(entity.getEntityType(), entity.getId(), pageRequest);

        List<HistoryEventDto> resultItems = new ArrayList<>(page.getNumberOfElements());
        for (HistoryEvent event : page) {
            HistoryEventDto eventDto = new HistoryEventDto();
            eventDto.setTimestamp(event.getTimestamp());

            String msg = renderMessage(event, entity);
            if (msg != null) {
                eventDto.setMessage(msg);
            } else {
                eventDto.setMessage(config.getHistoryMessageUnknown());
            }

            resultItems.add(eventDto);
        }

        Page<HistoryEventDto> result = new PageImpl<>(resultItems, pageRequest, page.getTotalElements());
        return result;
    }

    // Suspends the current transaction to save the history event, so error in history should not affect business logic transactions
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public HistoryEvent saveHistoryEvent(HistoryEvent event) {
        return historyEventDao.save(event);
    }

    @Override
    public ObjectHistoryEventBuilder registered(AppUser user) {
        return new ObjectHistoryEventBuilderImpl(this, HistoryEventType.REGISTERED_USER, user);
    }

    @Override
    public ObjectHistoryEventBuilder created(AppUser user) {
        return new ObjectHistoryEventBuilderImpl(this, HistoryEventType.CREATED_USER, user);
    }

    protected String renderMessage(HistoryEvent event, HistoryEntity currentEntity) {
        List<HistoryEventEntity> otherEntities = new ArrayList<>();
        HistoryEventEntity currentEntityWithRole = null;

        for (HistoryEventEntity historyEntity : event.getEntities()) {
            if (!(historyEntity.getEntityType().equals(currentEntity.getEntityType()) &&
                    historyEntity.getEntityId().equals(currentEntity.getId()))) {
                otherEntities.add(historyEntity);
            } else {
                currentEntityWithRole = historyEntity;
            }
        }

        Map<String, String> linkVariables = renderEntityLinks(otherEntities);

        return historyTemplateService.renderEventMessage(event, currentEntityWithRole, linkVariables);
    }

    protected Map<String, String> renderEntityLinks(List<HistoryEventEntity> otherEntities) {
        Map<String, String> entityLinks = new HashMap<>();

        for (HistoryEventEntity historyEntity : otherEntities) {
            HistoryEntity entity =
                historyEntityLoader.loadEntity(historyEntity.getEntityType(), historyEntity.getEntityId());

            if (entity == null) {
                entityLinks.put(historyEntity.getEntityRole().toString().toLowerCase(), "<i>unknown</i>");
            } else {
                String link = String.format("<a href=\"%s\">%s</a>", renderUri(entity), entity.getEntityName());
                entityLinks.put(historyEntity.getEntityRole().toString().toLowerCase(), link);
            }
        }

        return entityLinks;
    }

    protected String renderUri(HistoryEntity entity) {
        if (entity instanceof AppUser) {
            return String.format("/users/%d", entity.getId());
        } else   {
            return "";
        }
    }
}
