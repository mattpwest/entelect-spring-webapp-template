package za.co.entelect.web.controllers.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import za.co.entelect.domain.entities.history.HistoryEntity;
import za.co.entelect.services.providers.history.HistoryService;
import za.co.entelect.services.security.dto.HistoryEventDto;
import za.co.entelect.web.exceptions.EntityNotFoundException;

@Slf4j
public abstract class AbstractBaseHistoryController extends AbstractBaseController {

    @Autowired
    private HistoryService historyService;

    public ModelAndView getHistoryPage(@PathVariable("id") Long id,
                                          @RequestParam("page") Integer page,
                                          ModelMap model) {

        HistoryEntity entity = getEntity(id);
        if (entity == null) {
            throw new EntityNotFoundException(String.format("History entity with ID %d not found.", id));
        }

        setupHistoryViewModel(model, entity, page);

        return new ModelAndView("/fragments/history :: block", model);
    }

    protected abstract HistoryEntity getEntity(Long id);

    protected abstract String getControllerRootUri();

    protected void setupHistoryViewModel(ModelMap model, HistoryEntity entity, int historyPage) {
        Page<HistoryEventDto> historyEvents = historyService.findHistoryEvents(entity, historyPage);
        model.put("history", historyEvents);

        model.put("historyUri", getControllerRootUri() + entity.getId() + "/history");
    }
}
