package za.co.entelect.domain.entities.user.projections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.user.Role;

import java.util.Collection;

@Projection(name = "user", types = { AppUser.class })
public interface AppUserList {

    Long getId();

    String getEmail();

    String getDisplayName();

    String getFirstName();

    String getLastName();

    Boolean getEnabled();

    String getDescription();

    @Value("#{target.roles.isEmpty() ? T(java.util.Collections).EMPTY_LIST : target.roles.![role]}")
    Collection<Role> getRoles();
}
