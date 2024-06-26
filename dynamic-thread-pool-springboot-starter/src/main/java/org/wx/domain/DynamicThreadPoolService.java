package org.wx.domain;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wx.domain.model.ThreadPoolConfigEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public class DynamicThreadPoolService implements IDynamicThreadPoolService{

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolService.class);
    private final String appName;

    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;

    public DynamicThreadPoolService(String appName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.appName = appName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    @Override
    public List<ThreadPoolConfigEntity> queryThreadPoolList() {
        List<ThreadPoolConfigEntity> res = new ArrayList<>();
        //Set<String> threadPoolName = threadPoolExecutorMap.keySet();
        for (Map.Entry<String, ThreadPoolExecutor> threadPoolExecutorEntry : threadPoolExecutorMap.entrySet()) {
            ThreadPoolConfigEntity threadPoolEntity = new ThreadPoolConfigEntity(appName, threadPoolExecutorEntry.getKey());
            ThreadPoolExecutor threadPool = threadPoolExecutorEntry.getValue();

            getThreadPoolEntityFromThreadPool(threadPoolEntity, threadPool);
            logger.info("名为{}的线程池的配置信息为:{}",threadPoolExecutorEntry.getKey(), JSON.toJSONString(threadPoolEntity));
            res.add(threadPoolEntity);
        }
        return res;
    }

    @Override
    public ThreadPoolConfigEntity queryThreadPoolByName(String threadPoolName) {
        if(Strings.isBlank(threadPoolName)){
            logger.info("线程池名称不能为空!");
            return null;
        }
//        Map.Entry<String, ThreadPoolExecutor> target = threadPoolExecutorMap.entrySet().stream().filter(entry -> {
//            return entry.getKey().equals(threadPoolName);
//        }).findAny().orElse(null);

        ThreadPoolExecutor target = threadPoolExecutorMap.get(threadPoolName);
        if(target == null){
            logger.info("不存在名为{}的线程池",threadPoolName);
            return null;
        }
        ThreadPoolConfigEntity threadPoolEntity = new ThreadPoolConfigEntity(appName, threadPoolName);
        getThreadPoolEntityFromThreadPool(threadPoolEntity, target);
        logger.info("名为{}的线程池的配置信息为:{}",threadPoolName, JSON.toJSONString(threadPoolEntity));
        return threadPoolEntity;
    }

    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {
        if(threadPoolConfigEntity == null || !appName.equals(threadPoolConfigEntity.getAppName())) return ;
        ThreadPoolExecutor poolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());
        if(poolExecutor == null) return;

        //设置核心线程数，最大线程数..TODO
        poolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        poolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());


    }

    private void getThreadPoolEntityFromThreadPool(ThreadPoolConfigEntity threadPoolEntity, ThreadPoolExecutor threadPool) {
        threadPoolEntity.setPoolSize(threadPool.getPoolSize());
        threadPoolEntity.setCorePoolSize(threadPool.getCorePoolSize());
        threadPoolEntity.setMaximumPoolSize(threadPool.getMaximumPoolSize());
        threadPoolEntity.setActiveCount(threadPool.getActiveCount());
        threadPoolEntity.setQueueSize(threadPool.getQueue().size());
        threadPoolEntity.setQueueType(threadPool.getQueue().getClass().getSimpleName());
        threadPoolEntity.setRemainingCapacity(threadPool.getQueue().remainingCapacity());
    }
}
