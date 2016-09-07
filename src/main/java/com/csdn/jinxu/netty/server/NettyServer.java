package com.csdn.jinxu.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 实现描述：netty实战server
 *
 * @author jin.xu
 * @version v1.0.0
 * @see
 * @since 16-7-20 下午3:18
 */
public class NettyServer {
    public void bind(int port) throws Exception {
        /**服务器线程组 用于网络事件的处理 一个用于服务器接收客户端的连接*/
        /** 另一个线程组用于处理SocketChannel的网络读写*/
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /** NIO服务器端的辅助启动类 降低服务器开发难度*/
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)// 配置TCP参数
                    .childHandler(new ChildChannelHandler());// 最后绑定I/O事件的处理类

            /** 服务器启动后 绑定监听端口 同步等待成功 主要用于异步操作的通知回调 回调处理用的ChildChannelHandler*/
            ChannelFuture f = serverBootstrap.bind(port).sync();
            System.out.println("timeServer启动");
            /** 等待服务端监听端口关闭*/
            f.channel().closeFuture().sync();

        } finally {
            /** 优雅退出 释放线程池资源*/
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("服务器优雅的释放了线程资源...");
        }

    }

    /**
     * 网络事件处理器
     */
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new TimeServerHandler());
        }

    }

    public static void main(String[] args) throws Exception {
        int port = 8000;
        new NettyServer().bind(port);
    }
}
