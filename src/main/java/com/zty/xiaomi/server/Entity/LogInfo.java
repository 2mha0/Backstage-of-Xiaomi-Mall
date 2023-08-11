package com.zty.xiaomi.server.Entity;

import lombok.Data;

@Data
public class LogInfo {
    private String requestId;
    private String url;
    private String ip;
    private String reqParam;
    private Long timestamp;
    private Object others;
}
