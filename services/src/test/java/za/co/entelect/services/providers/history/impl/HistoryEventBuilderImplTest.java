package za.co.entelect.services.providers.history.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import za.co.entelect.UnitTest;
import za.co.entelect.domain.entities.history.HistoryEvent;
import za.co.entelect.domain.entities.history.HistoryEventType;
import za.co.entelect.persistence.history.HistoryEventDao;
import za.co.entelect.services.providers.history.HistoryEntityLoader;
import za.co.entelect.services.providers.history.HistoryEventBuilder;
import za.co.entelect.services.providers.history.HistoryService;
import za.co.entelect.services.providers.history.HistoryTemplateService;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class HistoryEventBuilderImplTest extends HistoryServiceBase {

    private HistoryService historyService = mock(HistoryService.class);
    private HistoryEntityLoader historyEntityLoader = mock(HistoryEntityLoader.class);
    private HistoryEventDao historyEventDao = mock(HistoryEventDao.class);
    private HistoryTemplateService historyTemplateService = mock(HistoryTemplateService.class);

    @Before
    public void testSetUp() {
        // Reset mocks
        reset(historyService);
        reset(historyEventDao);
        reset(historyEntityLoader);
        reset(historyTemplateService);

        when(historyService.saveHistoryEvent(any(HistoryEvent.class))).then(returnsFirstArg());
    }

    @Test
    public void testActorEventBuilder() {
        HistoryEventBuilder historyEventBuilder =
            new ActorHistoryEventBuilderImpl(historyService, HistoryEventType.CREATED_USER, user);

        testBuilderResult(historyEventBuilder.save());
    }

    @Test
    public void testSubjectEventBuilder() {
        HistoryEventBuilder historyEventBuilder =
            new SubjectHistoryEventBuilderImpl(historyService, HistoryEventType.CREATED_USER, user);

        testBuilderResult(historyEventBuilder.save());
    }

    @Test
    public void testObjectEventBuilder() {
        HistoryEventBuilder historyEventBuilder =
            new ObjectHistoryEventBuilderImpl(historyService, HistoryEventType.CREATED_USER, user);

        testBuilderResult(historyEventBuilder.save());
    }

    protected void testBuilderResult(HistoryEvent event) {
        Assert.assertNotNull(event);
        Assert.assertTrue(event.getEntities().size() > 0);
    }
}
