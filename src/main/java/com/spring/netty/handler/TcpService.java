package com.spring.netty.handler;

import com.spring.netty.codec.MsgDecode;
import com.spring.netty.codec.MsgEncode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TcpService implements CommandLineRunner {
    Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${netty.port}")
    private int port;
    @Override
    public void run(String... args){
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss,work).channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline()
                                    .addLast(new MsgDecode())
                                    .addLast(new MsgEncode())
                                    .addLast(new HeartBeatRespHandler())
                                    .addLast(new MessageHandler())
                                    .addLast(new ResponseBuz());
                        }
                    });
            log.info("netty 服务器监听端口：{}",port);
            try {
                ChannelFuture future = future = serverBootstrap.bind(port).sync();
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

}