package za.co.entelect.services.providers.history.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import za.co.entelect.UnitTest;
import za.co.entelect.persistence.user.AppUserDao;


import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class HistoryEntityLoaderImplTest extends HistoryServiceBase {

    private HistoryEntityLoaderImpl historyEntityLoader = new HistoryEntityLoaderImpl();

    private AppUserDao userDao = mock(AppUserDao.class);

    @Before
    public void testSetUp() {
        // Reset mocks
        reset(userDao);

        when(userDao.findOne(eq(1L))).thenReturn(user);

        // Setup class under test
        historyEntityLoader.setAppUserDao(userDao);
    }

    @Test
    public void testLoaders() {
        Assert.assertNotNull(historyEntityLoader.loadEntity(user.getEntityType(), user.getId()));
    }

    @Test
    public void testUnsupportedEntityTypeReturnsNull() {
        Assert.assertNull(historyEntityLoader.loadEntity("Batman", 1L));
    }
}
