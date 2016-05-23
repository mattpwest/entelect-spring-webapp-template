package za.co.entelect.web.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProfileLevelProgressDTO {
    private int progress;
    private String level;
    private String nextLevel;
    private List<String> required = new ArrayList<>();
}
