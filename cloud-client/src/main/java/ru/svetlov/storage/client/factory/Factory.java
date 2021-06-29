package ru.svetlov.storage.client.factory;

import ru.svetlov.storage.client.controller.CloudClient;
import ru.svetlov.storage.client.service.adapter.CloudClientAdapter;
import ru.svetlov.storage.client.service.adapter.RemoteStorage;
import ru.svetlov.storage.client.service.file.FileViewService;
import ru.svetlov.storage.client.service.file.impl.TestFileService;
import ru.svetlov.storage.client.service.network.NetworkClient;
import ru.svetlov.storage.client.service.network.impl.NetClient;
import ru.svetlov.storage.client.service.router.CommandRouter;

public class Factory {
    public static FileViewService getLocalStorage() {
        return new TestFileService();
    }

    public static NetworkClient getNetworkClient() {
        return new NetClient();
    }

    public static RemoteStorage getRemoteStorage() {
        return new CommandRouter(Factory.getNetworkClient());
    }

    public static CloudClient getCloudClient() {
        return new CloudClientAdapter(getLocalStorage(), getRemoteStorage());
    }
}
