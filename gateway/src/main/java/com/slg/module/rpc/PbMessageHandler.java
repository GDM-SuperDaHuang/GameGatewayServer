package com.slg.module.rpc;

import com.slg.module.message.ByteBufferMessage;
import com.slg.module.register.HandleBeanDefinitionRegistryPostProcessor;
import com.slg.module.util.BeanTool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;


import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Component
@ChannelHandler.Sharable
public class PbMessageHandler extends SimpleChannelInboundHandler<ByteBufferMessage> {
    @Autowired
    private HandleBeanDefinitionRegistryPostProcessor postProcessor;

    private final EventLoopGroup forwardingGroup = new NioEventLoopGroup(4);
    private final Map<String, Channel> serverChannels = new ConcurrentHashMap<>();
    private final Bootstrap bootstrap;
    private final IdleStateHandler idleStateHandler = new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS);
    private final IdleStateEventHandler idleStateEventHandler = new IdleStateEventHandler();

    public PbMessageHandler() {
        bootstrap = new Bootstrap();
        bootstrap.group(forwardingGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new MsgDecode());
                        p.addLast(new MsgEncode());
                        p.addLast(idleStateHandler);
                        p.addLast(idleStateEventHandler);
                        p.addLast(new TargetServerHandler(null));
                    }
                });
    }

    /**
     *
     * @param ctx 客户端-网关连接
     * @param byteBufferMessage 信息
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBufferMessage byteBufferMessage) throws Exception {
        int protocolId = byteBufferMessage.getProtocolId();
        long sessionId = byteBufferMessage.getSessionId();
        Method parse = postProcessor.getParseFromMethod(protocolId);
        if (parse == null) {
            ctx.close();
            return;
        }

        //todo
        //注册中心获取信息，进行选择
        if (true){
            //本地
            Object msgObject = parse.invoke(null, byteBufferMessage.getByteBuffer());
            //todo
            route(ctx, msgObject, protocolId,sessionId);
        }else {
            //转发
            // 根据用户信息选择目标服务器
            String targetServerAddress = getTargetServerAddress("--");
            // 转发到目标服务器
            forwardToTargetServer(ctx, byteBufferMessage, targetServerAddress);
        }

    }


    //todo 获取主机
    private String getTargetServerAddress(String userInfo) {
        // 简单逻辑：根据用户信息选择目标服务器
        if (userInfo.startsWith("user1")) {
            return "127.0.0.1:8081";
        } else if (userInfo.startsWith("user2")) {
            return "127.0.0.1:8082";
        }
        return "127.0.0.1:8081"; // 默认服务器
    }

    //转发到目标服务器
    private void forwardToTargetServer(ChannelHandlerContext ctx, ByteBufferMessage msg, String targetServerAddress) {
        try {
            Channel channel = getOrCreateChannel(targetServerAddress);
            if (channel != null && channel.isActive()) {
                channel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        // 消息转发成功的处理
                        // log.debug("Successfully forwarded message to {}, protocolId: {}",targetServerAddress, msg.getProtocolId());
                    } else {
                        // 消息转发失败的处理
                        log.error("Failed to forward message to {}", targetServerAddress, future.cause());
                        serverChannels.remove(targetServerAddress);
                        ctx.writeAndFlush(new ByteBufferMessage(msg.getProtocolId(),msg.getSessionId(), "Forward failed".getBytes()));
                    }
                });
            } else {
                log.error("No active channel for {}", targetServerAddress);
                ctx.writeAndFlush(new ByteBufferMessage(msg.getProtocolId(),msg.getSessionId(), "No connection".getBytes()));
            }
        } catch (Exception e) {
            log.error("Error forwarding message to {}", targetServerAddress, e);
            ctx.writeAndFlush(new ByteBufferMessage(msg.getProtocolId(),msg.getSessionId(), "Internal error".getBytes()));
        }
    }
    private Channel getOrCreateChannel(String targetServerAddress) {
        return serverChannels.computeIfAbsent(targetServerAddress, address -> {
            String[] parts = address.split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            
            try {
                ChannelFuture future = bootstrap.connect(host, port).sync();
                if (future.isSuccess()) {
                    return future.channel();
                }
            } catch (Exception e) {
                log.error("Failed to create channel for {}", address, e);
            }
            return null;
        });
    }

    @PreDestroy
    public void destroy() {
        serverChannels.values().forEach(Channel::close);
        serverChannels.clear();
        forwardingGroup.shutdownGracefully();
    }

    //todo
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof InvocationTargetException) {
            //目标方法错误
        } else if (cause instanceof SocketException
                || cause instanceof DecoderException) {
            //客户端关闭连接/连接错误
            // 关闭连接
            ctx.close();
        }
    }


    public void route(ChannelHandlerContext ctx, Object message, int protocolId,long userId) throws Exception {
        Class<?> handleClazz = postProcessor.getHandleMap(protocolId);
        if (handleClazz == null) {
            return;
        }
        Method method = postProcessor.getMethodMap(protocolId);
        if (method == null) {
            return;
        }
        method.setAccessible(true);
        Object bean = BeanTool.getBean(handleClazz);
        if (bean == null) {
            return;
        }
        method.invoke(bean, ctx, message,userId);
    }
}
