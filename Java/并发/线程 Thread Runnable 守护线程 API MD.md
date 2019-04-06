| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
线程 Thread Runnable 守护线程【API】  
***  
目录  
===  

- [Runnable](#Runnable)
- [Thread](#Thread)
	- [简介](#简介)
	- [构造方法](#构造方法)
	- [常量](#常量)
	- [静态方法](#静态方法)
	- [公共方法](#公共方法)
	- [不常用方法](#不常用方法)
	- [已过时方法](#已过时方法)
- [对 join 方法的测试案例](#对-join-方法的测试案例)
- [守护线程](#守护线程)
	- [守护线程使用注意](#守护线程使用注意)
	- [守护线程的意义](#守护线程的意义)
  
# Runnable  
[JDK文档](http://tool.oschina.net/uploads/apidocs/jdk-zh/java/lang/Runnable.html)  
  
所有已知实现类：  
`Thread`, `TimerTask`, AsyncBoxView.ChildState, FutureTask, RenderableImageProducer, SwingWorker  
  
Runnable 接口应该由那些打算通过某一`线程`执行其实例的类来实现。  
设计该接口的目的是为希望在活动时执行代码的对象提供一个公共协议。例如，Thread 类实现了 Runnable。激活的意思是说某个线程已启动并且尚未停止。  
  
此外，Runnable 为非 Thread 子类的类提供了一种激活方式。通过实例化某个 Thread 实例并将自身作为运行目标，就可以运行实现 Runnable 的类而无需创建 Thread 的子类。大多数情况下，如果只想重写 run() 方法，而不重写其他 Thread 方法，那么应使用 Runnable 接口。这很重要，因为除非程序员打算修改或增强类的基本行为，否则不应为该类创建子类。  
  
使用实现接口 Runnable 的对象创建一个线程时，启动该线程将导致在独立执行的线程中调用对象的 run 方法。  
  
方法 run 的常规协定是，它可能执行任何所需的动作。  
  
# Thread  
[JDK文档](http://tool.oschina.net/uploads/apidocs/jdk-zh/java/lang/Thread.html)  
  
## 简介  
```java  
public class Thread extends Object implements Runnable  
```  
  
Thread 是程序中的执行线程。Java 虚拟机允许应用程序并发地运行多个执行线程。  
  
每个线程都有一个`优先级`，高优先级线程的执行优先于低优先级线程。每个线程都可以或不可以标记为一个`守护程序`。当某个线程中运行的代码创建一个新 Thread 对象时，该新线程的初始优先级被设定为创建线程的优先级，并且当且仅当创建线程是守护线程时，新线程才是守护程序。  
  
当 Java 虚拟机启动时，通常都会有单个非守护线程（它通常会调用某个指定类的 main 方法）。Java 虚拟机会继续执行线程，直到下列任一情况出现时为止：  
*   调用了 Runtime 类的 exit 方法，并且安全管理器允许退出操作发生。  
*   非守护线程的所有线程都已停止运行，无论是通过从对 run 方法的调用中返回，还是通过抛出一个传播到 run 方法之外的异常。  
  
创建新执行线程有两种方法。一种方法是将类声明为 Thread 的子类。该子类应重写 Thread 类的 run 方法。接下来可以分配并启动该子类的实例。  
创建线程的另一种方法是声明实现 Runnable 接口的类。该类然后实现 run 方法。然后可以分配该类的实例，在创建 Thread 时作为一个参数来传递并启动。  
每个线程都有一个标识名，`多个线程可以同名`。如果线程创建时没有指定标识名，就会为其生成一个新名称。  
  
## 构造方法  
```java  
Thread(ThreadGroup group, Runnable target, String name, long stackSize)  
```  
分配新的 Thread 对象，将target作为其运行对象，将name作为其名称，作为group所引用的线程组的一员，并具有指定的堆栈大小  
*   group - 线程组。线程组可以批量的管理线程或线程组对象，有效的对线程或线程组进行组织。  
*   target - 其 run 方法被调用的对象。  
    *   如果 target 参数不是 null，则 target 的 run 方法在启动该线程时调用。如果 target 参数为 null，则该线程的 run 方法在该线程启动时调用。  
    *   新创建线程的优先级被设定为创建该线程的线程的优先级，即当前正在运行的线程的优先级。方法 setPriority 可用于将优先级更改为一个新值。  
    *   当且仅当创建新线程的线程当前被标记为守护线程时，新创建的线程才被标记为守护线程。方法 setDaemon 可用于改变线程是否为守护线程。  
*   name - 新线程的名称。自动生成的名称的形式为 `Thread- n`，其中的 n 为整数。  
*   stackSize - 新线程的`预期堆栈大小`，为零时表示忽略该参数。  
    堆栈大小是虚拟机要为该线程堆栈分配的地址空间的近似字节数。  
    stackSize 参数的作用具有高度的平台依赖性，在某些平台上，stackSize 参数的值无论如何不会起任何作用。  
    作为建议，可以让虚拟机自由处理 stackSize 参数。  
  
其他构造方法  
*   Thread()  
*   Thread(String name)  
*   Thread(Runnable target)  
*   Thread(Runnable target, String name)  
*   Thread(ThreadGroup group, String name)  
*   Thread(ThreadGroup group, Runnable target)  
*   Thread(ThreadGroup group, Runnable target, String name)  
  
## 常量  
public final static int `MAX_PRIORITY` = 10; //The maximum priority that a thread can have.  
public final static int `NORM_PRIORITY` = 5; //The default priority that is assigned to a thread.  
public final static int `MIN_PRIORITY` = 1; //The minimum priority that a thread can have.  
  
## 静态方法  
*   static int activeCount()  返回当前线程的`线程组中活动线程的数目`。  
*   static Thread currentThread()  返回对当前`正在执行的线程对象`的引用。  
*   static void dumpStack()  将当前线程的`堆栈跟踪`打印至标准错误流。效果类似于 `new Throwable().printStackTrace()` 。该方法仅用于调试。  
  
```java  
// 调用 Thread.dumpStack() 后打印的内容  
java.lang.Exception: Stack trace  
    at java.lang.Thread.dumpStack(Thread.java:1329)  
    at Test.main(Test.java:6)  
  
new Throwable().printStackTrace();  
java.lang.Throwable  
    at Test.main(Test.java:6)  
```  
  
- static int enumerate(Thread[] tarray)  将当前线程的线程组及其子组中的活动线程复制到数组中。该方法只调用当前线程的线程组的 enumerate 方法，且带有数组参数。返回放入该数组的线程数。  
- static Map getAllStackTraces()  返回所有活动线程的堆栈跟踪的一个映射。  
    - 映射的键是`线程`，而每个映射值都是一个 `StackTraceElement 数组`，该数组表示相应 Thread 的堆栈转储。 返回的堆栈跟踪的格式都是针对 getStackTrace 方法指定的。  
    - 在调用该方法的同时，线程可能也在执行。每个线程的堆栈跟踪仅代表一个`快照`，并且每个堆栈跟踪都可以在不同时间获得。如果虚拟机没有线程的堆栈跟踪信息，则映射值中将返回一个零长度数组。  
  
```java  
Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();  
for (Thread thread : map.keySet()) {  
    System.out.println(thread.toString() + "  " + Arrays.toString(map.get(thread)));  
}  
  
Thread[Finalizer,8,system]  [java.lang.Object.wait(Native Method), java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:143), java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:164), java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:209)]  
Thread[Attach Listener,5,system]  []  
Thread[Signal Dispatcher,9,system]  []  
Thread[Reference Handler,10,system]  [java.lang.Object.wait(Native Method), java.lang.Object.wait(Object.java:502), java.lang.ref.Reference.tryHandlePending(Reference.java:191), java.lang.ref.Reference$ReferenceHandler.run(Reference.java:153)]  
Thread[main,5,main]  [java.lang.Thread.dumpThreads(Native Method), java.lang.Thread.getAllStackTraces(Thread.java:1603), Test.main(Test.java:8)]  
```  
  
*   static Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler()  返回线程由于未捕获到异常而突然终止时调用的默认处理程序。如果返回值为 null，则没有默认处理程序。  
*   static void setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler eh) 设置当线程由于未捕获到异常而突然终止，并且没有为该线程定义其他处理程序时所调用的默认处理程序  
*   static boolean holdsLock(Object obj)  当且仅当当前线程在指定的对象上保持监视器锁时，才返回 true。该方法旨在使程序能够断言当前线程已经保持一个指定的锁：assert Thread.holdsLock(obj);  
*   static boolean interrupted()  测试当前线程是否已经中断。  
*   static void sleep(long millis)  在指定的毫秒数内让【当前】正在执行的线程休眠。  
*   static void sleep(long millis, int nanos)  在指定的毫秒数+纳秒数内让当前正在执行的线程休眠。nanos 的值必须在 0-999999 之间。  
    *   此操作受到系统计时器和调度程序精度和准确性的影响。该线程不丢失任何监视器的所属权。  
    *   如果任何线程中断了当前线程，则抛出 InterruptedException，当抛出该异常时，当前线程的 中断状态 被清除。  
*   static void yield()  暂停当前正在执行的线程对象，并执行其他线程。  
  
## 公共方法  
*   long getId()  返回该线程的标识符。线程 ID 是一个正的 long 数，在创建该线程时生成。线程 ID 是唯一的，并终生不变。线程终止时，该线程 ID 可以被重新使用。  
*   String getName()  返回该线程的名称。  
*   void setName(String name)  改变线程名称，使之与参数 name 相同。  
*   int getPriority()  返回线程的优先级。  
*   void setPriority(int newPriority)  更改线程的优先级。  
*   StackTraceElement[] getStackTrace()  返回一个表示该线程堆栈转储的堆栈跟踪元素数组。  
    *   如果该线程尚未启动或已经终止，则该方法将返回一个零长度数组。  
    *   如果返回的数组不是零长度的，则其第一个元素代表堆栈顶，它是该序列中最新的方法调用。最后一个元素代表堆栈底，是该序列中最旧的方法调用。  
*   Thread.State getState()  返回该线程的状态。该方法用于监视系统状态，不用于同步控制。  
    - Thread.State.NEW    至今尚未启动的线程处于这种状态。  
    - Thread.State.RUNNABLE    正在 Java 虚拟机中执行的线程处于这种状态。  
    - Thread.State.BLOCKED    受阻塞并等待某个监视器锁的线程处于这种状态。  
    - Thread.State.WAITING    无限期地等待另一个线程来执行某一特定操作的线程处于这种状态。  
    - Thread.State.TIMED_WAITING    等待另一个线程来执行取决于指定等待时间的操作的线程处于这种状态。  
    - Thread.State.TERMINATED    已退出的线程处于这种状态。  
    - 在给定时间点上，一个线程只能处于一种状态。这些状态是虚拟机状态，它们并没有反映所有操作系统线程状态。  
  
*   Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() 返回该线程由于未捕获到异常而突然终止时调用的处理程序。  
*   void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler eh)  设置该线程由于未捕获到异常而突然终止时调用的处理程序。  
*   void interrupt()  中断线程。如果线程在调用 Object 类的 wait()、wait(long) 或 wait(long, int) 方法，或者该类的 join()、join(long)、join(long, int)、sleep(long) 或 sleep(long, int) 方法过程中受阻，则其中断状态将被清除，它还将收到一个 InterruptedException。  
*   boolean isAlive()  测试线程是否处于活动状态。如果线程已经启动且尚未终止，则为活动状态。  
*   boolean isInterrupted()  测试线程是否已经中断。线程的 中断状态 不受该方法的影响。  
*   void run()  如果该线程是使用独立的 Runnable 运行对象构造的，则调用该 Runnable 对象的 run 方法；否则，该方法不执行任何操作并返回。  
*   boolean isDaemon()  测试该线程是否为守护线程。  
*   void setDaemon(boolean on)  将该线程标记为守护线程或用户线程。当正在运行的线程都是守护线程是，Java虚拟机退出。该方法必须在启动线程前使用。  
*   void start()  使该线程开始执行；Java 虚拟机调用该线程的 run 方法。  
    *   结果是两个线程并发地运行；当前线程（从调用返回给 start 方法）和另一个线程（执行其 run 方法）。  
    *   多次启动一个线程是非法的(会抛出IllegalThreadStateException)。特别是当线程已经结束执行后，不能再重新启动。  
*   String toString()  返回该线程的字符串表示形式，包括线程名称、优先级和线程组。`Thread[Thread-0,5,main]`，`Thread[main,5,main]`  
  
## 不常用方法  
*   void checkAccess()  判定当前运行的线程是否有权修改该线程。如果有安全管理器，则调用其 checkAccess 方法，并将该线程作为其参数。如果不允许当前线程访问该线程，抛出 SecurityException。  
*   ClassLoader getContextClassLoader()  返回该线程的上下文 ClassLoader。  
    *   上下文 ClassLoader 由线程创建者提供，供运行于该线程中的代码在加载类和资源时使用。  
    *   如果未设定，则默认为父线程的 ClassLoader 上下文。原始线程的上下文 ClassLoader 通常设定为用于加载应用程序的类加载器。  
*   void setContextClassLoader(ClassLoader cl)  设置该线程的上下文 ClassLoader。上下文 ClassLoader 可以在创建线程设置，并允许创建者在加载类和资源时向该线程中运行的代码提供适当的类加载器。  
*   ThreadGroup getThreadGroup()  返回该线程所属的线程组。如果该线程已经终止（停止运行），该方法则返回 null。  
  
```java  
System.out.println(new Thread().getThreadGroup());  
java.lang.ThreadGroup[name=main,maxpri=10]  
```  
  
*   void join()  让父线程等待子线程结束之后才能继续运行。  
    *   When we call this method using a thread object, it suspends the execution of the calling thread until the object called finishes its execution.当我们调用某个线程的这个方法时，这个方法会挂起调用线程，直到被调用线程结束执行，调用线程才会继续执行。  
    *   如果任何线程中断了当前线程则抛出 InterruptedException。当抛出该异常时，当前线程的 中断状态 被清除。  
  
*   void join(long millis)  等待该线程终止的时间最长为 millis 毫秒。  
*   void join(long millis, int nanos) 等待该线程终止的时间最长为 millis 毫秒+nanos 纳秒。nanos 的值必须在 0-999999 之间。  
  
  
## 已过时方法  
- int countStackFrames()  已过时  
- void destroy()  已过时  
- void resume()  已过时  
- void stop()  已过时  
- void stop(Throwable obj)  已过时  
- void suspend()  已过时  
  
# 对 join 方法的测试案例  
Thread 类中的 join 方法的主要作用就是同步，它可以使得线程之间的`并行执行变为串行执行`。  
join 的意思是`放弃当前线程的执行，并返回对应的线程`，例如下面代码的意思就是：  
```java  
ThreadJoinTest t1 = new ThreadJoinTest("子线程1");  
ThreadJoinTest t2 = new ThreadJoinTest("子线程2");  
t1.start();//子线程1开始执行  
t1.join();//父线程会放弃 cpu 控制权，直到线程 t1 执行完毕才获取 cpu 控制权  
t2.start();//所以直到线程 t1 执行完毕才会执行子线程2  
```  
  
程序在 main 线程(父线程)中调用 t1 线程的 join 方法，则 main 线程放弃 cpu 控制权，并返回 t1 线程继续执行直到线程 t1 执行完毕。  
所以结果是 t1 线程执行完后，才到主线程执行，相当于在 main 线程中同步 t1 线程，t1 执行完了，main 线程才有执行的机会。  
```java  
public class Test {  
    public static void main(String[] args) {  
        new Parent().start();  
    }  
  
    // 父线程  
    static class Parent extends Thread {  
        public void run() {  
            System.out.println("Parent start");  
            Child child = new Child();  
            child.start();  
            try {  
                child.join();  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
            System.out.println("Parent end");  
        }  
    }  
  
    // 子线程  
    static class Child extends Thread {  
        public void run() {  
            System.out.println("Child start");  
            try {  
                Thread.sleep(1000);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
            System.out.println("Child end");  
        }  
    }  
}  
```  
  
结果为：  
```java  
Parent start  
Child start  
Child end  
Parent end  
```  
  
若果没有 child.join() 结果为：  
```java  
Parent start  
Parent end  
Child start  
Child end  
```  
  
# 守护线程  
在Java中有两类线程：User Thread(用户线程)、Daemon Thread(守护线程) 。  
守护线程也称“服务线程”，`守护线程会在没有用户线程可服务时自动离开`。  
  
守护线程的优先级比较低，用于为系统中的其它对象和线程提供服务。  
Daemon的作用是为其他线程的运行提供便利服务，守护线程最典型的应用就是 GC (垃圾回收器)，它就是一个很称职的守护者。  
  
User和Daemon两者几乎没有区别，唯一的不同之处就在于虚拟机的离开：如果 User Thread已经全部退出运行了，只剩下Daemon Thread存在了，虚拟机也就退出了。 因为没有了被守护者，Daemon也就没有工作可做了，也就没有继续运行程序的必要了。  
  
守护线程并非只有虚拟机内部提供，用户在编写程序时也可以自己设置守护线程。  
```java  
Thread daemonTread = new Thread();    
daemonThread.setDaemon(true);   // 设定 daemonThread 为 守护线程  
daemonThread.isDaemon();  // 验证当前线程是否为守护线程，返回 true 则为守护线程   
```  
  
## 守护线程使用注意  
setDaemon(true) 必须在 start() 之前设置，否则会跑出一个 IllegalThreadStateException 异常，你不能把正在运行的常规线程设置为守护线程。  
在Daemon线程中产生的新线程也是Daemon的。   
```java  
Thread thread = new Thread(() -> System.out.println(new Thread().isDaemon()));//true  
thread.setDaemon(true);  
thread.start();    
```  
  
守护线程应该永远不去访问固有资源，如文件、数据库，因为它会在任何时候甚至在一个操作的中间发生中断。  
```java  
public class Test {  
    public static void main(String[] args) {  
        Thread thread = new Thread(() -> {  
            try {  
                System.out.println("start");  
                Thread.sleep(1000);//阻塞1秒后运行  
                FileWriter writer = new FileWriter(new File("d://daemon.txt"), true);  
                writer.write("daemon");  
                writer.close();  
                System.out.println("end");  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        });  
        thread.setDaemon(true); //设置守护线程时不会有任何内容写入文件  
        thread.start(); //开始执行分进程  
    }  
}  
```  
  
结果，字符串并没有写入指定文件。原因很简单，直到主线程完成，守护线程仍处于1秒的阻塞状态，主线程很快就运行完了，然后虚拟机就退出了，Daemon停止服务，输出操作自然失败了。  
  
## 守护线程的意义  
  
用个比较通俗的比喻，任何一个守护线程都是整个JVM中所有非守护线程的保姆：只要当前JVM实例中尚存在任何一个非守护线程没有结束，守护线程就全部工作；只有当最后一个非守护线程结束时，守护线程随着JVM一同结束工作。  
  
比如你正在用 Java 写成的编辑器写 Word 文档，你处理敲键盘事件的线程是个非守护线程， 除此之外，后台还有一个进行拼写检查的线程，它是个守护线程，他尽量不打扰你写稿子，他们可以同时进行，当守护线程发现有拼写错误时在状态条显示错误，但是你可以忽略。  
  
比如城堡门前有个卫兵（守护线程），里面有诸侯（非守护线程），他们是可以同时干着各自的活儿，但是如果城堡里面的人都搬走了， 那么卫兵也就没有存在的意义了。  
  
比如在使用长连接的comet服务端推送技术中，将消息推送线程设置为守护线程，服务于ChatServlet的servlet用户线程，在servlet的init启动消息线程，servlet一旦初始化后，一直存在服务器，servlet摧毁后，消息线程自动退出。  
  
2018-5-31  
