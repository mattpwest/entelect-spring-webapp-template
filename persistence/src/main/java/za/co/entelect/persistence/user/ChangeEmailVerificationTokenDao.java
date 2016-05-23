package za.co.entelect.persistence.user;

import org.springframework.data.repository.PagingAndSortingRepository;
import za.co.entelect.domain.entities.user.ChangeEmailVerificationToken;

public interface ChangeEmailVerificationTokenDao extends PagingAndSortingRepository<ChangeEmailVerificationToken, Integer> {

    ChangeEmailVerificationToken findByToken(String token);

}
