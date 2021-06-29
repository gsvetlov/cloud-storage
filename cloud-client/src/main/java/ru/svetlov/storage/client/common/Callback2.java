package ru.svetlov.storage.client.common;

@FunctionalInterface
public interface Callback2<T1, T2> {
    void call(T1 parameter1, T2 parameter2);
}
