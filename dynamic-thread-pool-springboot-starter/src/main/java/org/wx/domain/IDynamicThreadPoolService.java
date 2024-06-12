package org.wx.domain;

import org.wx.domain.model.ThreadPoolEntity;

import java.util.List;

public interface IDynamicThreadPoolService {


    List<ThreadPoolEntity> queryThreadPoolList();

    //在每个应用内通过线程池的名称查询配置
    ThreadPoolEntity queryThreadPoolByName(String threadPoolName);

    void updateThreadPoolConfig(ThreadPoolEntity threadPoolEntity);
}
