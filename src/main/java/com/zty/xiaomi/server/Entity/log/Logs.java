package com.zty.xiaomi.server.Entity.log;

import lombok.Data;



/**
 * @pakage com.zty.xiaomi.server.Entity.log
 * @auther 邮专第一深情
 * @Date 2023/8/13 15:39
 * @Descripetion
 */
@Data
public class Logs {

    private Integer id;
    private String url;
    private String ip;
    private Long timestamp;
    private Integer totalPrice;
    private Integer totalNum;

}
