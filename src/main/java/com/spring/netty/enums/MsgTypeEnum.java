package com.spring.netty.enums;


public enum MsgTypeEnum {

    BUZ_REQUEST(0,"业务请求"),
    BUZ_RESPONSE(1,"业务相应"),
    BUZ_ONEWAY(2,"即是请求也是响应"),
    HANDSHAKE_REQUEST(3,"握手请求"),
    HANDSHAKE_RESPONSE(4,"握手响应"),
    HEARTBEAT_REQUEST(5,"心跳请求"),
    HEARTBEAT_RESPONSE(6,"心跳响应"),
    ;
    private Integer type;
    private String name;
    MsgTypeEnum(Integer type,String name){
        this.name = name;
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
    public String getName(){
        return name;
    }
}