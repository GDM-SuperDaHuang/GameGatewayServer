package com.slg.module.rpc.outside;

import com.google.protobuf.GeneratedMessage;
import com.slg.module.connection.ClientChannelManage;
import com.slg.module.connection.ServerChannelManage;
import com.slg.module.connection.ServerConfig;
import com.slg.module.message.ByteBufferMessage;
import com.slg.module.message.MsgResponse;
import com.slg.module.register.HandleBeanDefinitionRegistryPostProcessor;
import com.slg.module.rpc.interMsg.IdleStateEventHandler;
import com.slg.module.rpc.interMsg.MsgServerInternalDecode;
import com.slg.module.rpc.interMsg.TargetServerHandler;
import com.slg.module.util.BeanTool;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

@Component
@ChannelHandler.Sharable
public class PbMessageHandler extends SimpleChannelInboundHandler<ByteBufferMessage> {
    @Autowired
    private HandleBeanDefinitionRegistryPostProcessor postProcessor;

    //目标服务器--网关管理
    @Autowired
    private ServerChannelManage serverChannelManage;
    //客户端--网关管理
    @Autowired
    private ClientChannelManage clientchannelManage;

    @Autowired
    private TargetServerHandler targetServerHandler;

    private final EventLoopGroup forwardingGroup = new NioEventLoopGroup(4);
    private final Bootstrap bootstrap;
    private final IdleStateHandler idleStateHandler = new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS);
    private final IdleStateEventHandler idleStateEventHandler = new IdleStateEventHandler();

    /**
     * 网关转发
     */
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
                        p.addLast(new MsgServerInternalDecode());
                        p.addLast(targetServerHandler);
                    }
                });
    }


    /**
     * @param ctx 客户端-网关连接
     * @param msg 信息
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBufferMessage msg) throws Exception {
        int protocolId = msg.getProtocolId();
        byte[] body = msg.getBody();
        Channel channel = ctx.channel();

        Method parse = postProcessor.getParseFromMethod(protocolId);
        if (parse == null) {
            return;
        }
        //todo

        //登录
        if (protocolId == 1) {
            //todo 获取用户信息
            long userId = 123456789L;
            String token = "";
            clientchannelManage.put(channel, userId);
            //本地
            Object msgObject = parse.invoke(null, body);
            MsgResponse message = route(ctx, msgObject, protocolId, userId);
            GeneratedMessage.Builder<?> responseBody = message.getBody();
            byte[] bodyByteArr = responseBody.buildPartial().toByteArray();

            //写回
            ByteBuf out = buildClientMsg(msg.getCid(), message.getErrorCode(), msg.getProtocolId(), 0, 1, bodyByteArr);
            ChannelFuture channelFuture = ctx.writeAndFlush(out);
            channelFuture.addListener(future -> {
                if (!future.isSuccess()) {
                    System.err.println("Write and flush failed: " + future.cause());
                }
            });
        } else {//转发
            long userId = 123456789L;
            // todo 根据用户信息选择目标服务器
            ServerConfig serverConfig = getTargetServerAddress(protocolId);
            // 转发到目标服务器
            forwardToTargetServer(ctx, msg, userId, serverConfig);
        }
    }


    //todo 获取主机
    private ServerConfig getTargetServerAddress(int protocolId) {
        // 简单逻辑：根据用户信息选择目标服务器
        if (protocolId > 1000 && protocolId < 2000) {
            return new ServerConfig(1, "127.0.0.1", 8081);
        } else if (protocolId >= 2000 && protocolId < 3000) {
            return new ServerConfig(1, "127.0.0.1", 8082);
        }
        return new ServerConfig(1, "127.0.0.1", 9999);
    }

    /**
     * 转发到目标服务器
     *
     * @param clientChannel 客户端-网关
     * @param msg           消息
     */
    private void forwardToTargetServer(ChannelHandlerContext clientChannel, ByteBufferMessage msg, long userId, ServerConfig serverConfig) {

        Channel channel = serverChannelManage.getChanelByIp(serverConfig.getServerId());
        if (channel == null) {
            try {
                ChannelFuture future = bootstrap.connect(serverConfig.getIp(), serverConfig.getPort()).sync();
                if (future.isSuccess()) {
                    channel = future.channel();
                    serverChannelManage.put(serverConfig.getServerId(), channel, serverConfig);
                }
            } catch (Exception e) {
//                log.error("Failed to create channel for {}", address, e);
                return;
            }
        }

        //进行转发到目标服务器
        if (channel != null && channel.isActive()) {
            ByteBuf out = buildServerMsg(userId, msg.getCid(), msg.getErrorCode(), msg.getProtocolId(), 0, 1, msg.getBody());
            ChannelFuture channelFuture = channel.writeAndFlush(out);
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    // 消息转发成功的处理
                    System.out.println("Successfully forwarded message");
                } else {
                    // 消息转发失败的处理
                    // log.error("Failed to forward message to {}", targetServerAddress, future.cause());
                    serverChannelManage.removeChanelByIp(serverConfig.getServerId());
                    //直接告诉客户端，返回错误码 todo
                    // 错误码 10，todo
                    ByteBuf outClient = buildClientMsg(msg.getCid(), 10, msg.getProtocolId(), 0, 1, null);
                    clientChannel.writeAndFlush(outClient);
                }
            });
        } else {
            //log.error("No active channel for {}", targetServerAddress);
            //todo 直接告诉客户端，返回错误码
            ByteBuf outClient = buildClientMsg(msg.getCid(), 10, msg.getProtocolId(), 0, 1, null);
            clientChannel.writeAndFlush(outClient);
        }
    }


    //客户端端处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof InvocationTargetException) {
            //目标方法错误
        } else if (cause instanceof SocketException
                || cause instanceof DecoderException) {
            //客户端关闭连接/连接错误
            // 关闭连接
            //断开无效连接
            destroyConnection(ctx);
            ctx.close();
        }
    }


    /**
     * 路由分发
     */
    public MsgResponse route(ChannelHandlerContext ctx, Object message, int protocolId, long userId) throws Exception {
        Class<?> handleClazz = postProcessor.getHandleMap(protocolId);
        if (handleClazz == null) {
            return null;
        }
        Method method = postProcessor.getMethodMap(protocolId);
        if (method == null) {
            return null;
        }
        method.setAccessible(true);
        Object bean = BeanTool.getBean(handleClazz);
        if (bean == null) {
            return null;
        }
        Object invoke = method.invoke(bean, ctx, message, userId);
        if (invoke instanceof MsgResponse) {
            return (MsgResponse) invoke;
        } else {
            return null;
        }
    }

    /**
     * 客户端消息
     */
    public ByteBuf buildClientMsg(int cid, int errorCode, int protocolId, int zip, int version, byte[] bodyArray) {
        int length = bodyArray.length;
        //写回
        ByteBuf out = Unpooled.buffer(16 + length);
        //消息头
        out.writeInt(cid);      // 4字节
        out.writeInt(errorCode);      // 4字节
        out.writeInt(protocolId);      // 4字节
        out.writeByte(zip);                       // zip压缩标志，1字节
        out.writeByte(version);                       // pb版本，1字节
        //消息体
        out.writeShort(bodyArray.length);                 // 消息体长度，2字节
        // 写入消息体
        out.writeBytes(bodyArray);
        return out;
    }

    /**
     * 服务器信息
     */
    public ByteBuf buildServerMsg(long userId, int cid, int errorCode, int protocolId, int zip, int version, byte[] bodyArray) {
        int length = bodyArray.length;
        //写回
        ByteBuf out = Unpooled.buffer(24 + length);
        //消息头
        out.writeLong(userId);      // 8字节
        out.writeInt(cid);      // 4字节
        out.writeInt(errorCode);      // 4字节
        out.writeInt(protocolId);      // 4字节
        out.writeByte(zip);                       // zip压缩标志，1字节
        out.writeByte(version);                       // pb版本，1字节
        //消息体
        out.writeShort(bodyArray.length);                 // 消息体长度，2字节
        // 写入消息体
        out.writeBytes(bodyArray);
        return out;
    }

    public void destroyConnection(ChannelHandlerContext ctx) {
        //断开无效连接
        clientchannelManage.remove(ctx.channel());
    }

}
