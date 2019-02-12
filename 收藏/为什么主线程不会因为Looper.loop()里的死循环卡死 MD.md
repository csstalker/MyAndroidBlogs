| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
***  
目录  
===  

- [为什么主线程不会因为Looper.loop()里的死循环卡死？](#为什么主线程不会因为looperloop里的死循环卡死？)
	- [参考一](#参考一)
	- [参考二](#参考二)
	- [参考三](#参考三)
  
# 为什么主线程不会因为Looper.loop()里的死循环卡死？  
  
## 参考一  
[原文](https://www.zhihu.com/question/34652589/answer/90344494)  
  
要完全彻底理解这个问题，需要准备以下4方面的知识：Process/Thread，Android Binder IPC，Handler/Looper/MessageQueue消息机制，Linux pipe/epoll机制。  
  
**总结一下楼主主要有3个疑惑：**  
  
1.Android中为什么主线程不会因为Looper.loop()里的死循环卡死？  
  
2.没看见哪里有相关代码为这个死循环准备了一个新线程去运转？  
  
3.Activity的生命周期这些方法这些都是在主线程里执行的吧，那这些生命周期方法是怎么实现在死循环体外能够执行起来的？  
  
-----------------------------------------------------------------------------------------------------  
针对这些疑惑，  
  
[@hi大头鬼hi](//www.zhihu.com/people/50b28faaec835947511e005cd6734484)  
  
[@Rocko](//www.zhihu.com/people/4897651cf72b010294463a1fd113af52)  
  
[@陈昱全](//www.zhihu.com/people/53013cf8f04bf168814156e563beb935)  
  
大家回答都比较精炼，接下来我再更进一步详细地一一解答楼主的疑惑：  
  
**(1) Android中为什么主线程不会因为Looper.loop()里的死循环卡死？**  
  
这里涉及线程，先说说说进程/线程，**进程：**每个app运行时前首先创建一个进程，该进程是由Zygote fork出来的，用于承载App上运行的各种Activity/Service等组件。进程对于上层应用来说是完全透明的，这也是google有意为之，让App程序都是运行在Android Runtime。大多数情况一个App就运行在一个进程中，除非在AndroidManifest.xml中配置Android:process属性，或通过native代码fork进程。  
  
**线程：**线程对应用来说非常常见，比如每次new Thread().start都会创建一个新的线程。该线程与App所在进程之间资源共享，从Linux角度来说进程与线程除了是否共享资源外，并没有本质的区别，都是一个task_struct结构体**，在CPU看来进程或线程无非就是一段可执行的代码，CPU采用CFS调度算法，保证每个task都尽可能公平的享有CPU时间片**。  
  
有了这么准备，再说说死循环问题：  
  
对于线程既然是一段可执行的代码，当可执行代码执行完成后，线程生命周期便该终止了，线程退出。而对于主线程，我们是绝不希望会被运行一段时间，自己就退出，那么如何保证能一直存活呢？**简单做法就是可执行代码是能一直执行下去的，死循环便能保证不会被退出，**例如，binder线程也是采用死循环的方法，通过循环方式不同与Binder驱动进行读写操作，当然并非简单地死循环，无消息时会休眠。但这里可能又引发了另一个问题，既然是死循环又如何去处理其他事务呢？通过创建新线程的方式。  
  
真正会卡死主线程的操作是在回调方法onCreate/onStart/onResume等操作时间过长，会导致掉帧，甚至发生ANR，looper.loop本身不会导致应用卡死。  
  
**(2) 没看见哪里有相关代码为这个死循环准备了一个新线程去运转？**  
  
事实上，会在进入死循环之前便创建了新binder线程，在代码ActivityThread.main()中：  
  
```  
public static void main(String[] args) {  
        ....  
  
        //创建Looper和MessageQueue对象，用于处理主线程的消息  
        Looper.prepareMainLooper();  
  
        //创建ActivityThread对象  
        ActivityThread thread = new ActivityThread();   
  
        //建立Binder通道 (创建新线程)  
        thread.attach(false);  
  
        Looper.loop(); //消息循环运行  
        throw new RuntimeException("Main thread loop unexpectedly exited");  
    }  
```  
  
**thread.attach(false)；便会创建一个Binder线程（具体是指ApplicationThread，Binder的服务端，用于接收系统服务AMS发送来的事件），该Binder线程通过Handler将Message发送给主线程**，具体过程可查看 [startService流程分析](https://link.zhihu.com/?target=http%3A//gityuan.com/2016/03/06/start-service/)，这里不展开说，简单说Binder用于进程间通信，采用C/S架构。关于binder感兴趣的朋友，可查看我回答的另一个知乎问题：  
[为什么Android要采用Binder作为IPC机制？ - Gityuan的回答](https://www.zhihu.com/question/39440766/answer/89210950)  
  
另外，**ActivityThread实际上并非线程**，不像HandlerThread类，ActivityThread并没有真正继承Thread类，只是往往运行在主线程，该人以线程的感觉，其实承载ActivityThread的主线程就是由Zygote fork而创建的进程。  
  
**主线程的死循环一直运行是不是特别消耗CPU资源呢？** 其实不然，这里就涉及到`Linux pipe/e****poll机制`，简单说就是在主线程的MessageQueue没有消息时，便阻塞在loop的queue.next()中的nativePollOnce()方法里，详情见[Android消息机制1-Handler(Java层)](https://link.zhihu.com/?target=http%3A//www.yuanhh.com/2015/12/26/handler-message-framework/%23next)，此时主线程会释放CPU资源进入休眠状态，直到下个消息到达或者有事务发生，通过往pipe管道写端写入数据来唤醒主线程工作。这里采用的epoll机制，是一种IO多路复用机制，可以同时监控多个描述符，当某个描述符就绪(读或写就绪)，则立刻通知相应程序进行读或写操作，本质同步I/O，即读写是阻塞的。 **所以说，主线程大多数时候都是处于休眠状态，并不会消耗大量CPU资源。**  
  
**(3) Activity的生命周期是怎么实现在死循环体外能够执行起来的？**  
  
ActivityThread的内部类H继承于Handler，通过handler消息机制，简单说Handler机制用于同一个进程的线程间通信。  
  
**Activity的生命周期都是依靠主线程的Looper.loop，当收到不同Message时则采用相应措施：**  
在H.handleMessage(msg)方法中，根据接收到不同的msg，执行相应的生命周期。  
  
比如收到msg=H.LAUNCH_ACTIVITY，则调用ActivityThread.handleLaunchActivity()方法，最终会通过反射机制，创建Activity实例，然后再执行Activity.onCreate()等方法；  
 再比如收到msg=H.PAUSE_ACTIVITY，则调用ActivityThread.handlePauseActivity()方法，最终会执行Activity.onPause()等方法。 上述过程，我只挑核心逻辑讲，真正该过程远比这复杂。  
  
**主线程的消息又是哪来的呢？**当然是App进程中的其他线程通过Handler发送给主线程，请看接下来的内容：  
  
----------------------------------------------------------------------------------------------------------  
**最后，从进程与线程间通信的角度，通过一张图加深大家对App运行过程的理解：**  
  
![](https://pic3.zhimg.com/80/7fb8728164975ac86a2b0b886de2b872_hd.jpg)</figure>  
  
**system_server进程是系统进程**，java framework框架的核心载体，里面运行了大量的系统服务，比如这里提供ApplicationThreadProxy（简称ATP），ActivityManagerService（简称AMS），这个两个服务都运行在system_server进程的不同线程中，由于ATP和AMS都是基于IBinder接口，都是binder线程，binder线程的创建与销毁都是由binder驱动来决定的。  
  
**App进程则是我们常说的应用程序**，主线程主要负责Activity/Service等组件的生命周期以及UI相关操作都运行在这个线程； 另外，每个App进程中至少会有两个binder线程 ApplicationThread(简称AT)和ActivityManagerProxy（简称AMP），除了图中画的线程，其中还有很多线程，比如signal catcher线程等，这里就不一一列举。  
  
Binder用于不同进程之间通信，由一个进程的Binder客户端向另一个进程的服务端发送事务，比如图中线程2向线程4发送事务；而handler用于同一个进程中不同线程的通信，比如图中线程4向主线程发送消息。  
  
**结合图说说Activity生命周期，比如暂停Activity，流程如下：**  
  
1.  线程1的AMS中调用线程2的ATP；（由于同一个进程的线程间资源共享，可以相互直接调用，但需要注意多线程并发问题）  
  
2.  线程2通过binder传输到App进程的线程4；  
  
3.  线程4通过handler消息机制，将暂停Activity的消息发送给主线程；  
  
4.  主线程在looper.loop()中循环遍历消息，当收到暂停Activity的消息时，便将消息分发给ActivityThread.H.handleMessage()方法，再经过方法的调用，最后便会调用到Activity.onPause()，当onPause()处理完后，继续循环loop下去。  
  
## 参考二  
[原文](https://www.zhihu.com/question/34652589/answer/157834250)  
  
简单一句话是：Android应用程序的主线程在进入`消息循环过程前`，会在内部创建一个`Linux管道（Pipe）`，这个管道的作用是使得Android应用程序主线程在消息队列为空时可以进入`空闲等待`状态，并且使得当应用程序的消息队列有消息需要处理时`唤醒`应用程序的主线程。  
  
---  
这一题是需要从消息循环、消息发送和消息处理三个部分理解Android应用程序的消息处理机制了，这里我对一些要点作一个总结：  
  
A. Android应用程序的消息处理机制由消息循环、消息发送和消息处理三个部分组成的。  
  
B. Android应用程序的主线程在进入消息循环过程前，会在内部创建一个Linux管道（Pipe），这个管道的作用是使得Android应用程序主线程在消息队列为空时可以进入空闲等待状态，并且使得当应用程序的消息队列有消息需要处理时唤醒应用程序的主线程。  
  
C. Android应用程序的主线程进入空闲等待状态的方式实际上就是在管道的读端等待管道中有新的内容可读，具体来说就是是通过Linux系统的Epoll机制中的epoll_wait函数进行的。  
  
D. 当往Android应用程序的消息队列中加入新的消息时，会同时往管道中的写端写入内容，通过这种方式就可以唤醒正在等待消息到来的应用程序主线程。  
  
E. 当应用程序主线程在进入空闲等待前，会认为当前线程处理空闲状态，于是就会调用那些已经注册了的IdleHandler接口，使得应用程序有机会在空闲的时候处理一些事情。  
  
## 参考三  
[链接](https://www.jianshu.com/p/cfe50b8b0a41)  
正如我们所知，在android中如果主线程中进行耗时操作会引发ANR（Application Not Responding）异常。  
  
> 造成ANR的原因一般有两种：  
  
1.  当前的事件没有机会得到处理（即主线程正在处理前一个事件，没有及时的完成或者looper被某种原因阻塞住了）  
2.  当前的事件正在处理，但没有及时完成  
  
为了避免ANR异常，android使用了Handler消息处理机制。让耗时操作在子线程运行。  
  
**因此产生了一个问题，主线程中的Looper.loop()一直无限循环检测消息队列中是否有新消息为什么不会造成ANR？**  
  
~~本人面试网易的时候就被问到了T_T~~  
  
**源码分析**  
  
ActivityThread.java 是主线程入口的类，这里你可以看到写Java程序中司空见惯的main方法，而main方法正是整个Java程序的入口。  
  
> ActivityThread源码  
  
```  
    public static final void main(String[] args) {  
        ...  
        //创建Looper和MessageQueue  
        Looper.prepareMainLooper();  
        ...  
        //轮询器开始轮询  
        Looper.loop();  
        ...  
    }  
```  
  
> Looper.loop()方法  
  
```  
   while (true) {  
       //取出消息队列的消息，可能会阻塞  
       Message msg = queue.next(); // might block  
       ...  
       //解析消息，分发消息  
       msg.target.dispatchMessage(msg);  
       ...  
    }  
```  
  
显而易见的，如果main方法中没有looper进行循环，那么主线程一运行完毕就会退出。这还玩个蛋啊！  
  
**总结：ActivityThread的main方法主要就是做消息循环，一旦退出消息循环，那么你的应用也就退出了。**  
  
* * *  
  
我们知道了消息循环的必要性，那为什么这个死循环不会造成ANR异常呢？  
  
因为Android 的是由事件驱动的，looper.loop() 不断地接收事件、处理事件，每一个点击触摸或者说Activity的生命周期都是运行在 Looper.loop() 的控制之下，如果它停止了，应用也就停止了。只能是某一个消息或者说对消息的处理阻塞了 Looper.loop()，而不是 Looper.loop() 阻塞它。  
  
**也就说我们的代码其实就是在这个循环里面去执行的，当然不会阻塞了。**  
  
> handleMessage方法部分源码  
  
```  
 public void handleMessage(Message msg) {  
        if (DEBUG_MESSAGES) Slog.v(TAG, ">>> handling: " + codeToString(msg.what));  
        switch (msg.what) {  
            case LAUNCH_ACTIVITY: {  
                Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityStart");  
                final ActivityClientRecord r = (ActivityClientRecord) msg.obj;  
                r.packageInfo = getPackageInfoNoCheck(r.activityInfo.applicationInfo, r.compatInfo);  
                handleLaunchActivity(r, null);  
                Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);  
            }  
            break;  
            case RELAUNCH_ACTIVITY: {  
                Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityRestart");  
                ActivityClientRecord r = (ActivityClientRecord) msg.obj;  
                handleRelaunchActivity(r);  
                Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);  
            }  
            break;  
            case PAUSE_ACTIVITY:  
                Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityPause");  
                handlePauseActivity((IBinder) msg.obj, false, (msg.arg1 & 1) != 0, msg.arg2, (msg.arg1 & 2) != 0);  
                maybeSnapshot();  
                Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);  
                break;  
            case PAUSE_ACTIVITY_FINISHING:  
                Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityPause");  
                handlePauseActivity((IBinder) msg.obj, true, (msg.arg1 & 1) != 0, msg.arg2, (msg.arg1 & 1) != 0);  
                Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);  
                break;  
            ...........  
        }  
    }  
```  
  
可以看见Activity的生命周期都是依靠主线程的Looper.loop，当收到不同Message时则采用相应措施。  
  
如果某个消息处理时间过长，比如你在onCreate(),onResume()里面处理耗时操作，那么下一次的消息比如用户的点击事件不能处理了，整个循环就会产生卡顿，时间一长就成了ANR。  
  
让我们再看一遍造成ANR的原因，你可能就懂了。  
  
> 造成ANR的原因一般有两种：  
  
1.  当前的事件没有机会得到处理（即主线程正在处理前一个事件，没有及时的完成或者looper被某种原因阻塞住了）  
2.  当前的事件正在处理，但没有及时完成  
  
而且主线程Looper从消息队列读取消息，当读完所有消息时，主线程阻塞。子线程往消息队列发送消息，并且往管道文件写数据，主线程即被唤醒，从管道文件读取数据，主线程被唤醒只是为了读取消息，当消息读取完毕，再次睡眠。因此loop的循环并不会对CPU性能有过多的消耗。  
  
**总结**  
Looer.loop()方法可能会引起主线程的阻塞，但只要它的消息循环没有被阻塞，能一直处理事件就不会产生ANR异常。  
  
主线程Looper从消息队列读取消息，当读完所有消息时，主线程阻塞。子线程往消息队列发送消息，并且往管道文件写数据，主线程即被唤醒，从管道文件读取数据，主线程被唤醒只是为了读取消息，当消息读取完毕，再次睡眠。因此**loop的循环并不会对CPU性能有过多的消耗**。  
