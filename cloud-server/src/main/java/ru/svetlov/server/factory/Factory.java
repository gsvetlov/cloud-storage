package ru.svetlov.server.factory;

import ru.svetlov.server.core.CloudServerService;
import ru.svetlov.server.core.NettyCoreServer;
import ru.svetlov.server.service.configuration.impl.Configuration;
import ru.svetlov.server.service.configuration.impl.ServerConfiguration;
import ru.svetlov.server.service.file.FileInfoProvider;
import ru.svetlov.server.service.file.FileUploadService;
import ru.svetlov.server.service.file.impl.RemoteFileInfoProvider;
import ru.svetlov.server.service.file.impl.FileUploadServiceImpl;
import ru.svetlov.server.service.jdbc.impl.AuthenticationProviderImpl;
import ru.svetlov.server.service.pool.CommandPool;
import ru.svetlov.server.service.pool.CommandRepositoryProvider;
import ru.svetlov.server.service.pool.impl.InMemoryCommandRepository;
import ru.svetlov.server.service.pool.impl.InMemoryCommandPool;
import ru.svetlov.server.service.jdbc.AuthenticationProvider;
import ru.svetlov.server.service.json.JsonMapProvider;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Factory implements ServiceLocator {
    private static final Factory instance;

    static {
        instance = new Factory();
    }

    public static Factory getInstance() {
        return instance;
    }

    private final Map<Type, Object> services;

    private Factory() {
        services = new HashMap<>();
        configureServices();
    }

    private void configureServices() {

        services.put(AuthenticationProvider.class, new AuthenticationProviderImpl(this.getConfiguration()));

        services.put(JsonMapProvider.class, JsonMapProvider.getInstance());

        services.put(FileInfoProvider.class, new RemoteFileInfoProvider());

        services.put(FileUploadService.class, new FileUploadServiceImpl());

        services.put(CommandRepositoryProvider.class, new InMemoryCommandRepository(this));

        services.put(CommandPool.class, new InMemoryCommandPool(this.getCommandRepositoryProvider()));
    }

    public CloudServerService getCloudServerService() {
        return NettyCoreServer.getInstance();
    }

    public AuthenticationProvider getAuthenticationProvider() {
        return (AuthenticationProvider) getService(AuthenticationProvider.class).orElse(null);
    }

    public CommandRepositoryProvider getCommandRepositoryProvider() {
        return (CommandRepositoryProvider) getService(CommandRepositoryProvider.class).orElse(null);
    }

    public CommandPool getCommandPool() {
        return (CommandPool) getService(CommandPool.class).orElse(null);
    }

    public JsonMapProvider getJsonMapProvider() {
        return (JsonMapProvider) getService(JsonMapProvider.class).orElse(null);
    }

    public Configuration getConfiguration() {
        return ServerConfiguration.getInstance();
    }

    public void addService(Type serviceName, Object serviceInstance) {
        services.put(serviceName, serviceInstance);
    }

    public Optional<Object> getService(Type serviceName) {
        Object service = services.get(serviceName);
        if (service == null) return Optional.empty();
        return Optional.of(service);
    }

}
