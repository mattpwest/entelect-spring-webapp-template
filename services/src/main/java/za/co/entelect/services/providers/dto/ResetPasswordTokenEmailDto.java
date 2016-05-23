package za.co.entelect.services.providers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResetPasswordTokenEmailDto {
    private String name;
    private String token;
    private String uri;
}
