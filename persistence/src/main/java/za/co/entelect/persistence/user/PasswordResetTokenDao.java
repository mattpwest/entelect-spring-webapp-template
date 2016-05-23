package za.co.entelect.persistence.user;

import org.springframework.data.repository.PagingAndSortingRepository;
import za.co.entelect.domain.entities.user.PasswordResetToken;

public interface PasswordResetTokenDao extends PagingAndSortingRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);
}
