package org.wx.registry.redis;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.wx.domain.model.ThreadPoolConfigEntity;
import org.wx.domain.valobj.RegistryEnumVO;
import org.wx.registry.IRegistry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisRegistry implements IRegistry {

    private final RedissonClient redissonClient;

    public RedisRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void reportThreadPoolEntities(List<ThreadPoolConfigEntity> threadPoolEntities) {
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        list.delete();
        list.addAll(threadPoolEntities);
    }

    @Override
    public void reportThreadPoolConfigEntity(ThreadPoolConfigEntity threadPoolConfigEntity) {
        String cacheKey = RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + threadPoolConfigEntity.getAppName() + "_" + threadPoolConfigEntity.getThreadPoolName();
        RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket(cacheKey);
        bucket.set(threadPoolConfigEntity, 30, TimeUnit.DAYS);
    }
}
