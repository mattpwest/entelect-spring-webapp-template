package za.co.entelect.services.providers.history.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import za.co.entelect.UnitTest;
import za.co.entelect.domain.entities.history.HistoryTemplate;
import za.co.entelect.persistence.history.HistoryTemplateDao;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class HistoryTemplateServiceImplTest extends HistoryServiceBase {

    private HistoryTemplateDao historyTemplateDao = mock(HistoryTemplateDao.class);
    private HistoryTemplateServiceImpl historyTemplateService;

    @Before
    public void testSetUp() {
        // Reset mocks
        reset(historyTemplateDao);

        // Reset the service under test
        historyTemplateService = new HistoryTemplateServiceImpl();
        historyTemplateService.setHistoryTemplateDao(historyTemplateDao);
    }

    @Test
    public void testRenderWithoutEventReturnsNull() {
        Assert.assertNull(historyTemplateService.renderEventMessage(null, createActor(), new HashMap<>()));
    }

    @Test
    public void testRenderWithoutCurrentEntityReturnsNull() {
        Assert.assertNull(historyTemplateService.renderEventMessage(generateUserEvent(), null, new HashMap<>()));
    }

    @Test
    public void testRenderWithoutTemplateReturnsNull() {
        when(historyTemplateDao.findOneByTypeAndRole(any(), any())).thenReturn(null);

        Assert.assertNull(historyTemplateService.renderEventMessage(generateUserEvent(), createActor(), new HashMap<>()));
    }

    @Test
    public void testRenderWithTemplateSubstitutesValues() {
        HistoryTemplate template = new HistoryTemplate();
        template.setTemplate("{actor} added {subject} to {object}.");

        Map<String, String> subs = new HashMap<>(3);
        subs.put("actor", "Boss");
        subs.put("subject", "Minion");
        subs.put("object", "Team Evil");

        when(historyTemplateDao.findOneByTypeAndRole(any(), any())).thenReturn(template);

        String result = historyTemplateService.renderEventMessage(generateUserEvent(), createActor(), subs);
        Assert.assertEquals("Boss added Minion to Team Evil.", result);
    }

    @Test
    public void testRenderWithTemplateMissingInputValuesShowUnknown() {
        HistoryTemplate template = new HistoryTemplate();
        template.setTemplate("{actor} added {subject} to {object}.");

        Map<String, String> subs = new HashMap<>(3);

        when(historyTemplateDao.findOneByTypeAndRole(any(), any())).thenReturn(template);

        String result = historyTemplateService.renderEventMessage(generateUserEvent(), createActor(), subs);
        Assert.assertEquals("unknown added unknown to unknown.", result);
    }
}
