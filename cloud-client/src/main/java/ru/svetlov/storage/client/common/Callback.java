package ru.svetlov.storage.client.common;

@FunctionalInterface
public interface Callback<T> {
    void call(T parameter);
}
