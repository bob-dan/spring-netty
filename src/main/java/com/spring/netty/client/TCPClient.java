package com.spring.netty.client;
import com.spring.netty.codec.MsgDecode;
import com.spring.netty.codec.MsgEncode;
import com.spring.netty.handler.HeartBeatReqHandler;
import com.spring.netty.handler.LoginReqHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TCPClient implements CommandLineRunner {
    Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${netty.host}")
    private String host;
    @Value("${netty.port}")
    private Integer port;

    @Override
    public void run(String... args){
        NioEventLoopGroup client = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try{
            bootstrap.group(client).channel(NioSocketChannel.class).localAddress("127.0.0.1",8800)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new MsgDecode())
                                    .addLast(new MsgEncode())
                                    .addLast(new HeartBeatReqHandler())
                                    .addLast(new LoginReqHandler());
                        }
                    });
            ChannelFuture f = bootstrap.connect(host,port).sync();
            if(f.isSuccess()){
                log.info("客户端连接主机:{}，ip:{}成功！",this.host,this.port);
            }
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            client.shutdownGracefully();
        }
    }

}