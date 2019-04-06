| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
服务 IntentService 工作线程 stopself MD  
***  
目录  
===  

- [介绍](#介绍)
- [测试](#测试)
	- [IntentService 源码解析【重要】](#IntentService-源码解析【重要】)
	- [MyIntentService](#MyIntentService)
	- [MainActivity](#MainActivity)
	- [测试案例](#测试案例)
		- [启动一次](#启动一次)
		- [启动一次后立即结束](#启动一次后立即结束)
		- [连续启动两次](#连续启动两次)
		- [连续启动两次后立即结束](#连续启动两次后立即结束)
- [Service 的 stopself 方法详解](#Service-的-stopself-方法详解)
  
# 介绍  
Google 为方便开发者使用，提高开发效率，封装了 IntentService 和 HandlerThread。HandlerThread 继承自 Thread，内部封装了 Looper。  
  
IntentService 是继承自 Service 并处理异步请求的一个类，在 IntentService 内有一个工作线程来处理耗时操作，当任务执行完后，IntentService 会自动停止，不需要我们去手动结束。如果启动 IntentService 多次，那么每一个耗时操作会以工作队列的方式在 IntentService 的 onHandleIntent 回调方法中执行，依次去执行，执行完自动结束。  
  
同一个IntentService只会新建一个线程，因为其采用的就是类似主线程的机制。  
  
IntentService 中使用的 Handler、Looper、MessageQueue 机制把消息发送到线程中去执行的，所以多次启动 IntentService 不会重新创建新的服务和新的线程，只是把消息加入消息队列中等待执行，而如果服务停止，会清除消息队列中的消息，后续的事件得不到执行。  
  
**IntentService 类**  
IntentService是Service的基类，可根据需要处理异步请求（表示为Intents）。客户端通过`Context#startService(Intent)`调用发送请求; 根据需要启动服务，使用工作线程依次处理每个Intent，并在工作完成时自行停止。  
> IntentService is a base class for Services that handle asynchronous requests (expressed as Intents) on demand. Clients send requests through Context#startService(Intent) calls; the service is started as needed, handles each Intent in turn using a worker thread, and stops itself when it runs out of work.  
  
这种“工作队列处理器”模式通常用于从应用程序的主线程卸载任务。IntentService类的存在就是为了简化此模式并处理机制。要使用它，请扩展IntentService并实现onHandleIntent(Intent)。 IntentService将接收Intents，启动一个工作线程，并根据需要停止服务。  
> This "work queue processor" pattern is commonly used to offload tasks from an application's main thread.  The IntentService class exists to simplify this pattern and take care of the mechanics. To use it, extend IntentService and implement onHandleIntent(Intent). IntentService will receive the Intents, launch a worker thread, and stop the service as appropriate.  
  
所有请求都在一个工作线程上处理 - 它们可能需要运行很长时间（并且不会阻止应用程序的主循环），但一次只能处理一个请求。  
> All requests are handled on a single worker thread -- they may take as long as necessary (and will not block the application's main loop), but only one request will be processed at a time.  
  
**onHandleIntent 方法**  
在工作线程上调用此方法并请求处理。一次只处理一个Intent，但处理过程发生在独立于其他应用程序逻辑运行的工作线程上。因此，如果此代码需要很长时间，它将阻止对同一个IntentService的其他请求，但它不会阻止其他任何内容。处理完所有请求后，IntentService会自行停止，因此您不应该调用 stopSelf。  
> This method is invoked on the worker thread with a request to process. Only one Intent is processed at a time, but the processing happens on a worker thread that runs independently from other application logic. So, if this code takes a long time, it will hold up other requests to the same IntentService, but it will not hold up anything else. When all requests have been handled, the IntentService stops itself, so you should not call stopSelf.  
  
# 测试  
[参考](https://www.jianshu.com/p/332b6daf91f0)  
  
最佳应用场景：后台队列按顺序异步下载、下载完毕后自动结束服务！  
  
## IntentService 源码解析【重要】  
  
IntentService是一种特殊的Service，它继承于Service并且还是个抽象类，所以我们必须创建它的子类才能使用IntentService。IntentService可以用来执行后台任务，当任务执行完后就会“自杀”。因为它自己也是个服务，所以优先级高于普通的线程，不容易被系统所干掉，所以IntentService比较适合执行优先级较高的后台任务。  
  
handleMessage的逻辑：  
- 收到消息后，会将Intent对象传递给onHandlerIntent方法去处理。注意这个Intent对象的内容和外界Activity中startService(intent)中的intent的内容是完全一致的，通过这个intent对象可以得到外界启动IntentService时所传递的参数，通过这些参数我们就可以区分不同的业务逻辑，这样onHandlerIntent就可以对不同的逻辑做出不同的操作了。  
- 因为我们的handler发送的消息都是通过looper顺序的取到，所以意味着IntentService是顺序的执行后台任务的，当有多个任务同时存在时，这些后台任务会按照我们代码发起的先后顺序排队执行的。  
- 当onHandlerIntent方法执行结束后，IntentService会通过stopSelf(int startId)方法尝试停止服务。之所以使用stopSelf(int startId)而不是stopSelf()来停止，是因为stopSelf()会马上停止服务，但是有可能还有消息未处理；stopSelf(int startId)则会等所有的消息都处理完后才销毁自己。  
> 一般来说的话，stopSelf(int startId)在停止之前会判断最近启动服务的次数和startId是不是相等的，如果相等就立刻停止，如果不相等说明还有别的消息没处理，就不停止服务，具体的要看AMS中的stopServiceToken方法的实现。  
  
onCreate的逻辑：  
- 当我们的IntentService第一次启动的时候，onCreate方法会执行一次，onCreate方法里创建了一个HandlerThread。HandlerThread继承Thread，它是一种可以使用Handler的Thread，它的run方法里通过Looper.prepare()来创建消息队列，并通过Looper.loop()来开启消息循环，所以就可以在其中创建Handler了。  
- 这里我们通过HandlerThread得到一个looper对象，并且使用它的looper对象来构造一个Handler对象，就是我们上面看到的那个，这样做的好处就是通过mServiceHandler发出的消息都是在HandlerThread中执行，所以从这个角度来看，IntentService是可以执行后台任务的。  
  
onStart的逻辑：  
- 通过handler发送了一个消息，把我们的intent对象和startId发送出去，在我们上面的handleMessage()会接收到消息，并且通过onHandlerIntent()方法将对象回调给子类。  
- 每次启动IntentService，onStartCommand()就会被调用一次，在这个方法里处理每个后台任务的intent，可以看到在这个方法里调用的是上方的onStart()方法。  
  
```java  
public abstract class IntentService extends Service {  
    private volatile Looper mServiceLooper; //HandlerThread的looper，这个looper是用于创建Handler的  
    private volatile ServiceHandler mServiceHandler; //通过looper创建的一个Handler对象，消息都是通过handler发送的【重要】  
    private String mName;  
    private boolean mRedelivery;  
      
    //通过HandlerThread线程中的looper对象构造的一个Handler对象，可以看到这个handler中主要就是处理消息  
    //并且将我们的onHandlerIntent方法回调出去，然后停止任务，销毁自己  
    private final class ServiceHandler extends Handler {  
        public ServiceHandler(Looper looper) {  
            super(looper);  
        }  
          
        @Override  
        public void handleMessage(Message msg) {  
            Log.i("bqt", "开始onHandleIntent");  
            onHandleIntent((Intent) msg.obj); //将消息处理的逻辑转移到了Service的抽象方法中  
            Log.i("bqt", "结束onHandleIntent，尝试stopSelf");  
            stopSelf(msg.arg1);//处理完毕后自动调用stopSelf结束服务，注意如果此时还有未执行完的message则不会结束【重要】  
        }  
    }  
      
    public IntentService(String name) {  
        super();  
        mName = name; //Used to name the worker thread, important only for debugging.  
    }  
      
    public void setIntentRedelivery(boolean enabled) {  
        mRedelivery = enabled;  
    }  
      
    @Override  
    public void onCreate() {  
        // TODO: It would be nice to have an option to hold a partial wakelock 如果有一个部分唤醒锁的选项会更好  
        super.onCreate();  
        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]"); //另一个重要的类出现了【重要】  
        thread.start();  
          
        mServiceLooper = thread.getLooper();  
        mServiceHandler = new ServiceHandler(mServiceLooper);  
    }  
      
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        onStart(intent, startId); //You should not override this method for your IntentService. Instead, override onHandleIntent  
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;  
    }  
      
    @Override  
    public void onStart(Intent intent, int startId) {  
        //每启动一次onStart方法，就会把消息数据发给mServiceHandler，相当于发送了一次Message消息给HandlerThread的消息队列  
        //mServiceHandler会把数据传给onHandleIntent方法，所以每次onStart之后都会调用我们重写的onHandleIntent方法去处理数据  
        Message msg = mServiceHandler.obtainMessage();  
        msg.arg1 = startId;  
        msg.obj = intent;  
        mServiceHandler.sendMessage(msg);  
    }  
      
    @Override  
    public void onDestroy() {  
        //因为looper是无限循环轮询消息的一个机制，所以当我们明确不需要继续使用的话，那么我们就应该通过它的quit()方法来终止它的执行  
        mServiceLooper.quit(); //曾经有面试官问我怎么结束looper的循环，其实就是调用这个方法  
    }  
      
    @Override  
    public IBinder onBind(Intent intent) {  
        return null; //Unless you provide binding for your service, you don't need to implement this method  
    }  
      
    //这就是IntentService中定义的抽象方法，具体交由它自己的子类来实现  
    protected abstract void onHandleIntent(Intent intent);  
}  
```  
  
## MyIntentService  
```java  
public class MyIntentService extends IntentService {  
      
    public MyIntentService() {  
        super("包青天的工作线程");  
    }  
      
    @Override  
    protected void onHandleIntent(Intent intent) {//注意，因为这里是异步操作，所以这里不能直接使用Toast。  
        //所有请求的Intent记录会按顺序加入到【队列】中并按顺序【异步】执行，并且每次只会执行【一个】工作线程  
        //当所有任务执行完后IntentService会【自动】停止  
        Log.i("bqt", "onHandleIntent");  
        try {  
            int count = 0;  
            sendThreadStatus("线程启动", count);  
            Thread.sleep(500);  
            while (count <= 100) {  
                count++;  
                Thread.sleep(30);  
                sendThreadStatus("线程运行中...", count);  
            }  
            sendThreadStatus("线程结束", count);  
            Thread.sleep(500);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
    }  
      
    @Override  
    public void onCreate() {  
        super.onCreate();  
        Log.i("bqt", "onCreate");  
        sendServiceStatus("服务启动");  
    }  
      
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.i("bqt", "onStartCommand");  
        return super.onStartCommand(intent, flags, startId);  
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        Log.i("bqt", "onDestroy");  
        sendServiceStatus("服务结束");  
    }  
      
    // 发送服务状态信息  
    private void sendServiceStatus(String status) {  
        Intent intent = new Intent(MainActivity.ACTION_TYPE_SERVICE);  
        intent.putExtra("status", status);  
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);  
    }  
      
    // 发送线程状态信息  
    private void sendThreadStatus(String status, int progress) {  
        Intent intent = new Intent(MainActivity.ACTION_TYPE_THREAD);  
        intent.putExtra("status", status);  
        intent.putExtra("progress", progress);  
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);  
    }  
}  
```  
  
## MainActivity  
```java  
public class MainActivity extends ListActivity {  
      
    public static final String ACTION_TYPE_SERVICE = "com.bqt.type.service";  
    public static final String ACTION_TYPE_THREAD = "com.bqt.type.thread";  
      
    ProgressBar progressBar;  
    TextView tvServiceStatus;  
    TextView tvThreadStatus;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"启动 IntentService",  
            "结束 IntentService",  
            "",};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
          
        tvServiceStatus = new TextView(this);  
        tvThreadStatus = new TextView(this);  
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);  
        progressBar.setIndeterminate(false);  
        progressBar.setMax(100);  
        getListView().addFooterView(tvServiceStatus);  
        getListView().addFooterView(tvThreadStatus);  
        getListView().addFooterView(progressBar);  
          
        IntentFilter intentFilter = new IntentFilter();  
        intentFilter.addAction(ACTION_TYPE_SERVICE);  
        intentFilter.addAction(ACTION_TYPE_THREAD);  
        LocalBroadcastManager.getInstance(this).registerReceiver(new MyBroadcastReceiver(), intentFilter);  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                startService(new Intent(this, MyIntentService.class));// 像启动 Service 那样启动 IntentService  
                break;  
            case 1:  
                stopService(new Intent(this, MyIntentService.class));  
                break;  
            case 2:  
                  
                break;  
        }  
    }  
      
    class MyBroadcastReceiver extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            switch (intent.getAction()) {  
                case ACTION_TYPE_SERVICE:  
                    tvServiceStatus.setText("服务状态：" + intent.getStringExtra("status"));  
                    break;  
                case ACTION_TYPE_THREAD:  
                    int progress = intent.getIntExtra("progress", 0);  
                    tvThreadStatus.setText("线程状态：" + intent.getStringExtra("status"));  
                    progressBar.setProgress(progress);  
                    break;  
            }  
        }  
    }  
}  
```  
  
## 测试案例  
  
### 启动一次  
点击［启动 IntentService］，服务运行，线程计数完成后，服务和线程都结束。  
```  
15:18:45.488: onCreate  
15:18:45.489: onStartCommand  
15:18:45.489: onStart  
15:18:45.489: 开始onHandleIntent  
15:18:45.489: onHandleIntent        //【开始在工作线程执行任务】  
15:18:49.651: 结束onHandleIntent，尝试stopSelf        //【线程执行完毕后调用stopSelf结束任务】  
15:18:49.659: onDestroy        //【服务成功被结束】  
```  
  
### 启动一次后立即结束  
点击［启动 IntentService］，服务运行，线程计数完成前，点击［停止 IntentService］，服务结束，线程计数完成后线程结束。  
```  
15:43:17.569: onCreate  
15:43:17.570: onStartCommand  
15:43:17.570: onStart  
15:43:17.570: 开始onHandleIntent  
15:43:17.570: onHandleIntent  
  
15:43:18.895: onDestroy        //【调用stopService会立即结束服务，但工作线程并不会结束，而会正常的执行】  
  
15:43:21.724: 结束onHandleIntent，尝试stopSelf        //【线程正常执行完毕，此前服务已经结束了】  
```  
  
### 连续启动两次  
点击两次［启动 IntentService］，服务运行，第一次线程计数完成后，进行第二次线程计数，两次完成后，服务和线程都结束。  
```  
14:36:30.794: onCreate  
14:36:30.795: onStartCommand  
14:36:30.795: 开始onHandleIntent  
14:36:30.796: onHandleIntent  
  
14:36:32.218: onStartCommand        //【在执行第一个任务时启动第二个任务】  
  
14:36:34.986: 结束onHandleIntent，尝试stopSelf  //【因为此startId不是最新的startId，所以服务结束失败】  
-----------------------------------------  
14:36:34.993: 开始onHandleIntent  
14:36:34.993: onHandleIntent  
14:36:39.178: 结束onHandleIntent，尝试stopSelf //【第二个任务完成时的startId是最新的startId，所以能成功结束服务】  
-----------------------------------------  
14:36:39.188: onDestroy        //【服务结束】  
```  
  
### 连续启动两次后立即结束  
点击两次［启动 IntentService］，服务运行，在第一次线程计数完成前，点击［停止 IntentService］，服务结束，第一次线程计数结束后不进行第二次计数。  
```  
16:34:05.052: onCreate  
16:34:05.054: onStartCommand  
16:34:05.054: onStart  
16:34:05.055: 开始onHandleIntent  
16:34:05.055: onHandleIntent  
  
16:34:05.463: onStartCommand  
16:34:05.463: onStart  
  
16:34:05.985: onDestroy        //【调用stopService会立即结束服务，但工作线程并不会结束，而会正常的执行】  
  
16:34:09.237: 结束onHandleIntent，尝试stopSelf        //【因为服务已经结束，所以不会再执行未开启的任务】  
```  
  
# Service 的 stopself 方法详解  
**结论**  
- `stopSelf()`：直接停止服务，和调用 `Context#stopService` 的效果完全一致。  
- `stopSelf(int startId)`：在其参数 startId 跟最后启动该 service 时生成的 ID 相等时才会执行停止服务。  
- `stopSelfResult(int startId)`：和 `stopSelf(int startId)` 的区别仅仅是有返回值，当成功停止服务时返回 true，否则返回 false。  
- 每次调用 startService 时 Service 都会重新赋值 startId 值，从1开始，如果 Service 没有被关闭，那这个值会顺序增加。  
- IntentService 在调用 `stopSelf(int startId)` 结束自己时，总是拿最新的 startId 和上一次传入的 startId 作比较，如果相等则结束自己，如果不相等则不结束，而IntentService 中维护着一个消息队列，要想结束自己必须等到消息队列没有消息了。  
  
**使用场景**  
如果同时有多个服务启动请求发送到`onStartCommand()`，不应该在处理完一个请求后调用stopSelf()，因为在调用此函数销毁service之前，可能 service 又接收到新的启动请求，如果此时 service 被销毁，新的请求将得不到处理。此情况应该调用`stopSelf(int startId)`，例如 IntentService 中，在执行完异步任务后，就是通过调用`stopSelf(int startId)`来结束服务的。  
  
**源码**  
先看看 `onStartCommand` 方法。  
每次通过 startService 方式启动服务时，都会调用 onStartCommand 方法，此方法中有一个 startId，这个 startId 代表启动服务的次数，也代表着某一特定启动请求。  
文档中明确说明了，这个 startId 其实就是在 stopSelfResult(int) 中用的。  
```java  
//startId：A unique integer representing this specific request to start. Use with stopSelfResult(int).  
public int onStartCommand(Intent intent, @StartArgFlags int flags, int startId) {  
    onStart(intent, startId);  
    return mStartCompatibility ? START_STICKY_COMPATIBILITY : START_STICKY;  
}  
```  
  
再看看 `stopSelf()` 方法，可以看到其只是调用了`stopSelf(-1)`。  
```java  
//Stop the service, if it was previously先前 started.   
//This is the same as calling Context#stopService for this particular特定 service.  
public final void stopSelf() {  
    stopSelf(-1);  
}  
```  
  
再看看 `stopSelf(int startId)` 方法，文档中没有什么解释，只说是旧版本中`stopSelfResult`的无返回值形式。  
```java  
//Old version of stopSelfResult that doesn't return a result.  
public final void stopSelf(int startId) {  
    if (mActivityManager == null) return;  
    try {  
        mActivityManager.stopServiceToken(new ComponentName(this, mClassName), mToken, startId);  
    } catch (RemoteException ex) {  
    }  
}  
```  
  
```java  
public final boolean stopSelfResult(int startId) {  
    if (mActivityManager == null) return false; //停止失败  
    try {  
        return mActivityManager.stopServiceToken(new ComponentName(this, mClassName), mToken, startId);  
    } catch (RemoteException ex) {  
    }  
    return false; //停止失败  
}  
```  
  
以下是文档中的详细描述：  
  
- 如果最近启动服务时的ID就是此startId，则停止服务。  
> Stop the service if the most recent time it was started was startId.   
  
- 这与对此特定服务调用Context＃stopService的效果相同，但如果有一个来自客户端的启动请求(在执行此方法时这个请求还在onStart中，所以你没发现这个请求)，则允许您安全地避免停止服务。  
> This is the same as calling Context#stopService for this particular service ,but allows you to safely avoid stopping ,if there is a start request from a client ,that you haven't yet seen in #onStart.   
  
- 要小心调用此函数后的顺序。  
> Be careful about ordering of your calls to this function.  
  
- 如果在为之前收到的ID调用之前使用最近收到的ID调用此函数，则无论如何都会立即停止该服务。  
> If you call this function with the most-recently received ID before you have called it for previously received IDs, the service will be immediately stopped anyway.    
  
- 如果您最终可能无序处理ID（例如通过在不同的线程上调度它们），那么您有责任按照收到它们的顺序停止它们。  
> If you may end up processing IDs out of order (such as by dispatching them on separate threads), then you are responsible for stopping them in the same order you received them.  
  
参数与返回值：  
- startId：The most recent start identifier received in  #onStart. onStart中收到的最新启动标识符  
- Returns true if the startId matches the last start request and the service will be stopped, else false.  
  
  
2019-2-13  
