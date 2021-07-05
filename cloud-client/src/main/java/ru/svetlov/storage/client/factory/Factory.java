package ru.svetlov.storage.client.factory;

import ru.svetlov.storage.client.service.adapter.CloudClient;
import ru.svetlov.storage.client.service.adapter.CloudClientAdapter;
import ru.svetlov.storage.client.service.router.RemoteStorageService;
import ru.svetlov.storage.client.service.file.FileViewService;
import ru.svetlov.storage.client.service.file.impl.LocalFileService;
import ru.svetlov.storage.client.service.network.NetworkClient;
import ru.svetlov.storage.client.service.network.impl.NetClient;
import ru.svetlov.storage.client.service.router.CommandRouterService;

public class Factory {
    public static FileViewService getLocalStorage() {
        return new LocalFileService();
    }

    public static NetworkClient getNetworkClient() {
        return new NetClient();
    }

    public static RemoteStorageService getRemoteStorage() {
        return new CommandRouterService(Factory.getNetworkClient());
    }

    public static CloudClient getCloudClient() {
        return new CloudClientAdapter(getLocalStorage(), getRemoteStorage());
    }
}
