package org.wx.config;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.util.Strings;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.wx.domain.DynamicThreadPoolService;
import org.wx.domain.IDynamicThreadPoolService;
import org.wx.registry.IRegistry;
import org.wx.registry.redis.RedisRegistry;
import org.wx.trigger.job.ThreadPoolDataReportJob;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 */
@Configuration
@EnableScheduling //启动定时任务完成数据上报
@EnableConfigurationProperties(DynamicThreadPoolAutoProperties.class)
public class DynamicThreadPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);
    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext, Map<String,ThreadPoolExecutor> threadPoolExecutorMap){
        String name = applicationContext.getEnvironment().getProperty("spring.application.name");
        if(Strings.isBlank(name)){
            logger.info("没有配置应用名称....");
            return new DynamicThreadPoolService("",threadPoolExecutorMap);
        }
        logger.info("线程池信息:{}", JSON.toJSONString(threadPoolExecutorMap.keySet()));
        return new DynamicThreadPoolService(name,threadPoolExecutorMap);
    }

    @Bean("redissonClient")
    public RedissonClient redissonClient(DynamicThreadPoolAutoProperties properties) {
        Config config = new Config();

        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive())
        ;

        RedissonClient redissonClient = Redisson.create(config);

        logger.info("动态线程池，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());

        return redissonClient;
    }

    @Bean
    public ThreadPoolDataReportJob threadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService , IRegistry registry){
        return new ThreadPoolDataReportJob(registry,dynamicThreadPoolService);
    }

    @Bean
    public IRegistry redisRegistry(RedissonClient redissonClient){
        return new RedisRegistry(redissonClient);
    }

}
