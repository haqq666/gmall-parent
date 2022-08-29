package com.atguigu.gmall.common.config.ThreadPool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/28 23:34
 */
@Configuration
@EnableConfigurationProperties(AppThreadPoolProperties.class)
public class ThreadPoolAutoConfiguration {

    @Resource
    AppThreadPoolProperties appThreadPoolProperties;
    @Value("${spring.application.name}")
    String applicationName;

    @Bean
    public ThreadPoolExecutor coreExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                appThreadPoolProperties.getCore(),
                appThreadPoolProperties.getMax(),
                appThreadPoolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(appThreadPoolProperties.getQueueSize()),
                new ThreadFactory() {
                    int i = 1;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName(applicationName + "-core-thread-[" + i++ + "]");
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }


}
