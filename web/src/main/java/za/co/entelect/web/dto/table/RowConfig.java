package za.co.entelect.web.dto.table;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class RowConfig {

    private Collection<Column> columns =  new ArrayList<>();
}
