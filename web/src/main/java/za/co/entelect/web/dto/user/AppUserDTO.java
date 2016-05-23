package za.co.entelect.web.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import za.co.entelect.dto.RoleDTO;

import java.io.Serializable;
import java.util.Collection;

@Getter 
@Setter
@AllArgsConstructor
public class AppUserDTO implements Serializable {

    private Integer id;

    private String email;
    
    private String firstName;

    private String lastName;
    
    private String displayName;
    
    private Boolean enabled;
    
    private Collection<RoleDTO> roles;

}
