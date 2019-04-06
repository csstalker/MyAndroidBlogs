| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
内存泄漏 Memory Leaks 内存优化 MD  
***  
目录  
===  

- [什么是内存泄漏](#什么是内存泄漏)
- [为什么会发生内存泄漏](#为什么会发生内存泄漏)
- [发生内存泄漏的常见场景](#发生内存泄漏的常见场景)
	- [静态集合类的使用](#静态集合类的使用)
	- [Listener、Receiver等监听器的引用](#Listener、Receiver等监听器的引用)
	- [非静态内部类对外部类的引用](#非静态内部类对外部类的引用)
		- [Handler引起内存泄漏案例分析](#Handler引起内存泄漏案例分析)
		- [Thread引起内存泄漏案例分析](#Thread引起内存泄漏案例分析)
	- [单例模式初始化后的对象](#单例模式初始化后的对象)
	- [数据库、网络、io流等连接](#数据库、网络、io流等连接)
- [优化内存的方法](#优化内存的方法)
  
# 什么是内存泄漏  
内存管理一直是Java 所鼓吹的强大优点。开发者只需要简单地创建对象，而Java的垃圾收集器将会自动管理内存空间的分配和释放。  
但在很多情况下，事情并不那么简单，在 Java程序中总是会频繁地发生内存泄漏(Memory Leaks)。  
   
内存泄漏就是：当某些对象不再被应用程序所使用，但是由于仍然被引用而导致垃圾收集器不能释放他们。  
或者说是：我们对某一内存空间使用完成后没有释放。  
用白话来说就是：该回收的内存没被回收。  
   
要理解这个定义，我们需要理解内存中的对象状态。下图展示了什么是不使用的部分，以及未被引用的部分。  
![](index_files/461af037-4ea2-4258-a1f9-a0a2dcce9a4c.jpg)  
  
从图中可以看出，内存中存在着"有引用的对象"和"无引用的对象"。无引用的对象将被垃圾收集器所回收，而有引用的对象则不会被当做垃圾收集。   
  
因为没有任何其他对象所引用，所以无引用对象一定是不再使用的，但是有一部分无用对象仍然被(无意中)引用着。这就是发生内存泄漏的根源。  
  
# 为什么会发生内存泄漏  
让我们看下面的实例来了解为什么内存会泄漏。   
  
在下面的情境中，对象A引用了对象B。 A的生命周期(t1 - t4) 比 B的(t2 - t3)要长得多。 当对象B在应用程序逻辑中不会再被使用以后， 对象 A 仍然持有着 B的引用。 (根据虚拟机规范)在这种情况下垃圾收集器不能将 B 从内存中释放，这种情况很可能会引起内存问题。甚至有可能 B 也持有一大堆其他对象的引用，这些对象由于被 B 所引用，也不会被垃圾收集器所回收。所有这些无用的对象将消耗大量宝贵的内存空间。  
  
![](index_files/00579c9c-4b58-4f37-bc7f-7a2c0f406fbb.jpg)  
  
导致内存泄漏最主要的原因就是：某些长存对象持有了一些其它应该被回收的对象的引用，导致垃圾回收器无法去回收掉这些对象。  
  
# 发生内存泄漏的常见场景  
## 静态集合类的使用  
像HashMap、ArrayList等的使用最容易出现内存泄漏，由于静态变量的生命周期和应用程序一致，所以他们所引用的所有的对象也不能被释放。  
如  
```java  
public class Test {  
    static ArrayList<Person> list = new ArrayList<Person>();//list中引用的对象在应用整个生命周期中都不会被释放  
    public static void main(String[] args) {  
        for (int i = 1; i < 10; i++) {  
            Person person = new Person("" + i);  
            list.add(person);  
            person = null;//这行代码的作用仅仅是将【变量person】指向null(之前是指向堆内存中的Person对象)，但是这时堆内存中的【Person对象】并不会被回收，因为它被list强引用了  
        }  
        for (Person person : list) {  
            System.out.println(person);//所有Person对象都没有被回收！！！  
        }  
    }  
}  
```  
  
在这个例子中，如果仅仅释放引用本身(person = null)，那么集合仍然引用该对象，所以这个对象对GC来说是不可回收的。因此，如果对象加入到集合后，还必须从集合中删除(移除)，最简单的方法就是将集合对象设置为null。  
  
## Listener、Receiver等监听器的引用  
当注册的监听器不再使用后，如果没有被注销，那么很可能会发生内存泄漏。  
```java  
public class Test {  
    public static void main(String[] args) {  
        B b = new B();//被监听者，如Activity  
        A a = new A();//监听者，如BroadcastReceiver  
        b.register(a);//在b中注册【a监听器】，通常register内部的操作是：将a的引用传给b的成员变量，进而长期持有a的引用  
        a = null;//虽然将监听器a设为null，但是监听器对象A并没有被回收，因为它被B引用了  
        b.show();//监听器工作了  
        b.unregister();//用完要注销掉  
    }  
}  
  
interface Listener {  
    public void listen();  
}  
  
class A implements Listener {//监听者，如BroadcastReceiver  
    @Override  
    public void listen() {  
        System.out.println("监听器工作了");  
    }  
}  
  
class B {//被监听者，如Activity  
    Listener listener;  
    public void register(Listener listener) {//B持有了Listener的引用，除非在B内部将其设为null，否则listener将不会被回收  
        this.listener = listener;  
    }  
    public void unregister() {  
        listener = null;  
    }  
    public void show() {  
        if (listener != null) listener.listen();  
    }  
}  
```  
  
上述只是逻辑十分清楚、简单的监听，对于关系复杂的监听，会导致更多更严重的问题。比如当我们在Activity中使用了registerReceiver()方法注册了一个BroadcastReceiver，如果没在Activity的生命周期内调用unregisterReceiver()方法取消注册此BroadcastReceiver，由于BroadcastReceiver不止被Activity引用，还可能会被AMS等系统服务、管理器等引用，导致BroadcastReceiver无法被回收，而BroadcastReceiver中又持有着Activity的引用(即：onReceive方法中的参数Context)，会导致Activity也无法被回收(虽然Activity回调了onDestroy方法，但并不意味着Activity呗回收了)，从而导致严重的内存泄漏。  
  
## 非静态内部类对外部类的引用  
（非静态）内部类的引用是比较容易遗忘的一种，而且一旦没释放可能导致一系列的后继类对象没有释放。  
  
内部类的实现其实是通过编译器的语法糖（Syntactic sugar）实现的，通过生成相应的子类即以OutClassName$InteriorClassName命名的Class文件。并添加构造函数，在构造函数中【传入】外部类，这也是为什么内部类能使用外部类的方法与字段的原因。所以，当外部类与内部类生命周期不一致的时候很有可能发生内存泄漏。  
  
### Handler引起内存泄漏案例分析  
例如，当使用内部类（包括匿名类）来创建Handler的时候，Handler对象会隐式地持有一个外部类对象（通常是一个Activity）的引用。而Handler通常会伴随着一个耗时的后台线程一起出现，这个后台线程在任务执行完毕之后，通过消息机制通知Handler，然后Handler把图片更新到界面。  
  
然而，如果用户在网络请求过程中关闭了Activity，正常情况下，Activity不再被使用，它就有可能在GC检查时被回收掉，但由于这时线程尚未执行完，而该线程持有Handler的引用，这个Handler又持有Activity的引用，就导致该Activity无法被回收，直到网络请求结束。  
  
另外，如果你执行了Handler的postDelayed()方法，该方法会将你的Handler装入一个Message，并把这条Message推到MessageQueue中，那么在你设定的delay到达之前，会有一条MessageQueue -> Message -> Handler -> Activity的链，导致你的Activity被持有引用而无法被回收。  
  
**Handler完整方案示例**  
首先：将内部类声明为静态内部类（通用方法）  
  
在Java 中，非静态的内部类和匿名内部类都会隐式地持有其外部类的引用，静态的内部类不会持有外部类的引用。如果你想使用外部类的话，可以通过软引用或弱引用的方式保存外部类的引用。  
  
静态类不持有外部类的对象，所以你的Activity可以随意被回收。由于Handler不再持有外部类对象的引用，导致程序不允许你在Handler中操作Activity中的对象了。所以你需要在Handler中增加一个对Activity的弱引用（WeakReference）。  
  
对于Handler，还可以通过程序逻辑来进行保护  
- 在关闭Activity的时候停掉你的后台线程。线程停掉了，就相当于切断了Handler和外部连接的线，Activity自然会在合适的时候被回收。  
- 如果你的Handler是被delay的Message持有了引用，那么使用相应的Handler的removeCallbacks()方法，把消息对象从消息队列移除就行了。  
  
```java  
public class MainActivity extends Activity {  
    private Handler mHandler = new MyHandler(this);  
    public TextView textView;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        textView = new TextView(this);  
        textView.setText("包青天");  
        setContentView(textView);  
        mHandler.sendMessageDelayed(Message.obtain(), 2000);  
    }  
      
    private static class MyHandler extends Handler {  
        private WeakReference<MainActivity> mWeakReference;  
        public MyHandler(MainActivity activity) {  
            mWeakReference = new WeakReference<MainActivity>(activity);  
        }  
        @Override  
        public void handleMessage(Message msg) {  
            MainActivity activity = mWeakReference.get();  
            if (activity != null) activity.textView.setText("静态内部类的Handler");  
        }  
    }  
      
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);  
    }  
}  
```  
  
### Thread引起内存泄漏案例分析  
包括Timer和TimerTask导致内存泄露、动画造成内存泄露  
  
例如在一个Activity启动一个Thread执行一个任务，因为Thread是内部类持有了Activity的引用，当Activity销毁的时候如果Thread的任务没有执行完成，造成Activity的引用不能被释放从而引起内存泄漏。  
  
这种情况下可以通过声明一个静态内部类来解决问题，从反编译中可以看出，声明为static的内部类不会持有外部类的引用。此时，如果你想在静态内部类中使用外部类的话，可以通过软引用的方式保存外部类的引用。  
```java  
/**  
 * 测试非静态内部类导致内存泄漏的问题  
 */  
public class MemoryLeaksActivity extends Activity {  
    TextView textView;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        textView = new TextView(this);  
        setContentView(textView);  
        String startTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS", Locale.getDefault()).format(new Date());  
        textView.setText("开始休息 " + startTime);  
          
        //匿名内部类，如果在Activity销毁前线程的任务还未完成，将导致Activity的内存资源无法回收，造成内存泄漏  
        new Thread(() -> {  
            SystemClock.sleep(1000 * 5);  
            String endTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS", Locale.getDefault()).format(new Date());  
            Log.i("bqt", "【结束休息】" + endTime);//即使Activity【onDestroy被回调了】，这条日志仍会打出来  
            //runOnUiThread(() -> textView.append("\n结束休息 " + endTime));  
        }).start();  
    }  
      
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        Log.i("bqt", "【onDestroy被回调了】");  
    }  
}  
```  
  
解决办法就是使用静态内部类，如下：  
```java  
/**  
 * 测试使用静态内部类避免导致内存泄漏  
 */  
public class MemoryLeaksActivity extends Activity {  
    TextView textView;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        textView = new TextView(this);  
        setContentView(textView);  
        String startTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS", Locale.getDefault()).format(new Date());  
        textView.setText("开始休息 " + startTime);  
          
        new MyThread(this).start();  
    }  
      
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        Log.i("bqt", "【onDestroy被回调了】");  
    }  
      
    //静态内部类  
    private static class MyThread extends Thread {  
        SoftReference<Activity> context;  
          
        MyThread(Activity activity) {  
            context = new SoftReference<>(activity);  
        }  
          
        @Override  
        public void run() {  
            SystemClock.sleep(1000 * 15);  
            String endTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS", Locale.getDefault()).format(new Date());  
            Log.i("bqt", "【结束休息】" + endTime);//即使Activity【onDestroy被回调了】，这条日志仍会打出来  
            if (context.get() != null) {  
                context.get().runOnUiThread(() -> Toast.makeText(context.get(), "结束休息", Toast.LENGTH_SHORT).show());  
            }  
        }  
    }  
}  
```  
  
## 单例模式初始化后的对象  
单例对象在被初始化后将在JVM的整个生命周期中存在（以静态变量的方式），如果单例对象持有外部对象的引用，那么这个外部对象将不能被jvm正常回收，导致内存泄漏。  
  
## 数据库、网络、io流等连接  
各种连接，比如数据库连接(cursor 游标)、网络连接(socket)和io流的连接，除非显式的调用了其close()方法将其连接关闭，否则是不会自动被GC回收的。  
  
# 优化内存的方法  
**小心使用static**  
减少不必要的全局变量，尽量避免static成员变量引用资源耗费过多的实例  
比如Context。因为Context的引用超过它本身的生命周期，会导致Context泄漏，所以尽量使用Application这种Context类型。   
  
**Cursor（游标）回收**  
Cursor是Android查询数据库后得到的一个管理数据集合的类，在使用结束以后，应该保证Cursor占用的内存被及时的释放掉，而不是等待GC来处理。  
Android明显是倾向于编程者手动的将Cursor close掉，因为在源代码中我们发现，如果等到垃圾回收器来回收时，会给用户以错误提示。  
  
**Receiver（接收器）回收**  
当我们Activity中使用了registerReceiver()方法注册了BroadcastReceiver，一定要在Activity的生命周期内调用unregisterReceiver()方法取消注册 。  
通常我们可以重写Activity的onDestory()方法，在onDestory里进行unregisterReceiver操作  
  
**Stream/File（流/文件）回收**  
主要针对各种流，文件资源等等，如：InputStream/OutputStream，SQLiteOpenHelper，SQLiteDatabase，Cursor，文件，I/O，Bitmap图片等操作等都应该记得显示关闭。  
  
**避免创建不必要的对象**  
最常见的例子就是当你要频繁操作一个字符串时，使用StringBuffer代替String。  
还比如：使用int数组而不是Integer数组。  
尽量避免创建短命的临时对象，因为减少对象的创建就能减少垃圾收集。  
  
**避免内部Getters/Setters**  
在Android中，虚方法调用的代价比直接字段访问高昂许多。通常根据面向对象语言的实践，在公共接口中使用Getters和Setters是有道理的，但在一个字段经常被访问的类中宜采用直接访问。  
  
2017-8-24  
