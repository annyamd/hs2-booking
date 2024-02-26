package com.example.hs2booking.util;

public interface Mapper<T, E> {
    E entityToDto(T entity);
    T dtoToEntity(E dto);
}
