package za.co.entelect.services.providers.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.entelect.services.providers.CRUDService;

import java.io.Serializable;

@Service
@Slf4j
public abstract class CRUDServiceImpl<T, ID extends Serializable> implements CRUDService<T, ID> {

    @Autowired
    @Setter
    protected PagingAndSortingRepository<T, ID> dao;

    @Override
    @Transactional
    public <S extends T> S save(S var1) {
        return dao.save(var1);
    }

    @Override
    @Transactional
    public <S extends T> Iterable<S> save(Iterable<S> var1) {
        return dao.save(var1);
    }

    @Override
    @Transactional
    public T findOne(ID var1) {
        return dao.findOne(var1);
    }

    @Override
    @Transactional
    public boolean exists(ID var1) {
        return dao.exists(var1);
    }

    @Override
    @Transactional
    public Iterable<T> findAll() {
        return dao.findAll();
    }

    @Override
    @Transactional
    public Iterable<T> findAll(Iterable<ID> var1) {
        return dao.findAll(var1);
    }

    @Override
    @Transactional
    public long count() {
        return dao.count();
    }

    @Override
    @Transactional
    public void delete(ID var1) {
        dao.delete(var1);
    }

    @Override
    @Transactional
    public void delete(T var1) {
        dao.delete(var1);
    }

    @Override
    @Transactional
    public void delete(Iterable<? extends T> var1) {
        dao.delete(var1);
    }

    @Override
    @Transactional
    public void deleteAll() {
        dao.deleteAll();
    }

    @Override
    @Transactional
    public Iterable<T> findAll(Sort var1) {
        return dao.findAll(var1);
    }

    @Override
    @Transactional
    public Page<T> findAll(Pageable var1) {
        return dao.findAll(var1);
    }
}

