package com.atguigu.gmall.item.controller;


import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/29 0:09
 */
@RestController
public class ThreadPoolController {

    @Autowired
    ThreadPoolExecutor executor;

    @GetMapping("threadPool/close")
    public Result closeThreadPool(){
        executor.shutdown();
        return Result.ok();
    }

    @GetMapping("monitor")
    public Result monitor(){
        int corePoolSize = executor.getCorePoolSize();
        long taskCount = executor.getTaskCount();
        return Result.ok(corePoolSize + "=====" + taskCount);
    }

}
