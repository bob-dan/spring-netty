package com.spring.netty.handler;

import com.spring.netty.constant.NettyConstant;
import com.spring.netty.entity.Head;
import com.spring.netty.entity.Message;
import com.spring.netty.enums.MsgTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        Message msg = (Message) obj;
        System.out.println("heartbeatReqhandler receive msg:"+msg.toString());
        if(msg!=null && msg.getHead().getType()== MsgTypeEnum.HANDSHAKE_RESPONSE.getType().byteValue()){
            ctx.executor().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Head head = Head.builder().sessionId(NettyConstant.LOGIN_SESSION_ID).priority(NettyConstant.PRIORITY)
                            .crcCode(NettyConstant.CRC_CODE).type(MsgTypeEnum.HEARTBEAT_REQUEST.getType().byteValue())
                            .length(NettyConstant.HEAD_LENGTH).build();
                    Message message = Message.builder().head(head).build();
                    System.out.println("clent send heartbeat***");
                    ctx.channel().writeAndFlush(message);
                }
            },0,50, TimeUnit.SECONDS);
        }else if(msg!=null && msg.getHead().getType()== MsgTypeEnum.HEARTBEAT_RESPONSE.getType().byteValue()){
            System.out.println("client receive heartbeat msg:"+msg.toString());
        }else{
            ctx.fireChannelRead(obj);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
        ctx.close();
    }
}