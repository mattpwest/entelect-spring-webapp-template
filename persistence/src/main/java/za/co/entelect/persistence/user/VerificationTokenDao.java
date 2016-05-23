package za.co.entelect.persistence.user;

import org.springframework.data.repository.PagingAndSortingRepository;
import za.co.entelect.domain.entities.user.VerificationToken;

public interface VerificationTokenDao extends PagingAndSortingRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);
}
