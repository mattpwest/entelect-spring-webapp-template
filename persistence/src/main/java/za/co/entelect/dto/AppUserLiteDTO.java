package za.co.entelect.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter 
@Setter
public class AppUserLiteDTO implements Serializable {

    private Long id;

    private String firstName;

    private String lastName;

    private Boolean enabled;

    private List<String> roles = new ArrayList<>(1);

    public AppUserLiteDTO(Long id, String firstName, String lastName, Boolean enabled) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
    }

}
