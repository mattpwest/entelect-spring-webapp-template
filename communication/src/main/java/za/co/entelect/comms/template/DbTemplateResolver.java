package za.co.entelect.comms.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import za.co.entelect.domain.entities.email.Template;
import za.co.entelect.persistence.email.TemplateDao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DbTemplateResolver extends TemplateResolver {

    @Autowired
    TemplateDao templateDao;

    public DbTemplateResolver(){
        setResourceResolver(new DbResourceResolver());
    }

    @Override
    protected String computeResourceName(TemplateProcessingParameters params){
        return params.getTemplateName();
    }

    private class DbResourceResolver implements IResourceResolver {
        @Override
        public String getName() {
            return "dbResourceResolver";
        }

        @Override
        public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters, String resourceName) {
            Template template = templateDao.findByName(resourceName);
            if(template != null){
                return new ByteArrayInputStream(template.getBody().getBytes());
            }
            return null;
        }
    }
}
