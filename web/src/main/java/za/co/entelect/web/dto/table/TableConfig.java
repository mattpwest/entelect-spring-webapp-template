package za.co.entelect.web.dto.table;

import lombok.Data;

@Data
public class TableConfig {

    private RowConfig rowConfig;

    private String entityURL;

    private String pagingListURL;

}
