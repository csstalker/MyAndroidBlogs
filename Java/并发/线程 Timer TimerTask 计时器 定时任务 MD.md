| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
线程 Timer TimerTask 计时器 定时任务  
***  
目录  
===  

- [Timer 计时器](#timer-计时器)
- [TimerTask 计时任务](#timertask-计时任务)
- [案例](#案例)
	- [案例1：延时执行指定任务](#案例1：延时执行指定任务)
	- [案例2：执行定时任务](#案例2：执行定时任务)
- [Timer 的缺陷](#timer-的缺陷)
	- [Timer 抛出异常缺陷](#timer-抛出异常缺陷)
	- [Timer 管理时间延迟缺陷](#timer-管理时间延迟缺陷)
- [用 ScheduledExecutorService 替代 Timer](#用-scheduledexecutorservice-替代-timer)
	- [可以规避 Timer 抛出异常缺陷](#可以规避-timer-抛出异常缺陷)
	- [可以规避 Timer 管理时间延迟缺陷](#可以规避-timer-管理时间延迟缺陷)
  
# Timer 计时器  
Timer可以定时(在指定时间执行任务)、延迟(延迟指定时间执行任务)、周期性地执行任务(每隔指定时间执行一次任务)。  
  
一种工具，线程用其安排以后在后台线程中执行的任务。可安排任务执行一次，或者定期重复执行。  
与每个 Timer 对象相对应的是单个后台线程，用于顺序地执行所有计时器任务。计时器任务应该迅速完成。如果完成某个计时器任务的时间太长，那么它会“独占”计时器的任务执行线程。因此，这就可能延迟后续任务的执行，而这些任务就可能“堆在一起”，并且在上述不友好的任务最终完成时才能够被快速连续地执行。  
  
对 Timer 对象最后的引用完成后，并且 所有未处理的任务都已执行完成后，计时器的任务执行线程会正常终止（并且成为垃圾回收的对象）。但是这可能要很长时间后才发生。  
默认情况下，任务执行线程并不作为守护线程来运行，所以它能够阻止应用程序终止。如果调用者想要快速终止计时器的任务执行线程，那么调用者应该调用计时器的 cancel 方法。  
如果意外终止了计时器的任务执行线程，例如调用了它的 stop 方法，那么所有以后对该计时器安排任务的尝试都将导致 IllegalStateException，就好像调用了计时器的 cancel 方法一样。  
  
此类是线程安全的：多个线程可以共享单个 Timer 对象而无需进行外部同步。  
此类不提供实时保证：它使用 Object.wait(long) 方法来安排任务。  
实现注意事项：此类可扩展到大量同时安排的任务（存在数千个都没有问题）。在内部，它使用二进制堆来表示其任务队列，所以安排任务的开销是 O(log n)，其中 n 是同时安排的任务数。  
实现注意事项：所有构造方法都启动计时器线程。  
  
**构造方法**  
- Timer()     创建一个新计时器。  
- Timer(String name)     创建一个新计时器，其相关的线程具有指定的名称。  
- Timer(boolean isDaemon)     创建一个新计时器，可以指定其相关的线程作为守护程序运行。  
- Timer(String name, boolean isDaemon)     创建一个新计时器，其相关的线程具有指定的名称，并且可以指定作为守护程序运行。    
  
**公共方法**  
- void    cancel()     终止此计时器，丢弃所有当前已安排的任务。  
    - 这不会干扰当前正在执行的任务（如果存在）。  
    - 一旦终止了计时器，那么它的执行线程也会终止，并且无法根据它安排更多的任务。  
    - 注意，在此计时器调用的计时器任务的 run 方法内调用此方法，就可以绝对确保正在执行的任务是此计时器所执行的最后一个任务。  
    - 可以重复调用此方法，但是第二次和后续调用无效。  
- int    purge()     从此计时器的任务队列中移除所有已取消的任务。  
- void    schedule(TimerTask task, long delay)     t安排在指定延迟后执行指定的任务。ask - 所要安排的任务，delay - 执行任务前的延迟时间(毫秒)。  
- void    schedule(TimerTask task, Date time)     安排在指定的时间执行指定的任务。如果此时间已过去，则安排立即执行该任务。  
- void    schedule(TimerTask task, long delay, long period)     安排指定的任务从指定的延迟后开始进行重复的固定延迟执行。以近似固定的时间间隔（由指定的周期分隔）进行后续执行。  
    - 在固定延迟执行中，根据前一次执行的实际执行时间来安排每次执行。如果由于任何原因（如垃圾回收或其他后台活动）而延迟了某次执行，则后续执行也将被延迟。从长期来看，执行的频率一般要稍慢于指定周期的倒数（假定 Object.wait(long) 所依靠的系统时钟是准确的）。  
    - 固定延迟执行适用于那些需要“平稳”运行的重复活动。换句话说，它适用于在短期运行中保持频率准确要比在长期运行中更为重要的活动。这包括大多数动画任务，如以固定时间间隔闪烁的光标。这还包括为响应人类活动所执行的固定活动，如在按住键时自动重复输入字符。  
- void    schedule(TimerTask task, Date firstTime, long period)     安排指定的任务在指定的时间开始进行重复的固定延迟执行。  
- void    scheduleAtFixedRate(TimerTask task, long delay, long period)     安排指定的任务在指定的延迟后开始进行重复的固定速率执行。以近似固定的时间间隔（由指定的周期分隔）进行后续执行。  
    - 在固定速率执行中，根据已安排的初始执行时间来安排每次执行。如果由于任何原因（如垃圾回收或其他后台活动）而延迟了某次执行，则将快速连续地出现两次或更多的执行，从而使后续执行能够“追赶上来”。从长远来看，执行的频率将正好是指定周期的倒数（假定 Object.wait(long) 所依靠的系统时钟是准确的）。  
    - 固定速率执行适用于那些对绝对时间敏感的重复执行活动，如每小时准点打钟报时，或者在每天的特定时间运行已安排的维护活动。它还适用于那些完成固定次数执行的总计时间很重要的重复活动，如倒计时的计时器，每秒钟滴答一次，共 10 秒钟。最后，固定速率执行适用于安排多个重复执行的计时器任务，这些任务相互之间必须保持同步。  
- void    scheduleAtFixedRate(TimerTask task, Date firstTime, long period)     安排指定的任务在指定的时间开始进行重复的固定速率执行。  
  
# TimerTask 计时任务  
所有已实现的接口：Runnable  
由 Timer 安排为一次执行或重复执行的任务。  
  
**构造方法**  
protected    TimerTask()    创建一个新的计时器任务。  
    
**公共方法**  
- boolean    cancel()   取消此计时器任务。  
    - 如果任务安排为一次执行且还未运行，或者尚未安排，则永远不会运行。如果任务安排为重复执行，则永远不会再运行。（如果发生此调用时任务正在运行，则任务将运行完，但永远不会再运行。）  
    - 注意，从重复的计时器任务的 run 方法中调用此方法绝对保证计时器任务不会再运行。  
    - 此方法可以反复调用；第二次和以后的调用无效。  
    - 返回：如果此任务安排为一次执行且尚未运行，或者此任务安排为重复执行，则返回 true。如果此任务安排为一次执行且已经运行，或者此任务尚未安排，或者此任务已经取消，则返回 false。（一般来说，如果此方法不允许发生一个或多个已安排执行，则返回 true。）  
- abstract  void    run()    此计时器任务要执行的操作。  
- long    scheduledExecutionTime()   返回此任务最近实际执行的已安排执行时间。  
  
# 案例  
## 案例1：延时执行指定任务  
```java  
final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS", Locale.getDefault());  
System.out.println(format.format(new Date()));  
new Timer().schedule(new TimerTask() {  
    @Override  
    public void run() {  
        System.out.println(format.format(new Date()));  
    }  
}, 500);  
```  
  
## 案例2：执行定时任务  
每10毫秒执行一次指定任务  
1、使用 schedule 方式：  
```java  
final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS", Locale.getDefault());  
System.out.println("schedule 开始时间 " + format.format(new Date()));  
new Timer().schedule(new TimerTask() {//或用 scheduleAtFixedRate 方法  
    @Override  
    public void run() {  
        System.out.println("run  循环执行时间 " + format.format(new Date()));  
    }  
}, 0, 10);  
```  
  
打印结果：  
```java  
schedule 开始时间 2018.06.12 09:27:31 186  
run  循环执行时间 2018.06.12 09:27:31 189  
run  循环执行时间 2018.06.12 09:27:31 200  
run  循环执行时间 2018.06.12 09:27:31 211  
run  循环执行时间 2018.06.12 09:27:31 222  
run  循环执行时间 2018.06.12 09:27:31 233  
run  循环执行时间 2018.06.12 09:27:31 243  
run  循环执行时间 2018.06.12 09:27:31 254  
run  循环执行时间 2018.06.12 09:27:31 265  
run  循环执行时间 2018.06.12 09:27:31 276  
run  循环执行时间 2018.06.12 09:27:31 287  
run  循环执行时间 2018.06.12 09:27:31 297  
run  循环执行时间 2018.06.12 09:27:31 308  
run  循环执行时间 2018.06.12 09:27:31 318  
run  循环执行时间 2018.06.12 09:27:31 329  
```  
可以发现，后续执行时间和首次执行时间的误差是越来越大的，如果时间非常长的话，误差会非常明显，所以schedule适合短时间的周期任务。  
  
2、使用 scheduleAtFixedRate 方式：  
打印结果：  
```java  
schedule 开始时间 2018.06.12 09:29:53 681  
run  循环执行时间 2018.06.12 09:29:53 683  
run  循环执行时间 2018.06.12 09:29:53 694  
run  循环执行时间 2018.06.12 09:29:53 704  
run  循环执行时间 2018.06.12 09:29:53 714  
run  循环执行时间 2018.06.12 09:29:53 724  
run  循环执行时间 2018.06.12 09:29:53 734  
run  循环执行时间 2018.06.12 09:29:53 744  
run  循环执行时间 2018.06.12 09:29:53 754  
run  循环执行时间 2018.06.12 09:29:53 764  
run  循环执行时间 2018.06.12 09:29:53 775  
run  循环执行时间 2018.06.12 09:29:53 784  
run  循环执行时间 2018.06.12 09:29:53 794  
run  循环执行时间 2018.06.12 09:29:53 804  
```  
可以发现，后续执行时间和首次执行时间的误差基本是不变的，即时间非常长时误差也很小，所以schedule适合长短时间的周期任务。  
  
# Timer 的缺陷  
## Timer 抛出异常缺陷  
Timer线程是不会捕获异常的，如果TimerTask抛出的了未检查异常则会导致Timer线程终止，同时Timer也不会重新恢复线程的执行，他会错误的认为整个Timer线程都会取消。同时，已经被安排单尚未执行的TimerTask也不会再执行了，新的任务也不能被调度。故如果TimerTask抛出未检查的异常，Timer将会产生无法预料的行为(当然，可以手动 try catch 捕获此异常)。  
  
下例中TimerTask抛出了未检查的RuntimeException，之后Timer会终止所有任务的运行，该Timer线程也会被立即终止：  
```java  
Timer timer = new Timer();  
timer.schedule(new TimerTask() {  
    @Override  
    public void run() {  
        System.out.println("执行第一个TimerTask  " + System.currentTimeMillis());  
        throw new RuntimeException();//这里抛出未检测的RuntimeException之后，整个Timer线程会被终止  
    }  
}, 0);  
  
Thread.sleep(100);  
  
System.out.println("准备执行第二个TimerTask  " + System.currentTimeMillis());  
timer.schedule(new TimerTask() {  
    @Override  
    public void run() {  
        System.out.println("执行第二个TimerTask  " + System.currentTimeMillis());  
    }  
}, 0);  
```  
  
打印结果：  
```  
执行第一个TimerTask  1528768752890  
Exception in thread "Timer-0" java.lang.RuntimeException  
    at Test$1.run(Test.java:11)  
    at java.util.TimerThread.mainLoop(Unknown Source)  
    at java.util.TimerThread.run(Unknown Source)  
准备执行第二个TimerTask  1528768752991  
Exception in thread "main" java.lang.IllegalStateException: Timer already cancelled.  
    at java.util.Timer.sched(Unknown Source)  
    at java.util.Timer.schedule(Unknown Source)  
    at Test.main(Test.java:18)  
```  
      
可以发现，执行第一个TimerTask时出现未捕获的异常后，第二个TimerTask就不会执行了，因为整个 Timer already cancelled 。  
  
## Timer 管理时间延迟缺陷  
Timer在执行所有定时任务时只会创建一个线程，在执行周期性任务时，如果某个周期性任务的执行时间长度大于其周期时间长度，那么就会导致这一次的任务还在执行，而下一个周期的任务已经需要开始执行了，这样会导致越来越严重的时间延迟。  
```java  
private int num = 0;  
  
public void test()  {  
    final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS", Locale.getDefault());  
    new Timer().schedule(new TimerTask() { //换成 scheduleAtFixedRate 时效果基本一致  
        @Override  
        public void run() {  
            System.out.println(num++ + "  " + format.format(new Date()));  
            try {  
                Thread.sleep(2000);//任务执行时间大于间隔时间  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
        }  
    }, 0, 1000);  
}  
```  
  
打印结果：  
```  
0  2018.06.12 11:05:29 517  
1  2018.06.12 11:05:31 517  
2  2018.06.12 11:05:33 518  
3  2018.06.12 11:05:35 519  
4  2018.06.12 11:05:37 520  
5  2018.06.12 11:05:39 520  
6  2018.06.12 11:05:41 521  
7  2018.06.12 11:05:43 522  
8  2018.06.12 11:05:45 523  
9  2018.06.12 11:05:47 524  
```  
  
同样，如果存在多个任务，若其中某个任务执行时间过长, 会导致其他的任务的实效准确性出现问题。  
```java  
final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS", Locale.getDefault());  
Timer timer = new Timer();  
timer.schedule(new TimerTask() {  
    @Override  
    public void run() {  
        System.out.println("执行第一个任务  " + format.format(new Date()));  
        try {  
            Thread.sleep(2000);//第一个任务执行时间过长  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
    }  
}, 0);  
  
timer.schedule(new TimerTask() {  
    @Override  
    public void run() {  
        System.out.println("执行第二个任务  " + format.format(new Date()));//导致第二个任务的执行时间也受到影响  
    }  
}, 0);  
```  
  
打印结果：  
```  
执行第一个任务  2018.06.12 11:14:18 352  
执行第二个任务  2018.06.12 11:14:20 353  
```  
  
# 用 ScheduledExecutorService 替代 Timer  
## 可以规避 Timer 抛出异常缺陷  
```java  
ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(2);  
  
scheduExec.schedule(() -> {  
    System.out.println("执行第一个任务  " + System.currentTimeMillis());  
    throw new RuntimeException();//这里抛出未检测的RuntimeException  
}, 0, TimeUnit.MILLISECONDS);  
  
Thread.sleep(100);  
  
scheduExec.schedule(() -> {  
    System.out.println("执行第二个任务  " + System.currentTimeMillis());//第二个任务不会受到影响  
}, 0, TimeUnit.MILLISECONDS);  
```  
  
打印结果：  
```  
执行第一个TimerTask  1528771566751  
执行第二个TimerTask  1528771566852  
  
```  
## 可以规避 Timer 管理时间延迟缺陷  
```java  
final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS", Locale.getDefault());  
ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(2);  
  
scheduExec.schedule(() -> {  
    System.out.println("执行第一个任务  " + format.format(new Date()));  
    try {  
        Thread.sleep(2000);//第一个任务执行时间过长  
    } catch (InterruptedException e) {  
        e.printStackTrace();  
    }  
}, 0, TimeUnit.MILLISECONDS);  
  
scheduExec.schedule(() -> {  
    System.out.println("执行第二个任务  " + format.format(new Date()));//不会影响第二个任务的执行时间  
}, 0, TimeUnit.MILLISECONDS);  
```  
  
打印结果：  
```java  
执行第一个任务  2018.06.12 11:34:49 406  
执行第二个任务  2018.06.12 11:34:49 406  
```  
  
注意，上面使用 Timer 执行周期性任务时有说到：如果某个周期性任务的执行时间长度大于其周期时间长度，那么就会导致这一次的任务还在执行，而下一个周期的任务已经需要开始执行了，这样会导致越来越严重的时间延迟。  
这个问题对于 ScheduledExecutorService 同样存在，JDK文档中是这么说的：对于 scheduleAtFixedRate ，如果此任务的任何一个执行要花费比其周期更长的时间，则将推迟后续执行，但不会同时执行。  
  
2018-5-30  
