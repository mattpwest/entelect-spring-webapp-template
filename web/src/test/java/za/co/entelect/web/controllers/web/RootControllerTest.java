package za.co.entelect.web.controllers.web;

import org.junit.Test;
import za.co.entelect.test.ControllerBase;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class RootControllerTest extends ControllerBase {

    @Test
    public void testRoot() throws Exception {
        mockMvc.perform(get("/").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("/home"));
    }
}
