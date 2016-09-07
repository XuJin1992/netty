package com.csdn.jinxu.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 实现描述：netty实战client
 *
 * @author jin.xu
 * @version v1.0.0
 * @see
 * @since 16-7-20 下午3:18
 */
public class NettyClient {
    public void connect(int port, String host) throws Exception {
        /**配置客户端NIO线程组*/
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            /**客户端辅助启动类 对客户端配置*/
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeClientHandler());
                }
            });
            /**异步链接服务器 同步等待链接成功*/
            ChannelFuture f = b.connect(host, port).sync();
            /**等待链接关闭*/
            f.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
            System.out.println("客户端优雅的释放了线程资源...");
        }

    }

    public static void main(String[] args) throws Exception {
        new NettyClient().connect(8000, "127.0.0.1");
    }

}
