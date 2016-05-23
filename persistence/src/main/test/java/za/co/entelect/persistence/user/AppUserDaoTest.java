package za.co.entelect.persistence.user;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import za.co.entelect.persistence.PersistenceTestBase;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AppUserDaoTest extends PersistenceTestBase {

    @Autowired
    private AppUserDao appUserDao;

    @Test
    public void testCount() {
        Assert.assertTrue("Test database should contain at least 1 user.", appUserDao.count() > 0);
    }
}
