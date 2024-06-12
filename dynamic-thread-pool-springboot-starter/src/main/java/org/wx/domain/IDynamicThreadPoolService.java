package org.wx.domain;

import org.wx.domain.model.ThreadPoolConfigEntity;

import java.util.List;

public interface IDynamicThreadPoolService {


    List<ThreadPoolConfigEntity> queryThreadPoolList();

    //在每个应用内通过线程池的名称查询配置
    ThreadPoolConfigEntity queryThreadPoolByName(String threadPoolName);

    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolEntity);
}
