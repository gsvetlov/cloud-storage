package ru.svetlov.server.factory;

import ru.svetlov.server.core.CloudServerService;
import ru.svetlov.server.core.NettyCoreServer;
import ru.svetlov.server.service.DataRepository;
import ru.svetlov.server.service.command.pool.CommandRepositoryProvider;
import ru.svetlov.server.service.command.pool.impl.InMemoryCommandRepository;
import ru.svetlov.server.service.command.pool.impl.InMemoryCommandPool;
import ru.svetlov.server.service.jdbc.TestEntityRepository;
import ru.svetlov.server.service.jdbc.impl.StubDataRepository;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Factory implements ServiceLocator {
    private static volatile Factory _instance;
    private static final Object _lock = new Object();

    public static Factory getInstance() {
        if (_instance != null) return _instance;
        synchronized (_lock) {
            if (_instance == null)
                _instance = new Factory();
        }
        return _instance;
    }

    private final Map<Type, Object> services;

    private Factory() {
        services = new HashMap<>();
        configureServices();
    }

    private void configureServices() {
        services.put(TestEntityRepository.class, new StubDataRepository());
        services.put(CommandRepositoryProvider.class, new InMemoryCommandRepository(this));
        services.put(ru.svetlov.server.service.command.pool.CommandPool.class, new InMemoryCommandPool(this.getCommandRepositoryProvider()));
    }

    public void addService(Type serviceName, Object serviceInstance) {
        services.put(serviceName, serviceInstance);
    }

    public Optional<Object> getService(Type serviceName) {
        Object service = services.get(serviceName);
        if (service == null) return Optional.empty();
        return Optional.of(service);
    }

    public CloudServerService getCloudServerService() {
        return NettyCoreServer.getInstance();
    }

    public CommandRepositoryProvider getCommandRepositoryProvider(){
        return (CommandRepositoryProvider) getService(CommandRepositoryProvider.class).orElse(null);
    }

    public ru.svetlov.server.service.command.pool.CommandPool getCommandPool(){
        return (ru.svetlov.server.service.command.pool.CommandPool) getService(ru.svetlov.server.service.command.pool.CommandPool.class).orElse(null);
    }

}
