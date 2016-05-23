package za.co.entelect.services.security.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WarningDto {

    private String reason;
    private LocalDateTime date;
    private Boolean banned;
    private String userIssuedDisplayName;
}
