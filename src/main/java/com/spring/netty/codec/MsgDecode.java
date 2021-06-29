package com.spring.netty.codec;

import com.spring.netty.constant.NettyConstant;
import com.spring.netty.entity.Body;
import com.spring.netty.entity.Head;
import com.spring.netty.entity.Message;
import com.spring.netty.utils.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "decode")
public class MsgDecode extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        Logger log = LoggerFactory.getLogger(this.getClass());
        log.info("解码");
        Integer length = byteBuf.readInt();
        Integer crcCode = byteBuf.readInt();
        Long sessionId = byteBuf.readLong();
        Byte type = byteBuf.readByte();
        Byte priority = byteBuf.readByte();

        Map<String,Object> map = new HashMap<>();
        Integer temp = NettyConstant.HEAD_LENGTH;
        if(byteBuf.isReadable()){
            Short size = byteBuf.readShort();
            temp+=2;
            while(map.size()<size){
                int keyLen = byteBuf.readInt();
                byte[] key = new byte[keyLen];
                byteBuf.readBytes(key,0,keyLen);
                int valueLen = byteBuf.readInt();
                byte[] value = new byte[valueLen];
                byteBuf.readBytes(value,0,valueLen);
                map.put(new String(key, CharsetUtil.UTF_8), ByteUtil.byteToObject(value));
                temp=temp+keyLen+valueLen;
            }

        }
        Object payload = null;
        if (byteBuf.isReadable()) {
            int bodyLen =length - temp;
            byte[] body = new byte[bodyLen];
            byteBuf.readBytes(body);
            payload = ByteUtil.byteToObject(body);
        }
        Head head = Head.builder().length(length).crcCode(crcCode).attachment(map).priority(priority)
                .sessionId(sessionId).type(type).build();
        Body body = new Body(payload);
        Message message = Message.builder().head(head).body(body).build();
        log.info("解码结束！message={}",message.toString());
        out.add(message);
    }
}


