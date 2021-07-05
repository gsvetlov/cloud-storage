package ru.svetlov.storage.client.service.network.impl;

import io.netty.channel.Channel;

public class PipelineManager {
    private final Channel channel;

    public PipelineManager(Channel channel) {
        this.channel = channel;
    }

    public void configure(PipelineConfigurationType type){

    }

}

