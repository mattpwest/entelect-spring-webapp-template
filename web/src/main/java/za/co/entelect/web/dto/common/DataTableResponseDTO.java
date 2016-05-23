package za.co.entelect.web.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter 
@Setter
@AllArgsConstructor
public class DataTableResponseDTO<T> {

    private long recordsTotal;
    private long recordsFiltered;
    private List<T> data;
}
