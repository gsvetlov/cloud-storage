package ru.svetlov.server.service;

import ru.svetlov.domain.entity.Entity;

import java.util.List;

public interface DataRepository<T extends Entity> {
    T add(T entity);
    boolean update(T entity);
    boolean remove(T entity);
    T get(int id);
    List<T> getAll();
}
