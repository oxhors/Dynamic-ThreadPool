package org.wx.registry;

import org.wx.domain.model.ThreadPoolConfigEntity;
import java.util.List;

public interface IRegistry {

    //上报当前应用的线程池状态信息，多个线程池用列表接受
    void reportThreadPoolEntities(List<ThreadPoolConfigEntity> threadPoolEntities);


    //上报当前应用的线程池配置参数
    void reportThreadPoolConfigEntity(ThreadPoolConfigEntity threadPoolConfigEntity);
}
