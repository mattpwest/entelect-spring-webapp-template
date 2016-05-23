package za.co.entelect.web.dto.table;

import lombok.Data;

@Data
public class TableConfigDto {

    private RowConfigDto rowConfig;

    private String entityURL;

    private String pagingListURL;

}
