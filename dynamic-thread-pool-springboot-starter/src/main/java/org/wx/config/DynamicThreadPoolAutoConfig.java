package org.wx.config;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.util.Strings;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RTopic;
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
import org.wx.domain.model.ThreadPoolConfigEntity;
import org.wx.domain.valobj.RegistryEnumVO;
import org.wx.registry.IRegistry;
import org.wx.registry.redis.RedisRegistry;
import org.wx.trigger.ThreadPoolAdjustListener;
import org.wx.trigger.ThreadPoolDataReportJob;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 */
@Configuration
@EnableScheduling //启动定时任务完成数据上报
@EnableConfigurationProperties(DynamicThreadPoolAutoProperties.class)
public class DynamicThreadPoolAutoConfig {
    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

    private  String appName = null;
    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext, Map<String,ThreadPoolExecutor> threadPoolExecutorMap,RedissonClient redissonClient){
        appName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if(Strings.isBlank(appName)){
            logger.info("没有配置应用名称....");
            return new DynamicThreadPoolService("",threadPoolExecutorMap);
        }
        logger.info("线程池信息:{}", JSON.toJSONString(threadPoolExecutorMap.keySet()));
        Set<String> names = threadPoolExecutorMap.keySet();
        for (String name : names) {
            ThreadPoolConfigEntity threadPoolConfig = redissonClient.<ThreadPoolConfigEntity>getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + appName + "_" + name).get();
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(name);
            //启动时从缓存中读取配置信息
            if(threadPoolConfig != null){
                threadPoolExecutor.setCorePoolSize(threadPoolConfig.getCorePoolSize());
                threadPoolExecutor.setMaximumPoolSize(threadPoolConfig.getMaximumPoolSize());
            }
        }
        return new DynamicThreadPoolService(appName,threadPoolExecutorMap);
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

    @Bean(initMethod = "start",destroyMethod = "stop")
    public ThreadPoolDataReportJob threadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService , IRegistry registry){
        return new ThreadPoolDataReportJob(registry,dynamicThreadPoolService);
    }

    @Bean
    public IRegistry redisRegistry(RedissonClient redissonClient){
        return new RedisRegistry(redissonClient);
    }

    @Bean
    public ThreadPoolAdjustListener threadPoolAdjustListener(IDynamicThreadPoolService dynamicThreadPoolService , IRegistry registry){
        return new ThreadPoolAdjustListener(dynamicThreadPoolService,registry);
    }
    @Bean
    public RTopic theadPoolAdjustListener(RedissonClient redissonClient, ThreadPoolAdjustListener threadPoolAdjustListener){
        RTopic topic = redissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getKey() + "_" + appName);
        topic.addListener(ThreadPoolConfigEntity.class,threadPoolAdjustListener);
        return topic;
    }

}
