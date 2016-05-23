package za.co.entelect.services.security.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoryEventDto {

    private LocalDateTime timestamp;
    private String message;
}
