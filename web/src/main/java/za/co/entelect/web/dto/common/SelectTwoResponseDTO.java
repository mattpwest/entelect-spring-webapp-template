package za.co.entelect.web.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SelectTwoResponseDTO<T> {

    private List<T> results;
    private boolean more;

}
