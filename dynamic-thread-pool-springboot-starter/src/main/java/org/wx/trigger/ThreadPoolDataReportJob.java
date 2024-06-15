package org.wx.trigger;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wx.TheadPool.wxThreadPool;
import org.wx.domain.IDynamicThreadPoolService;
import org.wx.domain.model.ThreadPoolConfigEntity;
import org.wx.registry.IRegistry;

import java.util.List;
import java.util.concurrent.TimeUnit;

//上报线程池数据的任务
public class ThreadPoolDataReportJob {
    private final Logger logger = LoggerFactory.getLogger(ThreadPoolDataReportJob.class);

    private final IRegistry registry;

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private final wxThreadPool threadPool = new wxThreadPool();

    //dynamicThreadPoolService获取数据， registry上报数据
    public ThreadPoolDataReportJob(IRegistry registry, IDynamicThreadPoolService dynamicThreadPoolService) {
        this.registry = registry;
        this.dynamicThreadPoolService = dynamicThreadPoolService;
    }

    //@Scheduled(cron = "0/20 * * * * ?")

    public void exec(){
        //当前应用的所有线程池参数
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.queryThreadPoolList();
        registry.reportThreadPoolEntities(threadPoolConfigEntities);
        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntities) {
            //分别上报每一个线程池的配置信息
            registry.reportThreadPoolConfigEntity(threadPoolConfigEntity);
        }
    }

    private final Runnable task = this::exec;
    public void start(){
        threadPool.execute(task,0,20, TimeUnit.SECONDS);
    }
    public void stop(){
        threadPool.stop();
    }
}
