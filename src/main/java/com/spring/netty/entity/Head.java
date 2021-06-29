package com.spring.netty.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Head implements Serializable {
    private Integer crcCode;
    private Integer length;
    private Long sessionId;
    private Byte type;
    private Byte priority;
    private Map<String,Object> attachment;

}