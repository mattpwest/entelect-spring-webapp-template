package za.co.entelect.web.forms;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ResetPasswordForm {

    @NotNull
    @NotEmpty
    @Size(max = 100, message="Password can only be a maximum of 100 characters.")
    private String password;

    @NotNull
    @NotEmpty
    @Size(max = 100, message="Password can only be a maximum of 100 characters.")
    private String passwordConfirmation;

    private String passwordResetToken;
}
