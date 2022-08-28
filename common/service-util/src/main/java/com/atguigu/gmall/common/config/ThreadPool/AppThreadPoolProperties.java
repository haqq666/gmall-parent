package com.atguigu.gmall.common.config.ThreadPool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/28 23:44
 */

@ConfigurationProperties("app.thread-pool")
@Component
@Data
public class AppThreadPoolProperties {
    Integer core = 4;
    Integer max = 8;
    Integer queueSize = 2000;
    Long keepAliveTime = 300l;
}
