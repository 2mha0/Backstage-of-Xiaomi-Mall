package com.zty.xiaomi.server.Service.log;

import com.zty.xiaomi.server.Entity.LogInfo;
import com.zty.xiaomi.server.Entity.log.*;
import com.zty.xiaomi.server.Mapper.Log;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @pakage com.zty.xiaomi.server.Service.log
 * @auther 邮专第一深情
 * @Date 2023/8/11 10:01
 * @Descripetion
 */
@Service
public class LogServiceImpl implements LogService{

    @Autowired
    private Log logMapper;

    @Override
    public void addLog(Logs logs) {
    if (logs == null) {
        throw new RuntimeException("logs对象为空");
    }
      logMapper.insert(logs);
    }

    @Override
    public void addLogUser(LogsUser logsUser) {
        logMapper.insertLogUser(logsUser);
    }

    @Override
    public void addLogProduct(LogsGood logsGood) {
        logMapper.insertLogProduct(logsGood);

    }

    @Override
    public void addLogCart(LogsCart logsCart) {
       logMapper.insertLogCart(logsCart);

    }

    @Override
    public List<LogInfo> getLogList() {
        List<LogInfo> logInfoList = logMapper.list();
        return logInfoList;
    }

    @Override
    public void addOrderLog(LogsOrder logsOrder) {
        logMapper.addOrderLog(logsOrder);
    }
}
