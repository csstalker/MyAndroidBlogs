[Markdown版本](https://github.com/baiqiantao/MyAndroidBlogs)
[我的GitHub](https://github.com/baiqiantao)


---------------


性能优化 UI 主线程 卡顿监测 Handler Looper Choreographer

[参考：BlockCanary原理](http://blog.zhaiyifan.cn/2016/01/16/BlockCanaryTransparentPerformanceMonitor/)  
[参考：鸿洋的博客](https://blog.csdn.net/lmj623565791/article/details/58626355)   

[AndroidPerformanceMonitor](https://github.com/markzhai/AndroidPerformanceMonitor)  
[Takt](https://github.com/wasabeef/Takt)  
[TinyDancer](https://github.com/friendlyrobotnyc/TinyDancer)  
[Cockroach](https://github.com/android-notes/Cockroach)  
***
目录
===
[TOC]

# BlockCanary
`BlockCanary`是一个Android平台的一个`轻量的，非侵入式的性能监控组件`，应用只需要实现一个抽象类，提供一些该组件需要的上下文环境，就可以在平时使用应用的时候检测`主线程`上的各种卡慢问题，并通过组件提供的各种信息分析出原因并进行修复。

取名为`BlockCanary`则是为了向 [LeakCanary](https://github.com/square/leakcanary) 致敬。

## 背景
在复杂的项目环境中，由于历史代码庞大，业务复杂，包含各种第三方库，偶尔再来个jni调用，所以在出现了卡顿的时候，我们`很难定位到底是哪里出现了问题`，即便知道是哪一个Activity/Fragment，也仍然需要进去里面一行一行看，动辄数千行的类再加上跳来跳去调来调去的，结果就是不了了之随它去了，实在不行了再优化吧。于是一拖再拖，最后可能压根就改不动了，客户端越来越卡。  

事实上，很多情况下`卡顿不是必现的`，它们可能与机型、环境、操作等有关，存在偶然性，即使发生了，再去查那如山般的logcat，也不一定能找到卡顿的原因，是我们自己的应用导致的还是其他应用抢占资源导致的？是哪些方法导致的？很难去回朔。有些机型自己修改了api导致的卡顿，还必须拿那台机器才能去调试找原因。

`BlockCanary`就是来解决这个问题的。告别打点和调试，哪里卡顿，一目了然。

## 特点
`BlockCanary`对主线程操作进行了完全透明的监控，并能输出有效的信息，帮助开发分析、定位到问题所在，迅速优化应用。其特点有：
- 非侵入式，简单的两行就打开监控，不需要到处打点，破坏代码优雅性。  
- 精准，输出的信息可以帮助定位到问题所在（精确到行），不需要像Logcat一样，慢慢去找。  

目前包括了核心监控输出文件，以及UI显示卡顿信息功能。

## 功能
BlockCanary会在发生卡顿的时候记录各种信息，输出到配置目录下的文件，并弹出消息栏通知。  
此项目提供了一个友好的展示界面，供开发测试直接查看卡慢信息（基于LeakCanary的界面修改）。

- 开发可以通过图形展示界面直接看信息，然后进行修复
- 测试可以把log丢给开发，也可以通过卡慢详情页右上角的更多按钮，分享到各种聊天软件
- Monkey生成一堆的log，找个专人慢慢过滤记录下重要的卡慢吧
- 还可以通过Release包`在用户端定时开启监控并上报log`，后台匹配堆栈过滤同类原因，提供给开发更大的样本环境来优化应用。

dump的信息包括：
- 基本信息：安装包标示、机型、api等级、uid、CPU内核数、进程名、`内存`、版本号等
- 耗时信息：`实际耗时`、`主线程时钟耗时`、卡顿开始时间和结束时间
- CPU信息：时间段内CPU是否忙，时间段内的系统CPU/应用CPU占比，I/O占CPU使用率
- 堆栈信息：发生卡慢前的`最近堆栈`，可以用来帮助定位卡慢发生的地方和重现路径

> PS: 由于该库使用了 `getMainLooper().setMessageLogging()`, 请确认是否与你的app冲突。

## 使用案例
引入依赖 
```gradle
implementation 'com.github.markzhai:blockcanary-android:1.5.0'
```

如果仅在debug包启用BlockCanary进行卡顿监控和提示的话，可以这么用：
```gradle
debugImplementation 'com.github.markzhai:blockcanary-android:1.5.0'
releaseImplementation 'com.github.markzhai:blockcanary-no-op:1.5.0' //空包，为了release打包时不编译进去
```
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.markzhai/blockcanary-android/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.markzhai/blockcanary-android)  

在Application中初始化：
```java
BlockCanary.install(this, new MyBlockCanaryContext()).start();//在主进程初始化调用
```

监控配置
```java
public class MyBlockCanaryContext extends BlockCanaryContext {
    @Override
    public int provideBlockThreshold() {
        return 500; //配置block阈值，在此持续时间内的dispatch被视为BLOCK。您可以根据设备的性能进行设置。单位为毫秒。
    }
    
    @Override
    public void onBlock(Context context, BlockInfo blockInfo) {
        //Block拦截器，开发者可以提供自己的动作
        //Toast.makeText(context, "UI阻塞了，耗时：" + blockInfo.timeCost, Toast.LENGTH_SHORT).show();
        Log.i("bqt", "【UI阻塞了】耗时：" + blockInfo.timeCost + "\n" + blockInfo.toString());
    }
    
    @Override
    public String providePath() {
        return "/blockcanary/"; //日志文件保存地址，每监测到一次卡顿都将在SD卡上保存一个单独的日志文件
    }
    
    @Override
    public boolean displayNotification() {
        return true; //设为true时，当监测到卡顿后会在通知栏提醒
    }
    
    @Override
    public boolean stopWhenDebugging() {
        return true; //是否在debug模式是关闭，如果关闭则返回true
    }
    
    @Override
    public int provideDumpInterval() {
        //线程 stack dump 间隔，当block发生时使用，BlockCanary将根据当前采样周期在主线程堆栈上dump。
        return provideBlockThreshold(); //因为Looper的实现机制，实际dump间隔会比这里指定的长，特别是当cpu比较繁忙时。
    }
    
    @Override
    public int provideMonitorDuration() {
        return -1; //配置Monitor持续的时间，过了这个时间以后BlockCanary就会停止，单位是小时，-1表示不会停止
    }
    
    @Override
    public boolean zip(File[] src, File dest) {
        return false; //参数为压缩前的文件列表和压缩后的文件，返回 true 代表压缩成功
    }
    
    @Override
    public void upload(File zippedFile) {
        throw new UnsupportedOperationException(); //用以将压缩后的日志文件上传到服务器，zip返回true才会调用。
    }
    
    @Override
    public List<String> concernPackages() {
        return null; //开发者所关心的包，默认情况下使用进程名称，如果只关心 package with process name，则返回null。
    }
    
    @Override
    public boolean filterNonConcernStack() {
        return false; //过滤掉那些没有任何包存在于 concernPackages() 方法所指定的包中的 stack。如果开启过滤返回true。
    }
    
    @Override
    public List<String> provideWhiteList() {
        LinkedList<String> whiteList = new LinkedList<>();
        whiteList.add("org.chromium");
        return whiteList; //提供白名单，白名单中的条目不会显示在ui列表中。如果您不需要白名单过滤器，则返回null。
        //注意，经过测试发现此库有一个bug，如果这里返回null，当点击通知栏或点击桌面上的"Blocks"时，应用就会崩溃！
    }
    
    @Override
    public boolean deleteFilesInWhiteList() {
        return true; //与白名单一起使用，是否删除那些其stack存在于白名单中的文件。如果删除则返回true。
    }
    
    @Override
    public String provideQualifier() {
        return "unknown"; //指定限定符，例如version + flavor
    }
    
    @Override
    public String provideUid() {
        return "uid";
    }
    
    @Override
    public String provideNetworkType() {
        return "unknown"; //2G, 3G, 4G, wifi, etc.
    }
}
```

## 卡顿分析
在APP中模拟一个耗时操作，例如：
```java
findViewById(R.id.tv).setOnClickListener(v -> SystemClock.sleep(500));
```

点击后，会有通知栏提醒，点击通知栏消息后会显示详细信息(和`LeakCanary`的使用体验完全一致)：  
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-2018100223575337.png)  
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181003000011684.png)  

通过APP的 `Share info` 或保存到 `SD` 卡上的日志文件，或通过 `Logcat` 日志，我们都可以拷贝详细信息：
```java
qua = unknown
versionName = 1.0
versionCode = 1
imei = 866947026070354
uid = uid
network = unknown
model = PLK-UL00
api-level = 23 6.0
cpu-core = 8
process = com.bqt.activity_lock
freeMemory = 1609364
totalMemory = 2858156
time = 502
thread-time = 1
time-start = 10-02 23:57:09.106
time-end = 10-02 23:57:09.608
cpu-busy = false
cpu-rate = 
stack = 10-02 23:57:09.506

java.lang.Thread.sleep(Native Method)
java.lang.Thread.sleep(Thread.java:1046)
java.lang.Thread.sleep(Thread.java:1000)
android.os.SystemClock.sleep(SystemClock.java:120)
com.bqt.lock.MainActivity.lambda$onCreate$0$MainActivity(MainActivity.java:31)
```

可以发现，我们代码中导致耗时的操作确实就是上面监测信息中提示的：  
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181003000400330.png)

除了图形界面可以供开发、测试阶段直接看卡顿原因外，更多的使用场景其实在于大范围的log采集和分析：如线上环境和monkey，或者测试同学们在整个测试阶段的log收集和分析。

对各个log分析的过程：
- 首先可以根据手机性能，如核数、机型、内存来判断对应耗时是不是应该判定为卡顿。如一些差的机器，或者内存本身不足的时候。
- 根据CPU情况，是否是app拿不到cpu，被其他应用拿走了。
- 看`timecost`和`threadtimecost`，如果两者差得很多，则是主线程被等待或者资源被抢占。
- 看卡顿发生前最近的几次堆栈，如果堆栈相同，则可以判定为是该处发生卡顿，否则需要比较分析。

## 原理分析
熟悉`Message/Looper/Handler`系列的同学们一定知道，我们整个应用的主线程中，不管有多少`handler`，只有一个`looper`。  
如果再细心一点会发现在Looper的loop方法中有这么一段：
```java
public static void loop() {
    ...
    for (;;) {
        ...
        // This must be in a local variable, in case a UI event sets the logger
        Printer logging = me.mLogging;
        if (logging != null) {
            logging.println(">>>>> Dispatching to " + msg.target + " " + msg.callback + ": " + msg.what);
        }
        msg.target.dispatchMessage(msg); //【核心】dispatchMessage 执行前后都会调用Printer打印日志信息
        if (logging != null) {
            logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
        }
        ...
    }
}
```
我们只要有办法检测 `msg.target.dispatchMessage(msg)` 的执行时间，就能够检测到部分UI线程是否有耗时操作了。可以看到在执行此代码前后，如果设置了logging，会分别打印出 `>>>>> Dispatching to` 和 `<<<<< Finished to` 这样的log。  

是的，核心就是这个`Printer`，它在每个`message`处理的前后被调用，而如果主线程卡住了，不就是在`dispatchMessage`里卡住了吗？

而这个`Printer`我们是可以自定义的：
```java
Looper.getMainLooper().setMessageLogging(new Printer() {});
```

我们可以通过计算两次log之间的时间差值，大致代码如下：
```java
//在Application的onCreate方法中执行
Looper.getMainLooper().setMessageLogging(log -> {
   if (log.startsWith(">>>>> Dispatching")) {
      LogMonitor.SINGLETON.startMonitor();
   } else if (log.startsWith("<<<<< Finished")) {
      LogMonitor.SINGLETON.removeMonitor();
   }
});
```

假设我们的阈值是1000ms，当我在匹配到`>>>>> Dispatching`时，我会在1000ms后执行一个任务:在非UI线程中打印出UI线程的堆栈信息。正常情况下，肯定是低于1000ms执行完成的，所以当我匹配到`<<<<< Finished`，会移除该任务。

大概代码如下：
```java
public enum LogMonitor {
    SINGLETON;
    private Handler mIogHandler;
    private static final long TIME_BLOCK = 1000L;
    
     LogMonitor() {
        //Handy class for starting a new thread that has a looper. The looper can then be used to create handler classes.
        HandlerThread mLogThread = new HandlerThread("log");
        mLogThread.start(); //Note that start() must still be called.
        mIogHandler = new Handler(mLogThread.getLooper());
    }
    
    private static Runnable mLogRunnable = () -> {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
        for (StackTraceElement s : stackTrace) {
            sb.append(s.toString()).append("\n");
        }
        Log.e("TAG", sb.toString());
    };

    
    public void startMonitor() {
        mIogHandler.postDelayed(mLogRunnable, TIME_BLOCK);
      //mStartTimeMillis = System.currentTimeMillis();
      //mStartThreadTimeMillis = SystemClock.currentThreadTimeMillis();
    }
    
    public void removeMonitor() {
        mIogHandler.removeCallbacks(mLogRunnable);
    }
}
```

我们利用了`HandlerThread`这个类，同样利用了`Looper`机制，只不过是执行在非UI线程中。如果执行耗时达到我们设置的阈值，则会执行mLogRunnable，打印出UI线程当前的堆栈信息；如果你阈值时间之内完成，则会remove掉该runnable。

核心流程图：
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-2018100300320198.png)  

根据上面的原理，我们是不是可以用mainLooperPrinter来做更多事情呢？  
- 比如，我只要parse出app`包名的第一行`，每次打印出来，是不是就不需要打点也能记录出`用户操作路径`？  
- 再者，比如想做`从onClick到页面创建`的耗时统计，是不是也能用这个原理呢？  

> 目前存在问题是获取线程堆栈是定时3秒取一次的，很可能一些比较快的方法操作一下子完成了没法在stacktrace里面反映出来。

# 其他类似的库 
## Takt 和 TinyDancer：利用Choreographer
[Takt](https://github.com/wasabeef/Takt)  
[TinyDancer](https://github.com/friendlyrobotnyc/TinyDancer)  
Android系统`每隔16ms`发出`VSYNC`信号，`触发对UI进行渲染`。SDK中包含了一个相关类，以及相关回调。理论上来说两次回调的时间周期应该在16ms，`如果超过了16ms我们则认为发生了卡顿`，我们主要就是利用`两次回调间的时间`周期来判断：
大致代码如下：
```java
Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
   @Override
   public void doFrame(long l) {
      if (LogMonitor.getInstance().isMonitor()) { //是否包含此runnable
         LogMonitor.getInstance().removeMonitor(); //如果包含则移除掉
      }
      LogMonitor.getInstance().startMonitor(); //添加runnable，开始监控
      Choreographer.getInstance().postFrameCallback(this);
   }
});
```

第一次的时候开始检测，如果大于阈值则输出相关堆栈信息，否则则移除。

## Cockroach：利用Looper
[Cockroach](https://github.com/android-notes/Cockroach)  

>  
   项目简介：  
   降低Android非必要crash
   cockroach 通过替换`ActivityThread.mH.mCallback`，实现拦截Activity生命周期， 通过调用`ActivityManager`的`finishActivity`结束掉生命周期抛出异常的Activity

先看一段代码：
```java
new Handler(Looper.getMainLooper()).post(() -> {});
```

该代码在UI线程中的`MessageQueue`中插入一个`Message`，最终会在`loop()`方法中取出并执行。
假设，我在run方法中拿到MessageQueue，自己执行原本的Looper.loop()方法逻辑，那么`后续的UI线程的Message就会将直接让我们处理`，这样我们就可以做一些事情：
```java
new Handler(Looper.getMainLooper()).post(() -> {
   try {
      final Looper me = Looper.getMainLooper();
      Field fieldQueue = me.getClass().getDeclaredField("mQueue");
      fieldQueue.setAccessible(true);
      final MessageQueue queue = (MessageQueue) fieldQueue.get(me); //拿到MessageQueue
      Method methodNext = queue.getClass().getDeclaredMethod("next");
      methodNext.setAccessible(true);
      Binder.clearCallingIdentity();
      for (; ; ) {
         Message msg = (Message) methodNext.invoke(queue);
         if (msg == null) {
            return;
         }
         LogMonitor.getInstance().startMonitor(); //开启
         msg.getTarget().dispatchMessage(msg);
         msg.recycle();
         LogMonitor.getInstance().removeMonitor(); //移除
      }
   } catch (Exception e) {
      e.printStackTrace();
   }
});
```
其实很简单，将`Looper.loop`里面本身的代码直接copy来了这里。`当这个消息被处理后，后续的消息都将会在这里进行处理`。
中间有变量和方法需要`反射`来调用，不过不影响查看`msg.getTarget().dispatchMessage(msg);`执行时间，但是就不要在线上使用这种方式了。

2018-10-3