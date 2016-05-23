package za.co.entelect.services.providers.email;

import za.co.entelect.domain.entities.email.Template;
import za.co.entelect.services.providers.CRUDService;

public interface TemplateService extends CRUDService<Template, Long> {

    Template findByName(String templateName);
}
