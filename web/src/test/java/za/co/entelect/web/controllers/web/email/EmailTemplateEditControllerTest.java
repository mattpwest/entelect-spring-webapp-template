package za.co.entelect.web.controllers.web.email;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.spring4.SpringTemplateEngine;
import za.co.entelect.domain.entities.email.Template;
import za.co.entelect.services.providers.email.TemplateService;
import za.co.entelect.test.ControllerBase;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EmailTemplateEditControllerTest extends ControllerBase {

    @Autowired
    private TemplateService templateService;

    @Autowired
    @Qualifier("emailTemplateEngine")
    private SpringTemplateEngine springTemplateEngine;

    @Before
    public void setUp() {
        reset(templateService);

        Template template = new Template();
        template.setId(1L);
        template.setName("testTemplate");
        template.setBody("<p>Test Template</p>");
        template.setSubject("Test Template");

        when(templateService.findByName("testTemplate")).thenReturn(template);
    }

    @Test
    public void testEditTemplate() throws Exception {
        mockMvc.perform(get("/admin/templates/edit/testTemplate")
                .with(csrf())
                .with(user(userAdmin)))
            .andExpect(status().isOk())
            .andExpect(view().name("/template/admin/edit"));
    }

    @Test
    public void testEditTemplateSubmit() throws Exception {
        ICacheManager cacheManager = mock(ICacheManager.class);
        when(springTemplateEngine.getCacheManager()).thenReturn(cacheManager);

        mockMvc.perform(post("/admin/templates/edit/testTemplate")
                .param("subject", "Subjective Subject")
                .param("body", "<p>A perfect body</p>")
                .with(csrf())
                .with(user(userAdmin)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/templates"))
            .andExpect(flash().attributeExists("message"));

        verify(cacheManager, times(1)).clearAllCaches();
    }

    @Test
    public void testEditTemplateSubmitFailedValidation() throws Exception {
        mockMvc.perform(post("/admin/templates/edit/testTemplate")
                .param("subject", "")
                .param("body", "")
                .with(csrf())
                .with(user(userAdmin)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/templates/edit/testTemplate"))
            .andExpect(flash().attributeExists("error"));
    }

    @Test
    public void testEditTemplateNotFound() throws Exception {
        mockMvc.perform(get("/admin/templates/edit/noTemplate")
                .with(csrf())
                .with(user(userAdmin)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/templates"));
    }
}
