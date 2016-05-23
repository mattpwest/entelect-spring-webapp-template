package za.co.entelect.web.forms.user;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class BanForm {

    @NotEmpty
    @NotNull
    @Size(max = 500, message="Reason can only be a maximum of 500 characters.")
    private String reason;

    private String banned;

    private String name;

    private String heading;

    private String action;
}
