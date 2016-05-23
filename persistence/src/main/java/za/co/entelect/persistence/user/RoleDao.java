package za.co.entelect.persistence.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import za.co.entelect.domain.entities.user.Role;

import java.util.List;

public interface RoleDao extends PagingAndSortingRepository<Role, Long> {
    Role findByName(String name);

    @Query(value = "SELECT cast(substring(r.Name, 6, 10) as varchar) FROM AppUserRole aur LEFT JOIN Role r ON r.id = aur.Role WHERE aur.AppUser = :userId", nativeQuery = true)
    List<String> findRoleNamesByUser(@Param("userId") Long userId);
}
