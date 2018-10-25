| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
LeakCanary 内存泄漏 监测 性能优化 简介 原理 MD    
GitHub：https://github.com/square/leakcanary    
Demo地址：https://github.com/baiqiantao/LeakCanaryTest.git    
***  
目录  
===  

- [简单使用](#简单使用)
- [更多介绍](#更多介绍)
- [自定义 LeakCanary](#自定义-LeakCanary)
- [测试案例](#测试案例)
	- [Application](#Application)
	- [MainActivity](#MainActivity)
	- [静态成员导致的内存泄漏](#静态成员导致的内存泄漏)
	- [单例导致的内存泄漏](#单例导致的内存泄漏)
  
# 简单使用   
A `memory leak detection` `内存泄露检测` library for Android and Java.    
> A small leak will sink a great ship. -- Benjamin Franklin    
> 千里之堤， 毁于蚁穴。 -- 《韩非子·喻老》  
  
添加依赖:    
```groovy  
dependencies {  
  debugImplementation 'com.squareup.leakcanary:leakcanary-android:1.6.1'  
  debugImplementation 'com.squareup.leakcanary:leakcanary-support-fragment:1.6.1' //当使用support库时添加  
  releaseImplementation 'com.squareup.leakcanary:leakcanary-android-no-op:1.6.1' //发布时用的是无任何操作的版本  
}  
```  
  
初始化:    
```java  
LeakCanary.install(application);  
```  
  
配置完以后，在 `debug` 构建的版本中，如果检测到某个 `activity 或 fragment` 有内存泄露，`LeakCanary` 就会自动地显示一个通知。    
  
# 更多介绍  
- 为什么要使用LeakCanary  
>     
    - 内存泄漏是一种编程错误`[programming error]`，会导致应用程序保留对不再需要的对象的引用。因此就会导致无法回收为该对象分配`[allocated]`的内存，最终导致 OutOfMemoryError crash。     
    - 例如，Android Activity 实例在调用 onDestroy 方法后就不再需要了，但是如果在静态字段中存储了对该Activity的强引用将会阻止其被GC`[garbage collected]`。    
    - LeakCanary对一个 longer needed 的对象做了唯一标识，并找到阻止它被垃圾回收的引用链。    
    - 当作者首次在Square公司的某款App中启用 LeakCanary 后，他找到并修复了多个内存泄漏，并将 OutOfMemoryError 的崩溃率降低了94％。  
  
- LeakCanary是怎么工作的  
>     
    - 通过 `RefWatcher.watch()` 创建了一个`KeyedWeakReference` to the watched object。  
    - 然后在后台线程检查引用是否被清除了，如果没有，则`triggers a GC`。  
    - 如果引用还是未被清除，则 dumps the heap 到文件系统中的 .hprof 文件中。  
    - 在另外一个独立的进程中启动 `HeapAnalyzerService` ，`HeapAnalyzer` 使用 [HAHA](https://github.com/square/haha) 解析 heap dump 。  
    - 得益于唯一的 `reference key`, `HeapAnalyzer` 在 heap dump 中找到 `KeyedWeakReference`，并且定位 leaking reference。  
    - `HeapAnalyzer` 计算到 `GC roots` 的最短强引用路径，并确定是否有泄露。如果有的话，创建导致泄露的引用链。  
    - 计算结果传递到 APP 进程中的 `DisplayLeakService` 中， 并以通知的形式展示出来。  
  
- 如何修复内存泄漏  
> 要修复某个内存泄漏，您需要查看该链并查找导致泄漏的那个引用，即在泄漏时哪个引用本应该被清除的。LeakCanary以红色下划线突出显示可能导致泄漏的引用。  
  
- 如何复制 leak trace 信息  
> 可以通过 logcat 或通过 Leaks App 的菜单复制·  
  
- Android SDK可能导致泄漏吗  
> 是。 在AOSP以及制造商实现中，已经存在许多已知的内存泄漏。 当发生这样的泄漏时，作为应用程序开发人员，您几乎无法解决此问题。    
> 出于这个原因，LeakCanary 有一个内置的已知Android漏洞列表可供忽略：AndroidExcludedRefs.java。  
  
- 如何通过 leak trace 挖掘泄漏信息  
>     
有时 leak trace 是不够的，您需要使用 [MAT](http://eclipse.org/mat/) 或 [YourKit](https://www.yourkit.com/) 挖掘 heap dump。 以下是在堆转储中找到泄漏实例的方法：  
    - 查找 `com.squareup.leakcanary.KeyedWeakReference` 的所有实例  
    - 对于其中的每一个，请查看 `key` 字段。  
    - 找到 key 字段等于 LeakCanary 报告的 the reference key 的 `KeyedWeakReference`。  
    - 找到的那个 KeyedWeakReference 的 `referent` 字段就是您内存泄漏的对象。  
    - 此后，问题就掌握在你手中。A good start 是查看 `GC Roots的最短路径`（除了弱引用）。  
  
- 如何修复构建错误  
>     
    - 如果leakcan-android不在Android Studio的 external libraries 列表中，但是 leakcanary-analyzer 和 leakcanary-watcher 却存在在那里：尝试做一个 Clean Build。 如果仍然存在问题，请尝试通过命令行构建。    
    - error: package com.squareup.leakcanary does not exist: 如果您有其他 build types 而不是 debug 和 release，则还需要为这些构建类型添加特定的依赖项(xxxCompile)。  
  
- LeakCanary添加了多少个方法  
>     
    - 如果您使用`ProGuard`，答案为9或0。  
    - LeakCanary 只应在调试版本中使用，并一定要在发布版本中禁用。我们为您的发布版本提供了一个特殊的空依赖项：`leakcanary-android-no-op`。  
    - LeakCanary 的完整版本更大，绝不应在您的 release 版本中发布。  
  
- 谁在推动 LeakCanary  
> LeakCanary由 [@pyricau](https://github.com/pyricau)创建并开源，目前由[@jrodbx](https://github.com/jrodbx)，[@JakeWharton](https://github.com/JakeWharton)和[@pyricau](https://github.com/pyricau)维护。  
  
- 为什么叫 LeakCanary  
> LeakCanary这个名称是参考 煤矿中的金丝雀`[canary in a coal mine]`，因为LeakCanary是一个用于通过提前预警危险`[advance warning of a danger]`来检测风险`[detect risks]`的哨兵`[sentinel]`。  
  
- Instant Run可能触发无效的 leaks  
> 启用Android Studio的即时运行功能可能会导致LeakCanary报告无效的内存泄漏。 请参阅 Android Issue Tracker 上的问题＃37967114(https://issuetracker.google.com/issues/37967114)。  
  
- 我知道我有泄漏，为什么通知不显示    
> 你是否 attached to a debugger？ LeakCanary在调试时忽略泄漏检测以避免误报。  
  
# 自定义 LeakCanary    
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181002004618226.png)    
  
- 如何观察具有生命周期的对象  
> 在您的应用程序中，您可能有其他具有生命周期的对象，例如Fragment，Service，Dagger组件等。可以使用RefWatcher来监视应该进行垃圾回收的实例：`refWatcher.watch(schrodingerCat);`  
  
- 使用 no-op 依赖    
> release 版本的`leakcanary-android-no-op`依赖项仅包含`LeakCanary和RefWatcher`类。如果您要自定义LeakCanary，您需要确保自定义仅出现在 debug 版本中，因为它可能会引用`leakcanary-android-no-op`依赖项中不存在的类。  
  
- 自定义图标和标签    
> `DisplayLeakActivity`附带了一个默认图标和标签，您可以通过在应用中提供`R.mipmap.leak_canary_icon`和`R.string.leak_canary_display_activity_label`来更改它：  
  
- install 方法的默认逻辑    
```java  
public static RefWatcher install(Application application) {  
   return refWatcher(application)  
      .listenerServiceClass(DisplayLeakService.class)  
      .excludedRefs(AndroidExcludedRefs.createAppDefaults().build())  
      .buildAndInstall();  
}  
```  
  
- 不监测特定的Activity    
默认情况下，LeakCanary会监视所有的Activity。 您可以自定义 installation steps 以执行不同的操作，例如忽略某种类型Activity的泄漏：  
```java  
RefWatcher refWatcher = LeakCanary.refWatcher(this)  
      .watchActivities(false)  
      .buildAndInstall();  
```  
```java  
registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {  
   @Override  
   public void onActivityDestroyed(Activity activity) {  
      if (activity instanceof IgnoreActivity) {  
         return;  
      }  
      refWatcher.watch(activity);  
   }  
   //...  
});  
```  
  
- 在运行时打开和关闭 LeakCanary    
```java  
refWatcher = LeakCanary.refWatcher(this)  
      .heapDumper(getHeapDumper()) //在运行时开启和关闭LeakCanary  
      .buildAndInstall();  
```  
```java  
public TogglableHeapDumper getHeapDumper() {  
    if (heapDumper == null) {  
        LeakDirectoryProvider leakDirectoryProvider = LeakCanaryInternals.getLeakDirectoryProvider(this);  
        AndroidHeapDumper defaultDumper = new AndroidHeapDumper(this, leakDirectoryProvider);  
        heapDumper = new TogglableHeapDumper(defaultDumper);  
    }  
    return heapDumper;  
}  
```  
```java  
public class TogglableHeapDumper implements HeapDumper {  
    private final HeapDumper defaultDumper;  
    private boolean enabled = true;  
      
    public TogglableHeapDumper(HeapDumper defaultDumper) {  
        this.defaultDumper = defaultDumper;  
    }  
      
    public void setEnabled(boolean enabled) {  
        this.enabled = enabled;  
    }  
      
    @Override  
    public File dumpHeap() {  
        return enabled ? defaultDumper.dumpHeap() : HeapDumper.RETRY_LATER;  
    }  
}  
```  
```java  
MyApplication.app().getHeapDumper().setEnabled(false);  
```  
  
# 测试案例  
## Application  
```java  
public class MyApplication extends Application {  
    private RefWatcher refWatcher;  
    private static MyApplication app;  
    private TogglableHeapDumper heapDumper;  
      
    @Override  
    public void onCreate() {  
        super.onCreate();  
        if (LeakCanary.isInAnalyzerProcess(this)) {  
            Log.i("bqt", "此进程是专用于LeakCanary进行堆分析用的。您不应该在此进程中初始化您的应用。");  
            return;  
        }  
          
        refWatcher = LeakCanary.refWatcher(this)  
                .watchActivities(true)  //默认为true，会监视所有Activity，你可以设置为false然后再指定要监测的Activity  
                .watchFragments(true) //默认为true，会监视 native Fragment，如果添加了support依赖，则也会监视support中的Fragment  
                .watchDelay(1, TimeUnit.SECONDS) //设置应该等待多长时间，直到它检查跟踪对象是否已被垃圾回收  
                .maxStoredHeapDumps(7) //设置LeakCanary最多可以保存的 heap dumps 个数，默认为7  
                .excludedRefs(getExcludedRefs()) //忽略特定的引用，这个垃圾东西设置后总是不生效  
                .heapDumper(getHeapDumper()) //在运行时开启和关闭LeakCanary  
                //.listenerServiceClass() //可以更改默认行为以将 leak trace 和 heap dump 上载到您选择的服务器。  
                .buildAndInstall();  
        app = this;  
    }  
      
    private ExcludedRefs getExcludedRefs() {  
        return AndroidExcludedRefs.createAppDefaults()//经过大量测试，我感觉TMD完全忽略不了Activity和Fragment中内存泄漏  
                .instanceField("com.bqt.test.Single", "imageView") //类名，字段名  
                .staticField("com.bqt.test.StaticLeakActivity", "bitmap") //类名，静态字段名  
                .clazz("com.bqt.test.StaticLeakActivity") //忽略提供的类名的所有子类的所有字段和静态字段  
                .thread("Thread-10086") //忽略指定的线程，一般主线程名为【main】，子线程名为【Thread-整数】  
                .build(); //忽略的引用如果又通过watch手动监测了，则仍会监测其内存泄漏情况  
    }  
      
    public static MyApplication app() {  
        return app;  
    }  
      
    public RefWatcher getRefWatcher() {  
        return refWatcher;  
    }  
      
    public TogglableHeapDumper getHeapDumper() {  
        if (heapDumper == null) {  
            LeakDirectoryProvider leakDirectoryProvider = LeakCanaryInternals.getLeakDirectoryProvider(this);  
            AndroidHeapDumper defaultDumper = new AndroidHeapDumper(this, leakDirectoryProvider);  
            heapDumper = new TogglableHeapDumper(defaultDumper);  
        }  
        return heapDumper;  
    }  
}  
```  
  
## MainActivity  
```java  
public class MainActivity extends FragmentActivity implements AdapterView.OnItemClickListener {  
    private FrameLayout frameLayout;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        ListView listView = new ListView(this);  
        String[] array = {"静态成员导致的内存泄漏",  
                "单例导致的内存泄漏：Fragment",  
                "禁用 LeakCanary",  
                "",};  
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
        listView.setOnItemClickListener(this);  
        frameLayout = new FrameLayout(this);  
        frameLayout.setId(R.id.fragment_id);  
        listView.addFooterView(frameLayout);  
        setContentView(listView);  
    }  
      
    @Override  
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  
        switch (position) {  
            case 0:  
                startActivity(new Intent(this, StaticLeakActivity.class));  
                break;  
            case 1:  
                getSupportFragmentManager().beginTransaction()  
                        .add(frameLayout.getId(), new SingleLeakFragment(), "SingleLeakFragment")  
                        .commit();  
                break;  
            case 2:  
                MyApplication.app().getHeapDumper().setEnabled(false);  
                break;  
            default:  
                break;  
        }  
    }  
}  
```  
  
## 静态成员导致的内存泄漏  
```java  
public class StaticLeakActivity extends Activity {  
    private static Bitmap bitmap;  
      
    @Override  
    protected void onCreate(@Nullable Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        ImageView imageView = new ImageView(this);  
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);  
        imageView.setImageBitmap(bitmap);  
        setContentView(imageView);  
    }  
}  
```  
  
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181002003344366.png)    
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181002003420791.png)    
  
相关信息：  
```java  
* com.bqt.test.StaticLeakActivity has leaked:  
* InputMethodManager$ControlledInputConnectionWrapper.!(mParentInputMethodManager)!  
* ↳ InputMethodManager.!(mLastSrvView)!  
* ↳ PhoneWindow$DecorView.mContext  
* ↳ StaticLeakActivity  
* Reference Key: 7f96d2f1-bf17-47e2-84ad-cd5976d72766  
* Device: HUAWEI HONOR PLK-UL00 PLK-UL00  
* Android Version: 6.0 API: 23 LeakCanary: 1.6.1 26145bf  
* Durations: watch=1007ms, gc=149ms, heap dump=1840ms, analysis=6567ms  
```  
  
## 单例导致的内存泄漏  
```java  
public class SingleLeakFragment extends Fragment {  
    private ImageView imageView;  
      
    @Nullable  
    @Override  
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {  
        imageView = new ImageView(getContext());  
        return imageView;  
    }  
      
    @Override  
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {  
        super.onViewCreated(view, savedInstanceState);  
        imageView.setImageResource(R.drawable.icon);  
        Single.SINGLETON.setImageView(imageView);//单例中引用View同样会导致Activity内存泄漏  
    }  
}  
```  
```java  
public enum Single {  
    @SuppressLint("StaticFieldLeak")  
    SINGLETON; //定义一个枚举的元素，它就代表了Single的一个实例  
    private ImageView imageView;  
      
    public void setImageView(ImageView imageView) {  
        this.imageView = imageView;  
    }  
}  
```  
  
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181002004006523.png)    
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181002004113713.png)    
  
相关信息：  
```java  
* com.bqt.test.SingleLeakFragment has leaked:  
* InputMethodManager$ControlledInputConnectionWrapper.!(mParentInputMethodManager)!  
* ↳ InputMethodManager.!(mLastSrvView)!  
* ↳ ListView.mOnItemClickListener  
* ↳ MainActivity.mFragments  
* ↳ FragmentController.mHost  
* ↳ FragmentActivity$HostCallbacks.mFragmentManager  
* ↳ FragmentManagerImpl.mAdded  
* ↳ ArrayList.array  
* ↳ array Object[].[0]  
* ↳ SingleLeakFragment  
* Reference Key: 4877bf10-596c-440f-b69c-5d239f670944  
* Device: HUAWEI HONOR PLK-UL00 PLK-UL00  
* Android Version: 6.0 API: 23 LeakCanary: 1.6.1 26145bf  
* Durations: watch=17245ms, gc=138ms, heap dump=1675ms, analysis=8159ms  
```  
  
2018-10-2  
