package za.co.entelect.web.dto.table;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class Column {

    private String header;

    private ColumnType columnType;

    private String defaultURL;

    private Collection<Action> actions = new ArrayList<>();

    public enum ColumnType{
        LOGO,NAME,ACTIONS,LOCATION,CLAN,BAN
    }
}


