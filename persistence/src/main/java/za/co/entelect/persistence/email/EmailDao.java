package za.co.entelect.persistence.email;

import org.springframework.data.repository.PagingAndSortingRepository;
import za.co.entelect.domain.entities.email.Email;

import java.util.List;

public interface EmailDao extends PagingAndSortingRepository<Email, Long> {

    List<Email> findBySentAtIsNullOrderByPriorityAsc();

    Email findByTemplateAndSentAtIsNull(String template);
}
