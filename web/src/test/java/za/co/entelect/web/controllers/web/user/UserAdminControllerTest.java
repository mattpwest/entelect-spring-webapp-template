package za.co.entelect.web.controllers.web.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.user.Role;
import za.co.entelect.persistence.user.AppUserDao;
import za.co.entelect.persistence.user.RoleDao;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.services.providers.dto.forms.user.UserCreationForm;
import za.co.entelect.services.providers.exceptions.EmailInUseException;
import za.co.entelect.services.security.dto.CustomUser;
import za.co.entelect.test.ControllerBase;
import za.co.entelect.web.forms.user.UserRolesForm;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class UserAdminControllerTest extends ControllerBase {

    private static final String TEST_NAME = "Yugi";
    private static final String TEST_SURNAME = "Test";
    private static final String TEST_EMAIL = "testy@example.com";

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserDao appUserDaoMock;

    @Autowired
    private RoleDao roleDaoMock;

    private AppUser testUser;
    private AppUser testAdminUser;
    private Role testRole;
    private UserDetails userDetails;
    private UserDetails userDetailsWithAdminRole;

    @Before
    public void setUpTest() {
        reset(appUserService);
        reset(appUserDaoMock);
        reset(roleDaoMock);

        testRole = new Role();

        testUser = new AppUser();
        testUser.setId(1L);
        userDetails = new CustomUser(TEST_EMAIL, "123", new ArrayList<>(), testUser);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        testAdminUser = new AppUser();
        testAdminUser.setId(2L);
        userDetailsWithAdminRole = new CustomUser(TEST_EMAIL, "123", authorities, testAdminUser);

        when(appUserService.findOne(1L)).thenReturn(testUser);
        when(appUserService.findOne(2L)).thenReturn(testAdminUser);
    }

    @Test
    public void testViewUserListWithoutAdminRole() throws Exception {
        mockMvc.perform(get("/admin/users")
            .with(csrf())
            .with(user(userDetails)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void testViewUserList() throws Exception {
        when(appUserDaoMock.findAll(any(Pageable.class))).thenReturn(mock(Page.class));

        mockMvc.perform(get("/admin/users")
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/admin/list"));
    }

    @Test
    public void testResetPassword() throws Exception {
        when(appUserDaoMock.findOne(any(Long.class))).thenReturn(testUser);

        mockMvc.perform(post("/admin/users/resetPassword")
                .param("userId", "1")
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("message", is(not(nullValue()))))
            .andExpect(redirectedUrl("/admin/users"));

        verify(appUserService).sendForgotPasswordToken(eq(testUser), any(String.class));
    }

    @Test
    public void testResetPasswordUserNotFound() throws Exception {
        when(appUserDaoMock.findOne(any(Long.class))).thenReturn(null);

        mockMvc.perform(post("/admin/users/resetPassword")
                .param("userId", "1")
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("error", is(not(nullValue()))))
            .andExpect(redirectedUrl("/admin/users"));

        verify(appUserService, times(0))
            .sendForgotPasswordToken(eq(testUser), any(String.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(appUserDaoMock.findOne(any(Long.class))).thenReturn(testUser);

        mockMvc.perform(post("/admin/users/delete")
                .param("userId", "1")
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("message", is(not(nullValue()))))
            .andExpect(redirectedUrl("/admin/users"));

        verify(appUserDaoMock).delete(eq(testUser));
    }

    @Test
    public void testDeleteUserUserNotFound() throws Exception {
        when(appUserDaoMock.findOne(any(Long.class))).thenReturn(null);

        mockMvc.perform(post("/admin/users/delete")
                .param("userId", "1")
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("error", is(not(nullValue()))))
            .andExpect(redirectedUrl("/admin/users"));

        verify(appUserDaoMock, times(0)).delete(eq(testUser));
    }

    @Test
    public void testDisableUser() throws Exception {
        when(appUserDaoMock.findOne(any(Long.class))).thenReturn(testUser);

        mockMvc.perform(post("/admin/users/disable")
                .param("userId", "1")
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("message", is(not(nullValue()))))
            .andExpect(redirectedUrl("/admin/users"));

        verify(appUserDaoMock).save(eq(testUser));
        Assert.assertFalse(testUser.getEnabled());
    }

    @Test
    public void testDisableUserUserNotFound() throws Exception {
        when(appUserDaoMock.findOne(any(Long.class))).thenReturn(null);

        mockMvc.perform(post("/admin/users/disable")
                .param("userId", "1")
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("error", is(not(nullValue()))))
            .andExpect(redirectedUrl("/admin/users"));

        verify(appUserDaoMock, times(0)).save(eq(testUser));
    }

    @Test
    public void testEnableUser() throws Exception {
        when(appUserDaoMock.findOne(any(Long.class))).thenReturn(testUser);

        mockMvc.perform(post("/admin/users/enable")
                .param("userId", "1")
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("message", is(not(nullValue()))))
            .andExpect(redirectedUrl("/admin/users"));

        verify(appUserDaoMock).save(eq(testUser));
        Assert.assertTrue(testUser.getEnabled());
    }

    @Test
    public void testEnableUserUserNotFound() throws Exception {
        when(appUserDaoMock.findOne(any(Long.class))).thenReturn(null);

        mockMvc.perform(post("/admin/users/enable")
                .param("userId", "1")
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("error", is(not(nullValue()))))
            .andExpect(redirectedUrl("/admin/users"));

        verify(appUserDaoMock, times(0)).save(eq(testUser));
    }

    @Test
    public void testUserRolesView() throws Exception {
        when(appUserDaoMock.findOne(any(Long.class))).thenReturn(testUser);

        List<Role> roles = new ArrayList<>();
        roles.add(testRole);
        when(roleDaoMock.findAll()).thenReturn(roles);

        mockMvc.perform(get("/admin/users/1/roles")
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/admin/roles"))
            .andExpect(model().attribute("user", testUser))
            .andExpect(model().attribute("roles", roles))
            .andExpect(model().attribute("userRolesForm", is(not(nullValue()))));
    }

    @Test
    public void testUserRolesViewUserNotFound() throws Exception {
        when(appUserDaoMock.findOne(any(Long.class))).thenReturn(null);

        List<Role> roles = new ArrayList<>();
        roles.add(testRole);
        when(roleDaoMock.findAll()).thenReturn(roles);

        mockMvc.perform(get("/admin/users/1/roles")
            .with(csrf())
            .with(user(userDetailsWithAdminRole)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/users"))
            .andExpect(flash().attribute("error", is(not(nullValue()))));
    }

    @Test
    public void testUpdateUserRoles() throws Exception {
        UserRolesForm form = new UserRolesForm();
        form.setRoles(new Long[]{1L});

        mockMvc.perform(post("/admin/users/1/roles")
                .param("roles", "1")
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/users"))
            .andExpect(flash().attribute("message", is(not(nullValue()))));

        verify(appUserService).updateUserRoles(1L, form.getRoles());
    }

    @Test
    public void testCreateUser() throws Exception {
        mockMvc.perform(post("/admin/users/create")
                    .param("firstName", TEST_NAME)
                    .param("lastName", TEST_SURNAME)
            .param("email", TEST_EMAIL)
            .with(csrf())
            .with(user(userDetailsWithAdminRole)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/users"))
            .andExpect(flash().attribute("message", is(not(nullValue()))));

        verify(appUserService).createUser(any(UserCreationForm.class), any(AppUser.class), any(String.class), any(String.class));
    }

    @Test
    public void testCreateUserWithEmailInUse() throws Exception {
        doThrow(new EmailInUseException()).when(appUserService).createUser(
            any(UserCreationForm.class), any(AppUser.class), any(String.class), any(String.class));

        when(appUserDaoMock.findAll(any(Pageable.class))).thenReturn(mock(Page.class));

        mockMvc.perform(post("/admin/users/create")
                .param("firstName", TEST_NAME)
                .param("lastName", TEST_SURNAME)
                .param("email", TEST_EMAIL)
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/admin/list"))
            .andExpect(model().attribute("page", is(not(nullValue()))))
            .andExpect(model().attribute("createUserFailed", true))
            .andExpect(model().attribute("form", is(not(nullValue()))))
            .andExpect(model().attribute("banForm", is(not(nullValue()))))
            .andExpect(model().attribute("error", is(not(nullValue()))));
    }

    @Test
    public void testCreateUserWithFormErrors() throws Exception {
        when(appUserDaoMock.findAll(any(Pageable.class))).thenReturn(mock(Page.class));

        mockMvc.perform(post("/admin/users/create")
                .param("lastName", TEST_SURNAME)
                .param("email", TEST_EMAIL)
                .with(csrf())
                .with(user(userDetailsWithAdminRole)))
            .andExpect(status().isOk())
            .andExpect(view().name("/appuser/admin/list"))
            .andExpect(model().attribute("page", is(not(nullValue()))))
            .andExpect(model().attribute("createUserFailed", true))
            .andExpect(model().attribute("form", is(not(nullValue()))))
            .andExpect(model().attribute("banForm", is(not(nullValue()))));

        verify(appUserService, times(0)).createUser(any(UserCreationForm.class), any(AppUser.class),
            any(String.class), any(String.class));
    }
}
