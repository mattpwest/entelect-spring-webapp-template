package za.co.entelect.persistence.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.domain.entities.user.projections.AppUserList;
import za.co.entelect.dto.AppUserLiteDTO;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "users", path = "users", excerptProjection = AppUserList.class)
public interface AppUserDao extends PagingAndSortingRepository<AppUser, Long> {

    AppUser findByEmail(String email);

    @RestResource(path = "users")
    Page<AppUser> findByFirstNameContainsOrLastNameContains(
                    @Param("name") String firstName,
                    @Param("name") String lastName,
                    Pageable pageable);

    @Query("select new za.co.entelect.dto.AppUserLiteDTO(u.id, u.firstName, u.lastName, u.enabled) from AppUser u")
    Page<AppUserLiteDTO> findAllLite(Pageable pageable);

    @Query("select new za.co.entelect.dto.AppUserLiteDTO(u.id, u.firstName, u.lastName, u.enabled) from AppUser u " +
        "where u.firstName like :name or u.lastName like :name")
    Page<AppUserLiteDTO> findAllLiteByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT u from AppUser u WHERE u.enabled = true")
    List<AppUser> findAllActive();

    @Query("SELECT u from AppUser u WHERE u.enabled = true AND u.id = :id")
    AppUser findActive(@Param("id") Long id);
}
