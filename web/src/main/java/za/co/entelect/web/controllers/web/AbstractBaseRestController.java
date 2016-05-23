package za.co.entelect.web.controllers.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import za.co.entelect.web.dto.common.DataTableRequestDTO;
import za.co.entelect.web.dto.common.DataTableResponseDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractBaseRestController<T> {

    protected abstract List<Sort.Order> getDefaultSortOrder();

    protected PageRequest getPageRequest(DataTableRequestDTO dataTableRequestDTO, List<Sort.Order> orders) {
        return new PageRequest(dataTableRequestDTO.getPage(), dataTableRequestDTO.getSize(), new Sort(orders));
    }

    protected PageRequest getPageRequest(DataTableRequestDTO dataTableRequestDTO) {
        return new PageRequest(dataTableRequestDTO.getPage(), dataTableRequestDTO.getSize());
    }

    protected List<Sort.Order> getSortOrders(DataTableRequestDTO dataTableRequestDTO) {
        if (dataTableRequestDTO.getSort() == null || dataTableRequestDTO.getSort().isEmpty()) {
            return getDefaultSortOrder();
        }

        if (dataTableRequestDTO.getSort().get(0).contains(",")) {
            // We are sorting by more than one column so go through each
            // field and split by ',' and set direction and propertyName
            // accordingly
            return dataTableRequestDTO.getSort().stream().map(
                    order -> new Sort.Order(Direction.fromStringOrNull(order.split(",")[1]), order.split(",")[0]))
                    .collect(Collectors.toList());
        } else {
            // We are sorting by only one column so take the first item in
            // the list as the propertyName and the second item as direction
            List<Sort.Order> sortOrders = new ArrayList<Sort.Order>();
            sortOrders.add(new Sort.Order(Direction.fromStringOrNull(dataTableRequestDTO.getSort().get(1)),
                    dataTableRequestDTO.getSort().get(0)));
            return sortOrders;
        }
    }
    
    protected abstract DataTableResponseDTO<T> getList(DataTableRequestDTO dataTableRequestDTO, Page<T> list);

}
