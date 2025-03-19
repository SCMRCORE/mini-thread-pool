# mini-thread-pool

手写一个简单的mini线程池

**blog链接**：[🥽 实现简单线程池 | Blog](https://scmrcore.github.io/Notes/CodeMemoirs/手写线程池.html)

**测试使用**：

```java
MyThreadPool myThreadPool = new MyThreadPool(corePoolSize, maxPoolSize, timeout, timeUnit, BlockingQueue, RejectHandler;

myThreadPool.execute(()->{
    your task
});
```

**遗憾**：未能实现execute原子性，未能实现shutdown，未能实现ThreadFactory

- 涉及知识点：exception，LockSupport，阻塞队列，线程池
- 文件结构：

```powershell
thread-pool/
├── src/
│   ├── main/
│     ├── java/
│     │   └── com.example.threadpool/
│     │       ├── MyThreadPool.java
│     │       ├── RejectHandle.java
│     │       ├── ThreadPoolApplication.java
│     │       └── ThrowRejectHandler.java
│     └── resources/
│         └── application.yml
├── pom.xml
```

- 线程池MyThreadPool结构分析：

```powershell
MyThreadPool/
├── 线程池核心参数/构造方法
│	//corePoolSize, maxSize, timeOut, timeUnit, blockingQueue, rejectHandle
│
├── task容器(List)
│	//coreList存储核心线程，supportList存储辅助线程
│
├── execute方法
│	//生成对应task的线程，涉及到corePoolSize和maxSize判断以及Reject选择
│
└── 两个task对应class线程的run()
	//核心线程task直接从blockingQueue取出，辅助线程task通过poll实现自动释放
```

- 执行结果：基本符合

![image-20250318175035806](README.assets/image-20250318175035806.png)