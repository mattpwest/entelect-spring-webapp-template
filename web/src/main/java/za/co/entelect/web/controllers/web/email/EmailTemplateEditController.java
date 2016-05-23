package za.co.entelect.web.controllers.web.email;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.spring4.SpringTemplateEngine;
import za.co.entelect.domain.entities.email.Template;
import za.co.entelect.services.providers.email.TemplateService;
import za.co.entelect.web.controllers.web.AbstractBaseController;
import za.co.entelect.web.dto.NavLocation;
import za.co.entelect.web.exceptions.EntityNotFoundException;
import za.co.entelect.web.forms.TemplateForm;

import javax.validation.Valid;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(value = "/admin/templates/edit/{templateName}")
public class EmailTemplateEditController extends AbstractBaseController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    @Qualifier("emailTemplateEngine")
    private SpringTemplateEngine emailTemplateEngine;

    @ModelAttribute("template")
    public Template getTemplate(@PathVariable("templateName") String templateName) {
        Template template = templateService.findByName(templateName);

        if (template == null) {
            throw new EntityNotFoundException(Template.class.getSimpleName());
        }

        return template;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView editTemplate(@ModelAttribute TemplateForm templateForm,
                                     ModelMap model) {
        setNav(model, NavLocation.ADMIN);

        Template template = (Template) model.get("template");
        model.put("template", template);
        templateForm.setSubject(template.getSubject());
        templateForm.setBody(template.getBody());

        return new ModelAndView("/template/admin/edit", model);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ModelAndView editTemplateSubmit(@ModelAttribute @Valid TemplateForm templateForm,
                                           BindingResult binding,
                                           ModelMap model,
                                           RedirectAttributes redirectAttributes) {

        Template template = (Template) model.get("template");

        if (binding.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Template update failed.");
            return new ModelAndView("redirect:/admin/templates/edit/" + template.getName());
        }

        template.setSubject(templateForm.getSubject());
        template.setBody(templateForm.getBody());
        templateService.save(template);

        emailTemplateEngine.getCacheManager().clearAllCaches();

        redirectAttributes.addFlashAttribute("message", "Template updated.");
        return new ModelAndView("redirect:/admin/templates");
    }
}
