package io.spring.app.service;

import java.util.Set;

public interface BaseService<T> {

    void save(T t);
    Set<T> getAll();
    T getById(Long id);
    void deleteById(Long id);
}
