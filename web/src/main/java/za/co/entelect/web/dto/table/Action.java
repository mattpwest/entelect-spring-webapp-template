package za.co.entelect.web.dto.table;

import lombok.Data;

@Data
public class Action {

    private ActionType actionType;

    private String actionURL;

    public enum ActionType{
        DELETE,EDIT,EDIT_MEMBERS,EDIT_TEAMS
    }
}
