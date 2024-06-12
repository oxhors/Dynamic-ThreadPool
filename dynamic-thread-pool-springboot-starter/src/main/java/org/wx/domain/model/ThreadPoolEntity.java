package org.wx.domain.model;

//封装线程池信息的实体对象
public class ThreadPoolEntity {
    private String appName;

    private String threadPoolName;

    private Integer corePoolSize;

    private Integer maxPoolSize;

    private Integer activeCnt;

    private Integer poolSize;

    private String queueType;

    private Integer queueSize;

    private Integer remainingCapacity;

    public ThreadPoolEntity(String appName, String threadPoolName, Integer corePoolSize, Integer maxPoolSize, Integer activeCnt, Integer poolSize, String queueType, Integer queueSize, Integer remainingCapacity) {
        this.appName = appName;
        this.threadPoolName = threadPoolName;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.activeCnt = activeCnt;
        this.poolSize = poolSize;
        this.queueType = queueType;
        this.queueSize = queueSize;
        this.remainingCapacity = remainingCapacity;
    }

    public ThreadPoolEntity(String appName, String threadPoolName) {
        this.appName = appName;
        this.threadPoolName = threadPoolName;
    }

    public ThreadPoolEntity() {
    }


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getThreadPoolName() {
        return threadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getActiveCnt() {
        return activeCnt;
    }

    public void setActiveCnt(Integer activeCnt) {
        this.activeCnt = activeCnt;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    public Integer getRemainingCapacity() {
        return remainingCapacity;
    }

    public void setRemainingCapacity(Integer remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }
    @Override
    public String toString() {
        return "ThreadPoolEntity{" +
                "appName='" + appName + '\'' +
                ", threadPoolName='" + threadPoolName + '\'' +
                ", corePoolSize=" + corePoolSize +
                ", maxPoolSize=" + maxPoolSize +
                ", activeCnt=" + activeCnt +
                ", poolSize=" + poolSize +
                ", queueType='" + queueType + '\'' +
                ", queueSize=" + queueSize +
                ", remainingCapacity=" + remainingCapacity +
                '}';
    }

}
