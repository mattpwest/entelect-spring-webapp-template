package za.co.entelect.web.forms;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ProfileForm {

    private Long userId;

    @NotNull
    @NotEmpty
    @Size(max = 100, message="First name can only be a maximum of 100 characters.")
    private String firstName;

    @NotNull
    @NotEmpty
    @Size(max = 100, message="Last name can only be a maximum of 100 characters.")
    private String lastName;

    public Boolean validate() {
        return !(StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName));
    }
}
