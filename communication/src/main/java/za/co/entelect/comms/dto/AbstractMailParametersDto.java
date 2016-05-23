package za.co.entelect.comms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbstractMailParametersDto {
    String name;
    String senderName;
    Integer interactionId;
    Integer entityId;
    String entityName;

}
