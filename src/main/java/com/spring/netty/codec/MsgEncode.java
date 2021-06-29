package com.spring.netty.codec;


import com.spring.netty.entity.Body;
import com.spring.netty.entity.Message;
import com.spring.netty.utils.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service("encode")
public class MsgEncode extends MessageToByteEncoder<Message> {
    Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message msg, ByteBuf out){
        log.info("编码 msg={}",msg.toString());
        Integer length = msg.getHead().getLength();
        Integer crcCode = msg.getHead().getCrcCode();
        Long sessionId = msg.getHead().getSessionId();
        Byte type = msg.getHead().getType();
        Byte priority = msg.getHead().getPriority();
        out.writeInt(length);
        out.writeInt(crcCode);
        out.writeLong(sessionId);
        out.writeByte(type);
        out.writeByte(priority);
        Map<String, Object> attachment = msg.getHead().getAttachment();
        if(attachment!=null && !attachment.isEmpty()){
            out.writeShort(attachment.size());//用两个字节记录可扩展字段attachment的大小，short是16位，2个字节
            if(attachment.size()>0){
                Set<Map.Entry<String, Object>> entries = attachment.entrySet();
                for (Map.Entry<String, Object> map : entries){
                    String key = map.getKey();
                    out.writeInt(key.length());//用4个字节记录key长度
                    out.writeCharSequence(key, CharsetUtil.UTF_8);
                    Object obj = map.getValue();
                    byte[] v = ByteUtil.toByteArray(obj);
                    int vlen = v.length;
                    out.writeInt(vlen);
                    out.writeBytes(v);
                }
            }
        }
        Body body  = msg.getBody();
        if(body!=null){
            Object payload = msg.getBody().getPayload();
            if(payload!=null){
                byte[] load = ByteUtil.toByteArray(payload);
                out.writeBytes(load);
            }
        }
        log.info("编码调用结束");
    }
}