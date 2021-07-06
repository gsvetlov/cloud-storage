package ru.svetlov.server.core.domain;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContext {
    private String username;
    private int sessionToken;
    private String rootPath;
    private ChannelHandlerContext channelHandlerContext;
}
