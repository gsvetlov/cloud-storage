package ru.svetlov.server.service.jdbc.impl;

import ru.svetlov.domain.entity.TestEntity;
import ru.svetlov.server.service.jdbc.TestEntityRepository;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/***
 * заглушка репозитория
 */

public class StubDataRepository implements TestEntityRepository {
    private static Random random = new Random();

    @Override
    public String toString() {
        return "This is a StubDataRepository";
    }

    @Override
    public TestEntity add(TestEntity entity) {
        return null;
    }

    @Override
    public boolean update(TestEntity entity) {
        return false;
    }

    @Override
    public boolean remove(TestEntity entity) {
        return false;
    }

    @Override
    public TestEntity get(int id) {

        return new TestEntity(id, "StubDataQuery " + id + " complete");
    }

    @Override
    public List<TestEntity> getAll() {
        return Collections.emptyList();
    }

}
