package za.co.entelect.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter 
@Setter
@AllArgsConstructor
public class RoleDTO implements Serializable {
    private String name;
}
