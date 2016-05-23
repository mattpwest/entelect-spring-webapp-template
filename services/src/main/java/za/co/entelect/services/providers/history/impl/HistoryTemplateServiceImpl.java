package za.co.entelect.services.providers.history.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.entelect.domain.entities.history.HistoryEvent;
import za.co.entelect.domain.entities.history.HistoryEventEntity;
import za.co.entelect.domain.entities.history.HistoryTemplate;
import za.co.entelect.persistence.history.HistoryTemplateDao;
import za.co.entelect.services.providers.history.HistoryTemplateService;

import java.util.Map;

@Service
@Slf4j
public class HistoryTemplateServiceImpl implements HistoryTemplateService {

    @Autowired
    @Setter
    private HistoryTemplateDao historyTemplateDao;

    @Override
    public String renderEventMessage(HistoryEvent event,
                                     HistoryEventEntity currentEntity,
                                     Map<String, String> links) {

        if (event == null) {
            log.warn("History event not rendered: no event specified.");
            return null;
        }

        if (currentEntity == null) {
            log.warn("History event not rendered: no event specified.");
            return null;
        }

        HistoryTemplate template = historyTemplateDao.findOneByTypeAndRole(event.getType(), currentEntity.getEntityRole());

        if (template == null) {
            log.warn("History event not rendered: no template found for event type " + event.getType() +
                " and entity type " + currentEntity.getEntityType() + ".");
            return null;
        }

        return template.getTemplate()
            .replace("{subject}", links.containsKey("subject") ? links.get("subject") : "unknown")
            .replace("{object}", links.containsKey("object") ? links.get("object") : "unknown")
            .replace("{actor}", links.containsKey("actor") ? links.get("actor") : "unknown")
            .replace("{staticText}", event.getStaticText() != null ? event.getStaticText() : "");
    }
}
