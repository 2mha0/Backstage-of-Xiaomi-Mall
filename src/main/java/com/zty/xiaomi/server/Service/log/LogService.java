package com.zty.xiaomi.server.Service.log;

import com.zty.xiaomi.server.Entity.LogInfo;
import com.zty.xiaomi.server.Entity.log.*;

import java.util.List;

/**
 * @pakage com.zty.xiaomi.server.Service.log
 * @auther 邮专第一深情
 * @Date 2023/8/11 10:01
 * @Descripetion
 */
public interface LogService {
    void addLog(Logs logs);

    void addLogUser(LogsUser logsUser);

    void addLogProduct(LogsGood logsGood);

    void addLogCart(LogsCart logsCart);

    List<LogInfo> getLogList();

    void addOrderLog(LogsOrder logsOrder);
}
