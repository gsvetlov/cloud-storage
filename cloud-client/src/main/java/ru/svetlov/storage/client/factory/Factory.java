package ru.svetlov.storage.client.factory;

import ru.svetlov.domain.service.viewer.FileInfoProvider;
import ru.svetlov.storage.client.service.adapter.CloudClientService;
import ru.svetlov.storage.client.service.adapter.impl.CloudClientServiceAdapter;
import ru.svetlov.storage.client.service.router.RemoteStorageService;
import ru.svetlov.storage.client.service.viewer.impl.LocalFileService;
import ru.svetlov.storage.client.service.network.NetworkClient;
import ru.svetlov.storage.client.service.network.impl.NetClient;
import ru.svetlov.storage.client.service.router.CommandRouterService;

public class Factory {
    public static FileInfoProvider getLocalStorage() {
        return new LocalFileService();
    }

    public static NetworkClient getNetworkClient() {
        return new NetClient();
    }

    public static RemoteStorageService getRemoteStorage() {
        return new CommandRouterService(Factory.getNetworkClient());
    }

    public static CloudClientService getCloudClient() {
        return new CloudClientServiceAdapter(getLocalStorage(), getRemoteStorage());
    }
}
