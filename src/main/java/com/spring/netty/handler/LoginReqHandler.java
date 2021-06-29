package com.spring.netty.handler;
import com.spring.netty.entity.Body;
import com.spring.netty.entity.Head;
import com.spring.netty.entity.Message;
import com.spring.netty.enums.MsgTypeEnum;
import com.spring.netty.utils.ByteUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

public class LoginReqHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Head head = Head.builder().priority((byte)1).sessionId(23l).crcCode(1).type(MsgTypeEnum.HANDSHAKE_REQUEST.getType().byteValue()).build();
        Map<String,Object> attachment = new HashMap<>();
        attachment.put("dev","2534");
        attachment.put("name","张三");
        head.setAttachment(attachment);
        Body body = new Body();
        body.setPayload("welcom to shenzhen");
        Message message = Message.builder().head(head).body(body).build();
        int len = ByteUtil.calculateLenth(message);
        message.getHead().setLength(len);
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message)msg;
        if(message!=null && message.getHead().getType()==MsgTypeEnum.HANDSHAKE_RESPONSE.getType().byteValue()){
            System.out.println("kkkkkkkkkkkkkkk");
        }else {
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}