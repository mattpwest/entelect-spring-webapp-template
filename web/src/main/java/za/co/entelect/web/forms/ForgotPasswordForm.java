package za.co.entelect.web.forms;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ForgotPasswordForm {

    @NotNull
    @NotEmpty
    @Email
    @Size(max = 100,message = "E-mail address cannot be longer than 100 characters")
    private String email;
}
