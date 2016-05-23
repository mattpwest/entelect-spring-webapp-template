package za.co.entelect.web.controllers.web.error;

import org.junit.Test;
import za.co.entelect.test.ControllerBase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ErrorHandlerControllerTest extends ControllerBase {

    @Test
    public void testError() throws Exception {
        mockMvc.perform(get("/error"))
            .andExpect(status().isOk())
            .andExpect(view().name("/error/error"));
    }

    @Test
    public void testNotFound() throws Exception {
        mockMvc.perform(get("/404"))
            .andExpect(status().isOk())
            .andExpect(view().name("/error/404"));
    }

}
