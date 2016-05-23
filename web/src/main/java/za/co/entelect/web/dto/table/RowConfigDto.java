package za.co.entelect.web.dto.table;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class RowConfigDto {

    private Collection<ColumnDto> columns =  new ArrayList<>();
}
