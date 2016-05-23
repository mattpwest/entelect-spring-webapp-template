package za.co.entelect.web.controllers.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import za.co.entelect.dto.AppUserLiteDTO;
import za.co.entelect.services.providers.AppUserService;
import za.co.entelect.web.controllers.web.AbstractBaseRestController;
import za.co.entelect.web.dto.common.DataTableRequestDTO;
import za.co.entelect.web.dto.common.DataTableResponseDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for the user list & search.
 */
@RestController
@Slf4j
@RequestMapping(value = "/api/public/users")

public class UserPublicRestController extends AbstractBaseRestController<AppUserLiteDTO> {

    @Autowired
    private AppUserService appUserService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public DataTableResponseDTO<AppUserLiteDTO>
            usersList(@ModelAttribute DataTableRequestDTO dataTableRequestDTO) {

        Page<AppUserLiteDTO> users = null;
        if (dataTableRequestDTO.getName() == null || dataTableRequestDTO.getName().isEmpty()) {
            users = appUserService.findAllLiteWithRoles(
                getPageRequest(dataTableRequestDTO, getSortOrders(dataTableRequestDTO)));
        } else {
            users = appUserService.findAllLiteWithRolesByName(dataTableRequestDTO.getName(),
                getPageRequest(dataTableRequestDTO, getSortOrders(dataTableRequestDTO)));
        }

        return getList(dataTableRequestDTO, users);
    }

    @Override
    protected List<Sort.Order> getDefaultSortOrder() {
        ArrayList<Sort.Order> defaultSort = new ArrayList<>(1);
        defaultSort.add(new Sort.Order(Sort.Direction.ASC, "lastName"));
        return defaultSort;
    }

    @Override
    protected DataTableResponseDTO<AppUserLiteDTO> getList(DataTableRequestDTO dataTableRequestDTO, Page<AppUserLiteDTO> list) {
        return new DataTableResponseDTO<>(
            list.getTotalElements(),
            list.getTotalElements(),
            list.getContent());
    }
}
