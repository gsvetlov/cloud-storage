package ru.svetlov.server.service.pool.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.svetlov.domain.command.base.annotations.ACommandHandler;
import ru.svetlov.server.factory.ServiceLocator;
import ru.svetlov.server.core.handler.command.CommandHandler;
import ru.svetlov.server.service.pool.CommandRepositoryProvider;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryCommandRepository implements CommandRepositoryProvider {
    private static final Logger log = LogManager.getLogger();
    private final SortedMap<String, CommandHandler> map;
    private final ServiceLocator locator;

    public InMemoryCommandRepository(ServiceLocator locator) {
        this.locator = locator;
        map = new TreeMap<>();
        initMap();
    }

    @Override
    public Map<String, CommandHandler> getCollection() {
        return Collections.unmodifiableMap(map);
    }

    private void initMap() {
        // TODO: заменить на конфиг
        Path basePath = Paths.get("./cloud-server/src/main/java/ru/svetlov/server/core/handler/command");
        List<Class<? extends CommandHandler>> classes = loadClasses(basePath);

        instantiateHandlers(classes);
    }

    private void instantiateHandlers(List<Class<? extends CommandHandler>> classes) {
        for (Class<? extends CommandHandler> aClass : classes) {
            Constructor<?>[] constructors = aClass.getDeclaredConstructors();

            Object[] parameters;
            if (constructors.length > 0)
                parameters = getConstructorParameters(constructors[0]);
            else
                parameters = new Object[0];

            CommandHandler handler = getHandlerInstance(constructors[0], parameters);
            if (handler != null)
                map.put(aClass.getAnnotation(ACommandHandler.class).command(), handler);
        }
    }

    private CommandHandler getHandlerInstance(Constructor<?> constructor, Object[] parameters) {

        try {
            CommandHandler handler;
            if (parameters.length > 0) {
                handler = (CommandHandler) constructor.newInstance(parameters);
            } else {
                handler = (CommandHandler) constructor.newInstance();
            }
            log.debug("Handler instance created: " + handler);

            return handler;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.warn("Handler creation failed");
            log.throwing(Level.ERROR, e);
        }
        return null;
    }

    private Object[] getConstructorParameters(Constructor<?> constructor) {

        Type[] parameterTypes = constructor.getGenericParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];

        for (int i = 0; i < parameters.length; i++) {
            Optional<Object> service = locator.getService(parameterTypes[i]);
            parameters[i] = service.orElse(null);
        }
        return parameters;
    }

    private List<Class<? extends CommandHandler>> loadClasses(Path basePath) {
        try {
            return Files.list(basePath)
                    .map(Path::toString)
                    .filter(s -> s.endsWith(".java"))
                    .map(InMemoryCommandRepository::convertToClassName)
                    .map(InMemoryCommandRepository::getCommandHandlerClasses)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.throwing(Level.ERROR, e);
        }
        return Collections.emptyList();
    }

    private static String convertToClassName(String s) {
        return s.substring(s.indexOf("java\\") + 5, s.lastIndexOf(".java"))
                .replace("\\", ".");
    }

    private static Class<? extends CommandHandler> getCommandHandlerClasses(String className) {
        try {
            Class<? extends CommandHandler> aClass = Class.forName(className).asSubclass(CommandHandler.class);
            if (aClass.isAnnotationPresent(ACommandHandler.class)) {
                log.debug("{} added to map", className);
                return aClass;
            }
            log.debug("@ACommandHandler is not found for {}", className);
        } catch (ClassNotFoundException e) {
            log.debug("Class not found: {}", e.getMessage());
        } catch (ClassCastException e) {
            log.debug("Class {} is not a CommandHandler", e.getMessage());
        }
        return null;
    }
}
