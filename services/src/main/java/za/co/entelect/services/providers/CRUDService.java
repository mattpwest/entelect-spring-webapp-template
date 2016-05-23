package za.co.entelect.services.providers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * This interface is copied from Spring Data's PagingAndSortingRepository and superclasses.
 *
 * @param <T> Type for which the service provides CRUD.
 * @param <ID> Type of the domain model's entity field.
 */
public interface CRUDService<T, ID extends Serializable> {

    <S extends T> S save(S var1);

    <S extends T> Iterable<S> save(Iterable<S> var1);

    T findOne(ID var1);

    boolean exists(ID var1);

    Iterable<T> findAll();

    Iterable<T> findAll(Iterable<ID> var1);

    long count();

    void delete(ID var1);

    void delete(T var1);

    void delete(Iterable<? extends T> var1);

    void deleteAll();

    Iterable<T> findAll(Sort var1);

    Page<T> findAll(Pageable var1);


}
