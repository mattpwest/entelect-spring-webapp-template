package za.co.entelect.web.controllers.rest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import za.co.entelect.dto.AppUserLiteDTO;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.test.ControllerBase;
import za.co.entelect.web.controllers.web.RootControllerTest;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserPublicRestControllerTest extends ControllerBase {

    @Autowired
    private AppUserService appUserService;

    @Test
    public void testDefaultList() throws Exception {
        Page<AppUserLiteDTO> result = new PageImpl<>(new ArrayList<>());
        when(appUserService.findAllLiteWithRoles(any(PageRequest.class))).thenReturn(result);

        mockMvc.perform(get("/api/public/users")
            .param("size", "10")
        ).andExpect(status().isOk());
    }

    @Test
    public void testListWithSearch() throws Exception {
        Page<AppUserLiteDTO> result = new PageImpl<>(new ArrayList<>());
        when(appUserService.findAllLiteWithRolesByName(any(String.class), any(PageRequest.class))).thenReturn(result);

        mockMvc.perform(get("/api/public/users")
            .param("name", "Matt")
            .param("size", "10")
        ).andExpect(status().isOk());
    }
}
