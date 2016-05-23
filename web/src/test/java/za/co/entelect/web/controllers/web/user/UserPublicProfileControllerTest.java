package za.co.entelect.web.controllers.web.user;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.providers.history.HistoryService;
import za.co.entelect.services.security.dto.CustomUser;
import za.co.entelect.test.ControllerBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class UserPublicProfileControllerTest extends ControllerBase {

    private AppUser testUser;
    private UserDetails userDetails;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private HistoryService historyService;

    @Before
    public void setUpTest() {
        testUser = new AppUser();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("SIMPLE"));

        userDetails = new CustomUser("test@example.com", "123", authorities, testUser);

        reset(appUserService);
        when(appUserService.findOne(any(Long.class))).thenReturn(testUser);

        reset(historyService);
        when(historyService.findHistoryEvents(any(AppUser.class), any(Integer.class)))
            .thenReturn(new PageImpl<>(new ArrayList<>()));
    }

    @Test
    public void testViewProfile() throws Exception {
        mockMvc.perform(get("/profile")
                .with(csrf())
                .with(user(userDetails)))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/view"));
    }

    @Test
    public void testViewPublicProfile() throws Exception {
        mockMvc.perform(get("/users/1")
            .with(csrf())
            .with(user(userDetails)))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/view"));
    }

    @Test
    public void testHistory() throws Exception {
        mockMvc.perform(get("/users/1/history?page=0")
                .with(csrf())
                .with(user(userDetails)))
            .andExpect(status().isOk())
            .andExpect(view().name("/fragments/history :: block"));
    }

    @Test
    public void testUserNotFound() throws Exception {
        when(appUserService.findOne(0L)).thenReturn(null);

        mockMvc.perform(get("/users/0")
            .with(csrf())
            .with(user(userDetails)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/users"))
            .andExpect(flash().attribute("error", is(not(nullValue()))));
    }
}
