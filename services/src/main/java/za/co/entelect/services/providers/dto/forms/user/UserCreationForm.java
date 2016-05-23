package za.co.entelect.services.providers.dto.forms.user;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form for administrator to create users.
 */
@Data
public class UserCreationForm {

    @NotNull
    @NotEmpty
    @Size(max = 100, message="First name can only be a maximum of 100 characters.")
    private String firstName;

    @NotNull
    @NotEmpty
    @Size(max = 100, message="Last name can only be a maximum of 100 characters.")
    private String lastName;

    @NotNull
    @NotEmpty
    @Email
    @Size(max = 100, message="E-mail address can only be a maximum of 100 characters.")
    private String email;
}
