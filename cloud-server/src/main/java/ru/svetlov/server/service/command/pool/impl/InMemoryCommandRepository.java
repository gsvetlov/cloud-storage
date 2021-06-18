package ru.svetlov.server.service.command.pool.impl;

import ru.svetlov.domain.command.base.GenericCommand;
import ru.svetlov.domain.command.base.TestCommand;
import ru.svetlov.domain.command.base.annotations.ACommandHandler;
import ru.svetlov.server.factory.Factory;
import ru.svetlov.server.factory.ServiceLocator;
import ru.svetlov.server.service.DataRepositoryProvider;
import ru.svetlov.server.service.command.handler.CommandHandler;
import ru.svetlov.server.service.command.pool.CommandRepositoryProvider;
import ru.svetlov.server.service.jdbc.impl.StubDataRepositoryProvider;

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
        Path basePath = Paths.get("./cloud-server/src/main/java/ru/svetlov/server/service/command/handler");
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
            return handler;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(e.getMessage());
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
            e.printStackTrace();
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
            if (aClass.isAnnotationPresent(ACommandHandler.class))
                return aClass;
            System.out.printf("No annotation @ACommandHandler is found in: %s\n", className);
        } catch (ClassNotFoundException e) {
            System.out.println("class not found: " + e.getMessage());
        } catch (ClassCastException e) {
            System.out.printf("Class %s is not a CommandHandler: \n", e.getMessage());
        }
        return null;
    }

    // TODO: debug purposes ONLY!
    public static void main(String[] args) {
        TestCommand command = new TestCommand();
        System.out.println("command: " + command.getCommand());
        Factory.getInstance().getCommandPool().getHandler(command).process(command);
    }
}
