package org.wx.trigger;

import org.redisson.api.listener.MessageListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.wx.domain.IDynamicThreadPoolService;
import org.wx.domain.model.ThreadPoolConfigEntity;
import org.wx.registry.IRegistry;

public class ThreadPoolAdjustListener implements MessageListener<ThreadPoolConfigEntity> {

    //复杂提供修改线程池的方法
    private final IDynamicThreadPoolService dynamicThreadPoolService;
    private final IRegistry registry;

    public ThreadPoolAdjustListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.registry = registry;
    }


    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        dynamicThreadPoolService.updateThreadPoolConfig(threadPoolConfigEntity);

        registry.reportThreadPoolEntities(dynamicThreadPoolService.queryThreadPoolList());

        registry.reportThreadPoolConfigEntity(threadPoolConfigEntity);
    }
}
