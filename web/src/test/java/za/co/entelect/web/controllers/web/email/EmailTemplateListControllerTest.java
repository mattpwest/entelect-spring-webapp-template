package za.co.entelect.web.controllers.web.email;

import org.junit.Test;
import za.co.entelect.test.ControllerBase;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class EmailTemplateListControllerTest extends ControllerBase {

    @Test
    public void testViewTemplateListingWithoutAdminRoleErrors() throws Exception {
        mockMvc.perform(get("/admin/templates")
                .with(csrf())
                .with(user(userNormal)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void testViewTemplateListingWithAdmin() throws Exception {
        mockMvc.perform(get("/admin/templates")
                .with(csrf())
                .with(user(userAdmin)))
            .andExpect(status().isOk())
            .andExpect(view().name("/template/admin/list"));
    }
}
