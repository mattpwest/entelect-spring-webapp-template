package za.co.entelect.web.forms;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class TemplateForm {

    @NotNull
    @NotEmpty
    @Size(max = 100, message="Subject can only be a maximum of 100 characters.")
    private String subject;

    @NotNull
    @NotEmpty
    @Size(max = 10000, message="Body can only be a maximum of 10000 characters.")
    private String body;
}
