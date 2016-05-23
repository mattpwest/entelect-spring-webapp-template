package za.co.entelect.persistence.email;

import org.springframework.data.repository.PagingAndSortingRepository;
import za.co.entelect.domain.entities.email.Template;

public interface TemplateDao extends PagingAndSortingRepository<Template, Long> {
    Template findByName(String name);
    Template findById(Integer id);
}
