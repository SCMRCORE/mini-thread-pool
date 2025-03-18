package com.example.threadpool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ThreadPoolApplication {

    public static void main(String[] args) {
        MyThreadPool myThreadPool = new MyThreadPool(2, 4, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), new ThrowRejectHandler());
        for (int i = 0; i < 4; i++) {
            myThreadPool.execute(()->{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName());
            });
        }
        System.out.println("主线程没有被阻塞");
    }

}
//TODO 无法复现视频里的