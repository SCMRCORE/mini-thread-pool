package com.example.threadpool;

public interface RejectHandle {
    //拒绝策略的话，我们肯定要任务信息和线程池的信息，所以传入这俩
    void reject(Runnable rejectCommand, MyThreadPool myThreadPool);
}
