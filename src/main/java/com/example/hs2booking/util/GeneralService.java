package com.example.hs2booking.util;

import com.example.hs2booking.controller.exceptions.not_found.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class GeneralService<T, E> {

    public List<E> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<T> foundPage = getRepository().findAll(pageable);
        return foundPage.getContent()
                .stream()
                .map(getMapper()::entityToDto)
                .toList();
    }

    public E findById(long id) {
        return getMapper().entityToDto(getEntityById(id));
    }

    public E create(E dto) {
        T entity = getMapper().dtoToEntity(dto);
        getRepository().save(entity);
        return getMapper().entityToDto(entity);
    }

    public void delete(long id) {
        getEntityById(id);
        getRepository().deleteById(id);
    }

    public T getEntityById(long id) {
        return getRepository().findById(id)
                .orElseThrow(() -> getNotFoundIdException(id));
    }

    protected abstract NotFoundException getNotFoundIdException(long id);

    protected abstract Mapper<T, E> getMapper();

    protected abstract JpaRepository<T, Long> getRepository();

}
