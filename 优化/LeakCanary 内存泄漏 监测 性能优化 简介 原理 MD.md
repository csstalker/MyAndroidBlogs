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

- [介绍](#介绍)
	- [简单使用](#简单使用)
	- [更多介绍](#更多介绍)
	- [原理分析](#原理分析)
	- [自定义 LeakCanary](#自定义-LeakCanary)
- [测试案例](#测试案例)
	- [Application](#Application)
	- [MainActivity](#MainActivity)
	- [静态成员导致的内存泄漏](#静态成员导致的内存泄漏)
	- [单例导致的内存泄漏](#单例导致的内存泄漏)
- [使用LeakCanary检测内存泄露中文翻译](#使用LeakCanary检测内存泄露中文翻译)
	- [介绍 (0:00)](#介绍-000)
	- [内存泄漏：非技术讲解 (1:40)](#内存泄漏：非技术讲解-140)
	- [LeakCanary 救援 (3:47)](#LeakCanary-救援-347)
	- [技术讲解内存泄漏 (8:06)](#技术讲解内存泄漏-806)
	- [分析堆 (10:16)](#分析堆-1016)
	- [LeakCanary 救你于水火 (12:04)](#LeakCanary-救你于水火-1204)
	- [LeakCanary API 演练 (13:32)](#LeakCanary-API-演练-1332)
	- [什么是弱引用 (14:17)](#什么是弱引用-1417)
	- [HAHA 内存分析器 (16:55)](#HAHA-内存分析器-1655)
	- [LeakCanary 的实现 (19:19)](#LeakCanary-的实现-1919)
	- [Debug 一个真实的例子 (22:12)](#Debug-一个真实的例子-2212)
	- [忽略 SDK Crashes (28:10)](#忽略-SDK-Crashes-2810)
	- [LeakCanary 的未来 (29:14)](#LeakCanary-的未来-2914)
	- [Q&A (31:50)](#Q&A-3150)
  
# 介绍  
  
A `memory leak detection` `内存泄露检测` library for Android and Java.    
> A small leak will sink a great ship. -- Benjamin Franklin    
> 千里之堤， 毁于蚁穴。 -- 《韩非子·喻老》  
  
## 简单使用   
  
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
  
## 更多介绍  
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
  
## 原理分析  
[参考](https://www.jianshu.com/p/1e7e9b576391)  
  
JVM如何判定一个对象是垃圾对象？  
> JVM采用图论的可达遍历算法来判定一个对象是否是垃圾对象，如果对象A是可达的，则认为该对象是被引用的，GC不会回收；如果对象A或者块B(多个对象引用组成的对象块)是不可达的，那么该对象或者块则判定是不可达的垃圾对象，GC会回收。  
  
内存泄漏的检测机制：  
> LeakCanary通过ApplicationContext统一注册监听的方式，来`监察所有的Activity生命周期`，并在Activity的`onDestroy`时，执行`RefWatcher的watch方法`，该方法的作用就是检测本页面内是否存在内存泄漏问题。  
  
Activity检测机制是什么？  
> 通过application.registerActivityLifecycleCallbacks来绑定Activity生命周期的监听，从而监控所有Activity; 在Activity执行onDestroy时，开始检测当前页面是否存在内存泄漏，并分析结果。因此，如果想要在不同的地方都需要检测是否存在内存泄漏，需要手动添加。  
  
检测的流程：  
- 移除不可达引用，如果当前引用不存在了，则不继续执行  
- 手动触发GC操作，gcTrigger中封装了gc操作的代码   
- 再次移除不可达引用，如果引用不存在了，则不继续执行  
- 如果两次判定都没有被回收，则开始分析这个引用，最终生成HeapDump信息  
  
原理：  
-  弱引用与ReferenceQueue联合使用，如果弱引用关联的对象被回收，则会把这个弱引用加入到ReferenceQueue中；通过这个原理，可以看出removeWeaklyReachableReferences()执行后，会对应删除KeyedWeakReference的数据。如果这个引用继续存在，那么就说明没有被回收。  
-  为了确保最大保险的判定是否被回收，一共执行了两次回收判定，包括一次手动GC后的回收判定。两次都没有被回收，很大程度上说明了这个对象的内存被泄漏了，但并不能100%保证；因此LeakCanary是存在极小程度的误差的。  
  
内存泄漏检测机制是什么？  
> KeyedWeakReference与ReferenceQueue联合使用，在弱引用关联的对象被回收后，会将引用添加到ReferenceQueue；清空后，可以根据是否继续含有该引用来判定是否被回收；判定回收， 手动GC, 再次判定回收，采用双重判定来确保当前引用是否被回收的状态正确性；如果两次都未回收，则确定为泄漏对象。  
  
总结下流程就是  
- 判定是否回收（KeyedWeakReference是否存在该引用）， Y -> 退出， N -> 向下执行  
- 手动触发GC  
- 判定是否回收， Y -> 退出， N-> 向下执行  
- 两次未被回收，则分析引用情况：  
    - humpHeap :  这个方法是生成一个文件，来保存内存分析信息   
    - analyze: 执行分析  
  
内存泄漏的轨迹生成机制：  
> LeakCanary采用了MAT对内存信息进行分析，并生成结果。其中在分析时，分为findLeakingReference与findLeakTrace来查找泄漏的引用与轨迹，根据GCRoot开始按树形结构依次生成当前引用的轨迹信息。  
  
## 自定义 LeakCanary    
![](index_files/fbb48311-dead-401c-9753-b235b817e47b.png)    
  
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
  
![](index_files/bbdafc73-40ea-4a9f-b2d0-cec2afc45cc5.png)    
![](index_files/69dc6510-3ad1-4411-b692-88c2a83b97ce.png)    
  
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
  
![](index_files/9d0415a0-3bd0-4af7-a332-0efa53751f95.jpg)    
![](index_files/9766f992-779e-42b6-84f8-5ed3e94a608e.png)    
  
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
  
# 使用LeakCanary检测内存泄露中文翻译  
[原文](https://academy.realm.io/cn/posts/droidcon-ricau-memory-leaks-leakcanary/)    
  
Nov 18 2015    
  
我们的 App 曾经遇到很多的内存泄漏导致 `OutOfMemoryError` 的崩溃，一些甚至是在生产环境。Square 的 Pierre-Yvews Ricau 开发了 `LeakCanary` 最终解决了这些问题。`LeakCanary` 是一个帮助你检测和修复内存泄漏的工具。在这个分享中，Pierre 教授大家如何修复内存泄漏的错误，让你的 App 更稳定和可靠。  
  
## 介绍 (0:00)  
大家好，我是 Pierre-Yvews Ricau (叫我 PY 就行)，现在在 Square 工作。  
  
Square 出了一款名为：Square Register 的 App， 帮助你用移动设备完成支付。在用这个 App 的时候，用户先要登陆他的个人账号。  
  
不幸的是，在签名页面有的时候会因为内存溢出而出现崩溃。老实说，这个崩溃来的太不是时候了 — 用户和商家都无法确认交易是否完成了，更何况是在和钱打交道的时候。我们也强烈的意识到，我们需要处理下内存溢出或者内存泄露这种事情了。  
  
## 内存泄漏：非技术讲解 (1:40)  
我想要聊的内存泄露解决方案是： LeakCanary。  
  
    LeakCanary 是一个可以帮助你发现和解决内存泄露的开源工具。  
  
但是到底什么是内存泄露呢？我们从一个非技术角度来开始，先来举个例子。    
...    
有外部的引用指向了本不应该再指向的对象。类似这样的小规模的内存泄露堆积以后就会造成大麻烦。  
  
## LeakCanary 救援 (3:47)  
这就是我们为什么要开发 LeakCanary。  
  
我现在可能已经清楚了 可被回收的 Android 对象应该及时被销毁。  
  
但是我还是没法清楚的看到这些对象是否已经被回收掉。有了 LeakCanary 以后，我们:  
  
    给可被回收的 Android 对象上打了智能标记，智能标记能知道他们所指向的对象是否被成功释放掉。  
      
如果过一小段时间对象依然没有被释放，他就会给内存做个快照。LeakCanary `随后`会把结果发布出来:  
  
    帮助我们查看到内存到底怎么泄露了，并清晰的向我们展示那些无法被释放的对象的引用链。  
  
举个具体的例子：在我们的 Square App 里的签名页面。用户准备签名的时候，App 因为内存溢出出错崩溃了。我们不能确认内存错误到底出在哪儿了。  
  
签名页面持有了一个很大的有用户签名的 Bitmap 图片对象。图片的大小和用户手机屏幕大小一致 — 我们猜测这个有可能会造成内存泄露。首先，我们可以配置 Bitmap 为 alpha 8-bit 来节省内存。这是很常见的一种修复方案，而且效果也不错。但是并没有彻底解决问题，只是减少了泄露的内存总量。但是内存泄露依然在哪儿。  
  
最主要的问题是我们 App 的堆满了，应该要留有足够的空间给我们的签名图片，但是由于很多处的内存泄露叠加在一起占用了很多内存。  
  
## 技术讲解内存泄漏 (8:06)  
假设，我有一个 App，这个 App 点一下就能买一个法棍面包。  
```java  
private static Button buyNowButton;  
```  
由于某种原因，我把这个 button 设置成了 `static` 的。问题随之而来：  
  
    这个按钮除非你设置成了null，不然就内存泄露了！  
  
你也许会说：“只是一个按钮而已，没啥大不了”。问题是这个按钮还有一个成员变量：叫 `mContext`，这个东西指向了一个 `Acitvity`，Acitivty 又指向了一个 `Window`，Window 又拥有整个 View 继承树。算下来，那可是一大段的内存空间。  
  
静态的变量是 [GC root](https://www.yourkit.com/docs/java/help/gc_roots.jsp) 类型的一种。垃圾回收器会尝试回收所有`非 GC root 的对象`，或者某些`被 GC root 持有的对象`。所以如果你创建一个对象，并且移除了这个对象的所有指向，他就会被回收掉。但是一旦你将一个对象设置成 GC root，那他就不会被回收掉。  
  
当你看到类似“法棍按钮”的时候，很显然这个按钮持有了一个 Activity 的引用，所以我们必须清理掉它。当你沉浸在你的代码的时候，你肯定很难发现这个问题。你可能只看到了引出的引用。你可以知道 Activity 引用了一个 Window，但是谁引用了 Activity？  
  
你可以用像 `IntelliJ` 这样的工具做些分析，但是它并不会告诉你所有的东西。通常，你可以把这些 Object 的引用关系组织成图，但是是个单向图。  
  
## 分析堆 (10:16)  
我们能做些什么呢？我们来做个快照。  
  
    我们拿出所有的内存然后导出到文件里，这个文件会被用来分析和解析堆结构。  
    其中一个工具叫做 Memory Analyzer，也叫 MAT。  
    它会通过 dump 的内存，然后分析所有存活在内存中的对象和类。  
  
你可以用 SQL 对他做些查询，类似如下：  
  
    SELECT * FROM INSTANCEOF android.app.Activity a WHERE a.mDestroyed = true  
  
这条语句会返回所有的状态为 `destroyed` 的实例。一旦你发现了泄露的 Activity，你可以执行 `merge_shortest_paths` 的操作来`计算出最短的 GC root 路径`。从而找出阻止你 Acitivty 释放的那个对象。  
  
之所以说要 “最短路径”，是因为通常从一个 GC root 到 Acitivty，有很多条路径可以到达。比如说：我的按钮的 `parent view`，同样也持有一个 mContext 对象。  
  
当我们看到内存泄露的时候，我们通常不需要去查看所有的这些路径。我们只需要最短的一条。那样的话，我们就排除了噪音，很快的找到问题所在。  
  
## LeakCanary 救你于水火 (12:04)  
有 MAT 这样一个帮我们发现内存泄露的工具是个很棒的事情。但是在一个正在运行的 App 的上下文中，我们很难像我们的用户发现泄露那样发现问题所在。我们不能要求他们在做一遍相同操作，然后留言描述，再把 `70MB+` 的文件发回给我们。我们可以在后台做这个，但是并不 Cool。我们期望的是，`我们能够尽早的发现泄露`，比如在我们开发的时候就发现这些问题。这也是 LeakCanary 诞生的意义。  
  
一个 Activity 有自己生命周期。你了解它是如何被创建的，如何被销毁的，你期望他会`在 onDestroy() 函数调用后，回收掉你所有的空闲内存`。如果你有一个`能够检测一个对象是否被正常的回收掉了的工具`，那么你就会很惊讶的喊出：“这个可能造成内存泄露！它本该被回收掉,但却没有被垃圾回收掉！”  
  
Activity 无处不在。很多人都把 Activity 当做神级 Object 一般的存在，因为它可以操作 Services，文件系统等等。经常会发生对象泄漏的情况，如果泄漏对象还持有 context 对象，那 context 也就跟着泄漏了。  
```java  
Resources resources = context.getResources();  
LayoutInflater inflater = LayoutInflater.from(context);  
File filesDir = context.getFilesDir();  
InputMethodManager inputMethodManager =(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);  
```  
  
## LeakCanary API 演练 (13:32)  
我们回过头来再看看智能标记`smart pin`，我们希望知道的是当生命后期结束后，发生了什么。幸运的时，LearkCanary有一个很简单的 API。  
  
第一步：创建 `RefWatcher`。这里的Ref 其实是 `Reference` 的缩写。给 RefWatcher 传入一个对象的实例，它会检测这个对象是否被成功释放掉。  
```java  
RefWatcher refWatcher = LeakCanary.install(this);  
```  
  
第二步：监听 Activity 生命周期。然后，当 onDestroy 被调用的时候，我们传入 Activity。  
```java  
refWatcher.watch(this);// Make sure you don’t get installed twice.  
```  
  
## 什么是弱引用 (14:17)  
想要了解这个是怎么工作的，我得先跟大家聊聊弱引用`Weak References`。  
  
我刚才提到过静态域的变量会持有Activity 的引用。所以刚才说的“下单”按钮就会持有 mContext 对象，导致 Activity 无法被释放掉。这个被称作强引用`Strong Reference`。  
  
    一个对象可以有很多的强引用，在垃圾回收过程中，当这些强引用的个数总和为零的时候，垃圾回收器就会释放掉它。  
    弱引用就是一种不增加引用总数的持有引用方式，垃圾回收期是否决定要回收一个对象，只取决于它是否还存在强引用。  
  
所以说，如果我们：  
  
    将我们的 Activity 持有为弱引用，一旦我们发现弱引用持有的对象已经被销毁了，那么这个 Activity 就已经被垃圾回收器回收了。  
    否则，那可以大概确定这个 Activity 已经被泄露了。  
  
弱引用的主要目的是为了做 Cache，而且非常有用。主要就是告诉 GC，尽管我持有了这个对象，但是如果一旦没有对象在用这个对象的时候，GC 就可以在需要的时候销毁掉。  
  
在下面的例子中，我们继承了 WeakReference：  
```java  
final class KeyedWeakReference extends WeakReference<Object> {  
    public final String key; //唯一标识符  
    public final String name;  
      
    KeyedWeakReference(Object referent, String key, String name, ReferenceQueue<Object> referenceQueue) {  
        super(checkNotNull(referent, "referent"), checkNotNull(referenceQueue, "referenceQueue"));  
        this.key = checkNotNull(key, "key");  
        this.name = checkNotNull(name, "name");  
    }  
}  
```  
你可以看到，我们给弱引用添加了一个 Key，这个 Key 是一个唯一字符串。想法是这样的：当我们解析一个`heap dump`文件的时候，我们可以遍历所有的 `KeyedWeakReference` 实例，然后找到对应的 Key。  
  
首先，我们创建一个 weakReference，然后我们写入『一会儿，我需要检查弱引用』。（尽管一会儿可能就是几秒后）。当我们调用 watch 函数的时候，其实就是发生了这些事情。  
```java  
public void watch(Object watchedReference, String referenceName) {  
   checkNotNull(watchedReference, "watchedReference");  
   checkNotNull(referenceName, "referenceName");  
   if (debuggerControl.isDebuggerAttached()) return;  
   final long watchStartNanoTime = System.nanoTime();  
   String key = UUID.randomUUID().toString();  
   retainedKeys.add(key);  
   final KeyedWeakReference reference = new KeyedWeakReference(watchedReference, key, referenceName, queue);  
     
   watchExecutor.execute(() -> ensureGone(reference, watchStartNanoTime));  
}  
```  
  
在这一切的背后，我们调用了 System.GC (免责声明— 我们本不应该去做这件事情)。然而，这是一种告诉垃圾回收器：『Hey，垃圾回收器，现在是一个不错的清理垃圾的时机。』，然后我们再检查一遍，如果发现有些对象依然存活着，那么可能就有问题了。我们就要触发 `heap dump` 操作了。  
  
## HAHA 内存分析器 (16:55)  
亲手做 `heap dump` 是件超酷的事情。当我亲手做这些的时候，花了很多时间和功夫。我每次都是做相同的操作：  
  
    下载 heap dump 文件，在内存分析工具里打开它，找到实例，然后计算最短路径。  
  
但是我很懒，我根本不想一次次的做这个。（我们都很懒对吧，因为我们是开发者啊！）  
  
我本可以为内存分析器写一个 Eclipse 插件，但是 Eclipse 插件机制太糟糕了。后来我灵机一动，我其实可以把某个 Eclipse 的插件，移除 UI，利用它的代码。  
  
HAHA 是一个`无 UI Android 内存分析器`。基本上就是把另一个人写的代码重新打包。开始的时候，我就是 fork 了一份别的代码然后移除了UI部分。两年前，有人重新 fork 了我的代码，然后添加了 Android 支持。又过了两年，我才发现这个人的仓储，然后我又重新打包上传到了 `maven center`。  
  
我最近根据 `Android Studio` 修改了代码实现。代码还说的过去，还会继续维护。  
  
## LeakCanary 的实现 (19:19)  
我们有自己的库去解析 `heap dump` 文件，而且实现的很容易。我们打开 `heap dump`，加载进来，然后解析。然后我们根据 key 找到我们的引用。然后我们根据已有的 Key 去查看拥有的引用。我们拿到实例，然后得到对象图，再反向推导发现泄漏的引用。  
  
    以上(下)所有的工作都发生在 Android 设备上。  
  
- 当 LeakCanary 探测到一个 Activity 已经被销毁掉，而没有被垃圾回收器回收掉的时候，它就会强制导出一份 `heap dump` 文件存在磁盘上。  
- 然后开启`另外一个进程`去分析这个文件得到内存泄漏的结果。如果在同一进程做这件事的话，可能会在尝试分析堆内存结构的时候而发生内存不足的问题。  
- 最后，你会得到一个通知，点击一下就会展示出`详细的内存泄漏链`。而且还会展示出`内存泄漏的大小`，你也会很明确自己解决掉这个内存泄漏后到底能够解救多少内存出来。  
  
LeakCanary 也是支持 API 的，这样你就`可以添加内存泄漏的回调`，比方说可以`把内存泄漏问题传到服务器上`。  
用上 API 以后，我们的程序崩溃率降低了 94%！简直棒呆！  
  
## Debug 一个真实的例子 (22:12)  
这个是 Android 4年前的一次代码修改留下的问题，当时是为了修复另一个 bug，然而带来了无法避免的内存泄漏。我们也不知道何时能被修复。  
  
## 忽略 SDK Crashes (28:10)  
通常来说，总是有些内存泄漏是你无法修复的。我们某些时候需要忽略掉这些无法修复的内存泄漏提醒。在 LeakCanary 里，有`内置的方法`去忽略无法修复的问题。  
  
我想要重申一下，LeakCanary 只是一个开发工具。不要将它用到生产环境中。一旦有内存泄漏，就会展示一个通知给用户，这一定不是用户想看到的。  
  
我们即便用上了 LeakCanary 依然有内存溢出的错误出现。我们的内存泄露依然有多个。有没有办法改变这些呢？  
  
## LeakCanary 的未来 (29:14)  
```java  
public class OomExceptionHandler implements Thread.UncaughtExceptionHandler {  
    private final Thread.UncaughtExceptionHandler defaultHandler;  
    private final Context context;  
      
    public OomExceptionHandler(Thread.UncaughtExceptionHandler defaultHandler, Context context) {...}  
      
    @Override  
    public void UncaughtException(Thread thread, Throwable ex) {  
        if (containsOom(ex)) {  
            File heapDumpFile = new File(context.getFilesDir(), "out-of-memory.hprof");  
            Debug.dumpHprofData(heapDumpFile.getAbsolutePath());  
        }  
        defaultHandler.uncaughtException(thread, ex);  
    }  
      
    private boolean containsOom(Throwable ex) {...}  
}  
```  
这是一个 Thread.UncaughtExceptionHandler，你可以将线程崩溃委托给它，它会导出 heap dump 文件，并且在另一个进程里分析内存泄漏情况。  
  
有了这个以后，我们就能做一些好玩儿的事情了，比如：列出所有的应该被销毁却依然在内存里存活的 Activity，然后列出所有的 Detached View。我们可以依此来为泄漏的内存按重要性排序。    
  
我实际上已经有一个很简单的 Demo 了，是我在飞机上写的。还没有发布，因为还有些问题，最严重的问题是没有足够的内存去解析 heap dump 文件。想要修复这个问题，得想想别的办法。比如采用 stream 的方法去加载文件等等。  
  
## Q&A (31:50)  
Q: LeakCanary 能用于 Kotlin 开发的 App?    
PY: 我不知道，但是应该是可以的，毕竟到最后他们都是字节码，而且 Kotlin 也有引用。  
  
Q:你们是在 Debug 版本一直开启 LeakCanary 么？还是只在最后的某些版本开启做做测试    
PY： 不同的人有不同的方法，我们通常是一直都开着的。  
  
> 备注：这篇文章是2015年作者视频内容的中文翻译，有一些内容可能已经改变了。  
  
2018-10-2  
