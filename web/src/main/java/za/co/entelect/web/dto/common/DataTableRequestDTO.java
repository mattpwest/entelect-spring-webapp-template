package za.co.entelect.web.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataTableRequestDTO {

    // Maps to the items from the request that need to be sorted and what direction
    private List<String> sort;
    
    // Maps to the value that is being searched
    private String name;
    
    // Maps to the page number that we are on
    private int page;
    
    // Maps to the amount of items that is requested
    private int size;
}
