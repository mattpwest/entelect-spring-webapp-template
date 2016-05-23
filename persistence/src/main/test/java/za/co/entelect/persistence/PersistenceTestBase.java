package za.co.entelect.persistence;

import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import za.co.entelect.IntegrationTest;
import za.co.entelect.config.JPAConfig;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("h2Db")
@ContextConfiguration(classes = {JPAConfig.class})
public class PersistenceTestBase {
}
