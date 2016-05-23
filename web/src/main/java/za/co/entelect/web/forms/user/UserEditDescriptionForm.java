package za.co.entelect.web.forms.user;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserEditDescriptionForm {

    @NotNull
    @NotEmpty
    @Size(max = 10000, message="Reason can only be a maximum of 10000 characters.")
    private String description;

    private String action;

    private String name;
}
