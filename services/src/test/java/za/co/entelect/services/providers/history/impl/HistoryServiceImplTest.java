package za.co.entelect.services.providers.history.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import za.co.entelect.UnitTest;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.domain.entities.history.HistoryEntity;
import za.co.entelect.domain.entities.history.HistoryEvent;
import za.co.entelect.persistence.history.HistoryEventDao;
import za.co.entelect.services.providers.history.HistoryEntityLoader;
import za.co.entelect.services.providers.history.HistoryTemplateService;
import za.co.entelect.services.security.dto.HistoryEventDto;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class HistoryServiceImplTest extends HistoryServiceBase {

    private HistoryServiceImpl historyService;
    private HistoryEntityLoader historyEntityLoader = mock(HistoryEntityLoader.class);
    private HistoryEventDao historyEventDao = mock(HistoryEventDao.class);
    private HistoryTemplateService historyTemplateService = mock(HistoryTemplateService.class);
    private ConfigProperties config = new ConfigProperties();

    @Before
    public void testSetUp() {
        // Reset mocks
        reset(historyEventDao);
        reset(historyEntityLoader);
        reset(historyTemplateService);

        when(historyEntityLoader.loadEntity(eq(user.getEntityType()), eq(1L))).thenReturn(user);

        // Reset the service under test
        historyService = new HistoryServiceImpl();

        historyService.setHistoryEntityLoader(historyEntityLoader);
        historyService.setHistoryEventDao(historyEventDao);
        historyService.setHistoryTemplateService(historyTemplateService);

        config.setHistoryPageSize(3);
        config.setHistoryMessageUnknown("Unknown event.");
        historyService.setConfig(config);
    }

    @Test
    public void testFindHistoryEvents() {
        when(historyEventDao.findEventsByEntityTypeAndEntityId(any(String.class), any(Long.class), any(PageRequest.class)))
            .thenReturn(generateTestHistoryEvents());
        when(historyTemplateService.renderEventMessage(any(), any(), any())).thenReturn("test");

        Page<HistoryEventDto> events = historyService.findHistoryEvents(user);

        Assert.assertNotNull(events);
        Assert.assertEquals(1, events.getTotalElements());
        Assert.assertEquals(3, events.getSize());
    }

    @Test
    public void testFindHistoryEventsIncludesItemsThatFailToRender() {
        when(historyEventDao.findEventsByEntityTypeAndEntityId(any(String.class), any(Long.class), any(PageRequest.class)))
            .thenReturn(generateTestHistoryEvents());
        when(historyTemplateService.renderEventMessage(any(), any(), any())).thenReturn(null);

        Page<HistoryEventDto> events = historyService.findHistoryEvents(user);

        Assert.assertNotNull(events);
        Assert.assertEquals(1, events.getTotalElements());
        Assert.assertEquals(3, events.getSize());
        Assert.assertEquals(config.getHistoryMessageUnknown(), events.getContent().get(0).getMessage());
    }

    @Test
    public void testSaveHistoryEvent() {
        HistoryEvent testEvent = generateUserEvent();
        when(historyEventDao.save(eq(testEvent))).thenReturn(testEvent);

        HistoryEvent result = historyService.saveHistoryEvent(testEvent);

        Assert.assertNotNull(result);
        verify(historyEventDao, times(1)).save(eq(testEvent));
    }

    @Test
    public void testRenderUri() {
        String userUri = historyService.renderUri(user);

        Assert.assertEquals("/users/1", userUri);
    }

    @Test
    public void testRenderUriEdgeCases() {
        HistoryEntity testEntity = new HistoryEntity() {
            @Override
            public String getEntityType() {
                return "TestEntity";
            }

            @Override
            public String getEntityName() {
                return "TestEntity";
            }

            @Override
            public Long getId() {
                return 1L;
            }
        };

        String nullUri = historyService.renderUri(null);
        String unsupportedUri = historyService.renderUri(testEntity);

        Assert.assertEquals("", nullUri);
        Assert.assertEquals("", unsupportedUri);
    }

    @Test
    public void testBuilderCreation() {
        Assert.assertNotNull(historyService.created(user));
        Assert.assertNotNull(historyService.registered(user));
    }
}
