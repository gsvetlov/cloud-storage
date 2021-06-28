package ru.svetlov.server.factory;

import java.lang.reflect.Type;
import java.util.Optional;

public interface ServiceLocator {
    void addService(Type serviceName, Object serviceInstance);
    Optional<Object> getService(Type serviceName);
}
