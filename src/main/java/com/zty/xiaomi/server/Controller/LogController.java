package com.zty.xiaomi.server.Controller;

import com.zty.xiaomi.server.Entity.LogInfo;
import com.zty.xiaomi.server.Service.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @pakage com.zty.xiaomi.server.Controller
 * @auther 邮专第一深情
 * @Date 2023/8/11 14:07
 * @Descripetion
 */
@RequestMapping("log")
@RestController
public class LogController {

    @Autowired
    private LogService logService;
    @GetMapping("/list")
    public List<LogInfo> queryLogList() {
        List<LogInfo> logInfoList = logService.getLogList();
        if (logInfoList == null) {
            throw new RuntimeException("log日志不存在");
        }
        return logInfoList;
    }
}
