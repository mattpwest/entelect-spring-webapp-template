package za.co.entelect.services.providers.email.impl;

import za.co.entelect.domain.entities.email.Template;
import za.co.entelect.persistence.email.TemplateDao;
import za.co.entelect.services.providers.email.TemplateService;
import za.co.entelect.services.providers.impl.CRUDServiceImpl;

import javax.annotation.PostConstruct;

public class TemplateServiceImpl extends CRUDServiceImpl<Template, Long> implements TemplateService {

    private TemplateDao templateDao = (TemplateDao) dao;

    @Override
    public Template findByName(String templateName) {
        return templateDao.findByName(templateName);
    }
}
