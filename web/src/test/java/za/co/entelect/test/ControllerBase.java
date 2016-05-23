package za.co.entelect.test;

import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import za.co.entelect.IntegrationTest;
import za.co.entelect.test.config.TestRootConfig;
import za.co.entelect.web.config.SpringMVCConfig;
import za.co.entelect.web.config.SpringSecurityConfig;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestRootConfig.class, SpringMVCConfig.class, SpringSecurityConfig.class})
@WebAppConfiguration
public abstract class ControllerBase {

    static {
        System.setProperty("ENTELECT_ENV", "test");
    }

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Before
    public void setUpTestContext() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.
                    webAppContextSetup(webApplicationContext).
                    apply(springSecurity()).
                build();
        }
    }
}
