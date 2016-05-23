package za.co.entelect.web.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FreeTextMailDto {
    private String name;
    private String senderName;
    private String body;
}
