package com.example.threadpool;

import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {

    private int corePoolSize;
    private int maxSize;
    private int timeout;
    private TimeUnit timeUnit;
    //阻塞队列，避免其他容器等待时疯狂消耗CPU资源
    BlockingQueue<Runnable> blockingQueue;//用户传入时需要指定容量
    private final RejectHandle rejectHandle;

    public MyThreadPool(int corePoolSize, int maxSize, int timeout, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue, RejectHandle rejectHandle) {
        this.corePoolSize = corePoolSize;
        this.maxSize = maxSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.blockingQueue = blockingQueue;//需要指定容量
        this.rejectHandle=rejectHandle;
    }

    //省略get方法

    //用容器存储线程
    List<Thread> coreList = new ArrayList<>();
    //核心线程已满，需要辅助线程来帮我们处理任务
    List<Thread> supportList = new ArrayList<>();//maxSize-corePoolSize

    void execute(Runnable command) {
        //判断ThreadList有多少元素，没到corePoolSize就创建线程
        if(coreList.size()<corePoolSize){
            Thread thread = new CoreThread();
            coreList.add(thread);
            thread.start();
        }
        //offer区别于add会有个返回值，我们会用到这个值来判断
        if(blockingQueue.offer(command)) return;

        if(coreList.size() + supportList.size() < maxSize){
            Thread thread =new SupportThread();
            supportList.add(thread);
            thread.start();
        }
        //在多线程环境下，因为不是原子即使刚创建，也可能满
        if(!blockingQueue.offer(command)){
            rejectHandle.reject(command, this);
        }
    }

    //将线程里的运行逻辑提取成一个共用task,并且可以将两个task封装为类方便调用
    class CoreThread extends Thread{
        @Override
        public void run() {
            while(true) {
                try {
                    Runnable command = blockingQueue.take();
                    command.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    class SupportThread extends Thread{
        @Override
        public void run() {
            while(true) {
                try {
                    Runnable command = blockingQueue.poll(timeout, timeUnit);
                    if(command==null) {
                        break;//如果没null说明空闲，break后会自动推出循环，线程结束
                    }
                    command.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(Thread.currentThread().getName()+"线程结束了!");
        }
    }
}
