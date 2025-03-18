package com.example.threadpool;

public class ThrowRejectHandler implements RejectHandle{
    @Override
    public void reject(Runnable rejectCommand, MyThreadPool myThreadPool) {
        throw new RuntimeException("阻塞队列已满");
    }
}
