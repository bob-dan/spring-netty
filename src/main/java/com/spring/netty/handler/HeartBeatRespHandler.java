package com.spring.netty.handler;

import com.spring.netty.constant.NettyConstant;
import com.spring.netty.entity.Head;
import com.spring.netty.entity.Message;
import com.spring.netty.enums.MsgTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        ChannelPipeline pipeline = ctx.channel().pipeline();
        ChannelPipeline pipeline1 = ctx.pipeline();
        System.out.println("pipeline is equal"+(pipeline==pipeline1)+"   "+(ctx.channel()==ctx.pipeline().channel()));


        Message msg = (Message)obj;
        System.out.println("HeartBeatRespHandler receive msg:"+msg.toString());
        if(msg!=null && msg.getHead().getType()== MsgTypeEnum.HEARTBEAT_REQUEST.getType().byteValue()){
            Head head = Head.builder().sessionId(NettyConstant.LOGIN_SESSION_ID).priority(NettyConstant.PRIORITY)
                    .crcCode(NettyConstant.CRC_CODE).type(MsgTypeEnum.HEARTBEAT_RESPONSE.getType().byteValue())
                    .length(NettyConstant.HEAD_LENGTH).build();
            msg.setHead(head);
            ctx.channel().writeAndFlush(msg);
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
