package com.spring.netty.handler;

import com.spring.netty.entity.Message;
import com.spring.netty.enums.MsgTypeEnum;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class MessageHandler extends ChannelInboundHandlerAdapter {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private String writeList="192.168.25.92:8800,/127.0.0.1:8800";
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message)msg;
        System.out.println(msg.toString());
        if(message!=null && message.getHead().getType()== MsgTypeEnum.HANDSHAKE_REQUEST.getType().byteValue()){
            Channel channel = ctx.channel();
            String remoteIp = channel.remoteAddress().toString();
            InetSocketAddress addr = (InetSocketAddress) channel.remoteAddress();
            System.out.println(addr.toString());
            System.out.println(remoteIp+"\t"+writeList.contains(remoteIp));
            if(writeList.contains(remoteIp)){
                message.getHead().setType(MsgTypeEnum.HANDSHAKE_RESPONSE.getType().byteValue());
                log.info("message 发送出去~~~~~~{}",message.toString());
                ctx.writeAndFlush(message);
            }
        }else{
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
        Channel channel = ctx.channel();
        if(channel.isActive()){
            ctx.close();
        }
    }
}