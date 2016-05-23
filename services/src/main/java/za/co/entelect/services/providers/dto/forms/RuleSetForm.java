package za.co.entelect.services.providers.dto.forms;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RuleSetForm {

    @NotNull
    @NotEmpty
    @Size(max = 50, message="Rules name can only be a maximum of 50 characters.")
    private String name;

    @NotNull
    @Min(1)
    @Max(255)
    private Integer priority;

    @NotNull
    @NotEmpty
    @Size(max = 150000, message="Rules text can only be a maximum of 150 000 characters.")
    private String rules;
}
