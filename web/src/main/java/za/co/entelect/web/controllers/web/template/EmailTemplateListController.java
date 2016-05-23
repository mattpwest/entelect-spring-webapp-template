package za.co.entelect.web.controllers.web.template;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import za.co.entelect.web.controllers.web.AbstractBaseController;
import za.co.entelect.web.dto.NavLocation;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(value = "/admin/templates")
public class EmailTemplateListController extends AbstractBaseController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView templatesList(ModelMap model) {
        setNav(model, NavLocation.ADMIN);
        return new ModelAndView("/template/admin/list");
    }
}
