package com.spring.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ResponseBuz extends ChannelInboundHandlerAdapter {
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.add(channel);
        channelGroup.writeAndFlush("客户["+channel.remoteAddress()+"]加入聊天");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("客户["+channel.remoteAddress()+"]离开了聊天");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println(ctx.channel().remoteAddress()+"上线了");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] b = new byte[buf.readableBytes()];
        buf.readBytes(b);
        String str = new String(b);
        Channel channel = ctx.channel();
        System.out.println("接收到客户端【"+ channel.remoteAddress()+"】发送过来的消息："+buf.toString(CharsetUtil.UTF_8));
        channelGroup.forEach(ch->{
            if(channel!=ch){
                ch.writeAndFlush(Unpooled.copiedBuffer(("[" + channel.remoteAddress() + "发送了消息：]" + str+"\n").getBytes()) );
            }else{
                ch.writeAndFlush(Unpooled.copiedBuffer(("【自己】发送的消息："+str).getBytes()));
            }
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"离线了");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
        ctx.flush();
    }
}