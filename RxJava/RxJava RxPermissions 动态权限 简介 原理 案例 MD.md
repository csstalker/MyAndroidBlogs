| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
RxJava RxPermissions 动态权限 简介 原理 案例 MD    
[GitHub](https://github.com/tbruyelle/RxPermissions)   
[Demo地址](https://github.com/baiqiantao/RxJavaDemo.git)    
***   
目录   
===   

- [基本使用步骤](# 基本使用步骤)
- [注意事项和优点](# 注意事项和优点)
- [API](# api)
- [测试案例](# 测试案例)
- [原理](# 原理)
	- [构造方法](# 构造方法)
	- [request 方法](# request-方法)
	- [申请权限的方法](# 申请权限的方法)
	- [申请结果回调方法](# 申请结果回调方法)
  
# 基本使用步骤   
RxPermissions是基于 RxJava 开发的用于帮助在 Android 6.0 中处理运行时权限检测的框架。   
  
1、配置   
```   
allprojects {   
    repositories {   
        maven { url 'https://jitpack.io' }   
    }   
}   
```   
```   
implementation 'com.github.tbruyelle:rxpermissions:0.10.2'   
```   
  
2、创建 RxPermissions 实例   
```java   
final RxPermissions rxPermissions = new RxPermissions(this); // where this is an Activity or Fragment instance   
```   
  
> 注意：this 参数可以是 FragmentActivity 或 Fragment。如果你在 fragment 中使用 RxPermissions，你应该传递 fragment 实例作为构造函数参数而不是 fragment.getActivity()，否则你可能抛出异常 java.lang.IllegalStateException: FragmentManager is already executing[执行]    
  
3、请求权限，必须在初始化阶段比如 onCreate 中调用   
```java   
// Must be done during an initialization phase like onCreate   
rxPermissions.request(Manifest.permission.CAMERA)   
    .subscribe(granted -> {   
        if (granted) { // Always true pre-M   
           // I can control the camera now   
        } else {   
           // Oups permission denied   
        }   
    });   
```   
  
If you need to trigger触发 the permission request from a specific event特定事件, you need to setup your event as an observable inside an initialization phase初始化阶段.   
```java   
RxView.clicks(view)   
    .compose(new RxPermissions(this).ensure(Manifest.permission.CAMERA))   
    .subscribe(granted -> Toast.makeText(this, granted ? "已赋予权限" : "已拒绝权限", Toast.LENGTH_SHORT).show());   
```   
  
# 注意事项和优点   
注意：    
如上所述，由于您的应用程序可能在权限请求期间重新启动，因此请求必须在初始化阶段完成。这可能是 Activity.onCreate 或View.onFinishInflate，但不能在 onResume 等 pausing 方法中，因为由于您请求的 activity 在权限请求期间被框架暂停了，因此您可能会创建一个无限的请求循环。   
  
如果没有，并且如果您的应用程序在权限请求期间重新启动了（例如，由于配置更改），则用户的响应将永远不会发送给订阅者。    
你可以在这里找到更多相关细节。    
  
优点    
- 不需要担心框架的版本。 如果sdk是 pre-M，则观察者将自动接收授予的结果。   
- 您不需要拆分权限请求和结果处理之间的代码。目前，如果没有这个库，你必须在一个地方请求权限，并在 Activity.onRequestPermissionsResult() 中处理结果。   
- 所有RX提供的转换、过滤、链接[transformation, filter, chaining]......   
  
  
# API   
构造方法   
```java   
RxPermissions(FragmentActivity activity) //不可以是普通 Activity ，因为他的原理是通过 FragmentManager 操作 Fragment 实现的   
RxPermissions(Fragment fragment)   
```   
请求权限   
```java   
Observable<Boolean> request(String... permissions)   
<T> ObservableTransformer<T, Boolean> ensure(final String... permissions) //与 compose 操作符一起使用   
```   
观察详细的结果   
```java   
Observable<Permission> requestEach(String... permissions) //申请了多少个权限就回调多少次，相当于 flatMap 的效果   
Observable<Permission> requestEachCombined(String... permissions) //只回调一次，回调对象的的属性值是多个原始对象的组合值   
<T> ObservableTransformer<T, Permission> ensureEach(final String... permissions)   
<T> ObservableTransformer<T, Permission> ensureEachCombined(final String... permissions)   
```   
其他方法   
```java   
boolean isGranted(String permission)  //是否已赋予权限   
void setLogging(boolean logging)   
Observable<Boolean> shouldShowRequestPermissionRationale(Activity activity, String... permissions)   
```   
  
# 测试案例   
入口Activity   
```java   
public class RxPermissionsActivity extends ListActivity {   
       
    protected void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);   
        String[] array = {"最简单实用的案例，request",   
                "和 RxView 一起使用，compose + ensure",   
                "一次请求多个权限",   
                "观察详细的结果，requestEach、ensureEach",   
                "观察详细的结果，requestEachCombined 和 ensureEachCombined",   
                "shouldShowRequestPermissionRationale 和 isGranted 的使用 ",};   
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));   
    }   
       
    @Override   
    protected void onListItemClick(ListView l, View v, int position, long id) {   
        Intent intent = new Intent(this, RxPermissionsFragmentActivity.class);   
        intent.putExtra("type", position);   
        startActivity(intent);   
    }   
}   
FragmentActivity   
public class RxPermissionsFragmentActivity extends FragmentActivity {   
    private TextView view;   
       
    @Override   
    protected void onCreate(@Nullable Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);   
        view = new TextView(this);   
        view.setBackgroundColor(0xff00ff00);   
        setContentView(view);   
        int type = getIntent().getIntExtra("type", 0);   
        init(type);   
    }   
       
    private void init(int type) {   
        switch (type) {   
            case 0:   
                new RxPermissions(this)   
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)   
                        .subscribe(granted -> {   
                            if (granted) { //在 Android M(6.0) 之前始终为true   
                                Toast.makeText(this, "已赋予权限", Toast.LENGTH_SHORT).show();   
                            } else {   
                                Toast.makeText(this, "已拒绝权限", Toast.LENGTH_SHORT).show();   
                            }   
                        });   
                break;   
            case 1:   
                RxView.clicks(view)   
                        .compose(new RxPermissions(this).ensure(Manifest.permission.CAMERA))   
                        .subscribe(granted -> Toast.makeText(this, granted ? "已赋予权限" : "已拒绝权限", Toast.LENGTH_SHORT).show());   
                break;   
            case 2:   
                new RxPermissions(this)   
                        .request(Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS)//只有所有权限都赋予了才会回调成功   
                        .subscribe(granted -> Toast.makeText(this, granted ? "已赋予权限" : "已拒绝权限", Toast.LENGTH_SHORT).show());   
                RxView.clicks(view)   
                        .compose(new RxPermissions(this).ensure(Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS))   
                        .subscribe(granted -> Toast.makeText(this, granted ? "已赋予权限" : "已拒绝权限", Toast.LENGTH_SHORT).show());   
                break;   
            case 3:   
                RxView.clicks(view)   
                        .compose(new RxPermissions(this).ensureEach(Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS))   
                        .subscribe(permission -> {//申请了多少个权限就回调多少次，相当于 flatMap 的效果   
                            if (permission.granted) {   
                                Toast.makeText(this, "已赋予权限：" + permission.name, Toast.LENGTH_SHORT).show();   
                            } else if (permission.shouldShowRequestPermissionRationale) { //拒绝了权限，但是没有勾选"ask never again"   
                                Toast.makeText(this, "已拒绝权限：" + permission.name + " ，但可以继续申请", Toast.LENGTH_SHORT).show();   
                            } else {   
                                Toast.makeText(this, "已拒绝权限：" + permission.name + " ，并且不会再显示", Toast.LENGTH_SHORT).show();   
                            }   
                        });   
                break;   
            case 4:   
                RxView.clicks(view)   
                        .compose(new RxPermissions(this).ensureEachCombined(Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS))   
                        .subscribe(permission -> {//只回调一次，回调的 permission 的属性值是多个原始 permission 的组合值   
                            if (permission.granted) {   
                                Toast.makeText(this, "已赋予全部所需的权限：" + permission.name, Toast.LENGTH_SHORT).show();   
                            } else if (permission.shouldShowRequestPermissionRationale) { //拒绝了权限，但是没有勾选"ask never again"   
                                Toast.makeText(this, "有权限被拒绝：" + permission.name + " ，但可以继续申请", Toast.LENGTH_SHORT).show();   
                            } else {   
                                Toast.makeText(this, "有权限被拒绝：" + permission.name + " ，并且不会再显示", Toast.LENGTH_SHORT).show();   
                            }   
                        });   
                break;   
            case 5:   
                boolean isGranted = new RxPermissions(this).isGranted(Manifest.permission.CAMERA);   
                Toast.makeText(this, "是否已赋予权限：" + isGranted, Toast.LENGTH_SHORT).show();   
                   
                new RxPermissions(this)   
                        .shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS)   
                        .subscribe(granted -> Toast.makeText(this, granted ? "全部权限已授权，或有权限被拒绝但可以继续申请" :   
                                "有权限被拒绝，并且不会再显示", Toast.LENGTH_SHORT).show());   
                break;   
            default:   
                break;   
        }   
    }   
}   
```   
  
# 原理   
RxPermissions 就包含4个类，很明显 BuildConfig 与原理没任何关系，所以真正有用的是 Permission、RxPermissions、RxPermissionsFragment这三个：   
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-2018093014354624.png)   
Permission 是定义权限的 model类，用来存放权限名称以及是否获取权限的信息：   
```java   
public class Permission {   
    public final String name;   
    public final boolean granted;   
    public final boolean shouldShowRequestPermissionRationale;   
}   
```   
  
RxPermissions 就是最主要的类了，我们根据一个基本使用案例分析一下调用流程：   
```java   
new RxPermissions(this)   
    .request(Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS)//只有所有权限都赋予了才会回调成功   
    .subscribe(granted -> Toast.makeText(this, granted ? "已赋予权限" : "已拒绝权限", Toast.LENGTH_SHORT).show());   
```   
  
## 构造方法   
首先看构造方法的具体逻辑：   
```java   
public RxPermissions(@NonNull FragmentActivity activity) {   
    this.mRxPermissionsFragment = this.getLazySingleton(activity.getSupportFragmentManager());   
}   
```   
```java   
public RxPermissions(@NonNull Fragment fragment) {   
    this.mRxPermissionsFragment = this.getLazySingleton(fragment.getChildFragmentManager());   
}   
```   
  
在构造方法中初始化了成员 RxPermissionsFragment：   
```java   
@NonNull   
private RxPermissions.Lazy<RxPermissionsFragment> getLazySingleton(@NonNull final FragmentManager fragmentManager) {   
    return new RxPermissions.Lazy<RxPermissionsFragment>() {   
        private RxPermissionsFragment rxPermissionsFragment;   
   
        public synchronized RxPermissionsFragment get() {   
        if (this.rxPermissionsFragment == null) this.rxPermissionsFragment = RxPermissions.this.getRxPermissionsFragment(fragmentManager);   
        return this.rxPermissionsFragment;   
        }   
    };   
}   
```   
  
可以看到，其维护的是一个单例，如果已经存在则直接返回，否则通过 getRxPermissionsFragment 方法获取：   
```java   
static final String TAG = RxPermissions.class.getSimpleName();   
   
private RxPermissionsFragment getRxPermissionsFragment(@NonNull FragmentManager fragmentManager) {   
    RxPermissionsFragment rxPermissionsFragment = this.findRxPermissionsFragment(fragmentManager);   
    boolean isNewInstance = rxPermissionsFragment == null;   
    if (isNewInstance) {   
        rxPermissionsFragment = new RxPermissionsFragment();   
        fragmentManager.beginTransaction().add(rxPermissionsFragment, TAG).commitNow();   
    }   
    return rxPermissionsFragment;   
}   
```   
  
里面的逻辑很简单，如果已经存在则直接返回，否则如果是新创建的，则通过 FragmentManager 将其 add 到当前 FragmentActivity 或 Fragment 中。   
  
判断是否已存在的逻辑同样是通过 FragmentManager 来判断的：   
```java   
private RxPermissionsFragment findRxPermissionsFragment(@NonNull FragmentManager fragmentManager) {   
    return (RxPermissionsFragment)fragmentManager.findFragmentByTag(TAG);   
}   
```   
  
## request 方法   
再看看请求权限的具体逻辑：   
```java   
public Observable<Boolean> request(String... permissions) {   
    return Observable.just(TRIGGER).compose(this.ensure(permissions)); //【static final Object TRIGGER = new Object();】   
}   
```   
  
首先，利用 just 方法创建Observable对象，接下来会调用 compose 方法，compose 的参数的作用是将一个类型的 Observable 对象转换成另外一个类型的Observable对象，同时也可以对自身对象的一些重复操作进行封装，避免重复编写代码。我们看到，compose 方法中调用了我们可能用到的另一个方法 ensure ：   
```java   
public <T> ObservableTransformer<T, Boolean> ensure(final String... permissions) {   
   return new ObservableTransformer<T, Boolean>() {   
      public ObservableSource<Boolean> apply(Observable<T> o) {   
         return RxPermissions.this   
               .request(o, permissions) //核心步骤   
               .buffer(permissions.length)   
               .flatMap(new Function<List<Permission>, ObservableSource<Boolean>>() { /* 转换操作 */});   
      }   
   };   
}   
```   
  
上面代码的作用是传入一个 Observable<T> 对象，然后转换成一个 Observable<Boolean> 对象，同时 ensure 方法传入了 permissions 权限申请列表，在转换开始前，需要根据 permissions 权限列表去申请权限，具体操作在 request 方法中，下面看看 request 的实现：   
```java   
private Observable<Permission> request(Observable<?> trigger, final String... permissions) {   
   if (permissions != null && permissions.length != 0) {   
      return this.oneOf(trigger, this.pending(permissions)).flatMap(new Function<Object, Observable<Permission>>() {   
         public Observable<Permission> apply(Object o) {   
            return RxPermissions.this.requestImplementation(permissions); //核心步骤   
         }   
      });   
   } else throw new IllegalArgumentException("RxPermissions.request/requestEach requires at least one input permission");   
}   
```   
  
通过上面代码可以看出，首先会判断申请权限列表是否为空，如果为空就会抛出异常，然后通过 oneOf 方法和 pending 方法来创建合并 Observable 对象，下面看一下这两个方法   
```java   
private Observable<?> oneOf(Observable<?> trigger, Observable<?> pending) {   
    return trigger == null ? Observable.just(TRIGGER) : Observable.merge(trigger, pending);   
}   
private Observable<?> pending(String... permissions) {   
    String[] var2 = permissions;   
    int var3 = permissions.length;   
   
    for(int var4 = 0; var4 < var3; ++var4) {   
        String p = var2[var4];   
        if (!((RxPermissionsFragment)this.mRxPermissionsFragment.get()).containsByPermission(p)) {   
            return Observable.empty();   
        }   
    }   
    return Observable.just(TRIGGER);   
}   
private Map<String, PublishSubject<Permission>> mSubjects = new HashMap();   
   
public boolean containsByPermission(@NonNull String permission) {   
    return this.mSubjects.containsKey(permission);   
}   
```   
  
从以上的两个方法的代码可以看出，pending 主要是判断权限申请列表中是否全部都在 `Fragment` 中的 mSubjects 集合中：如果有一个不在集合中，则返回 `Observable.empty()`，如果已经全部在集合中，则返回`Observable.just(TRIGGER)`。然后在 oneOf 方法中根据 trigger 是否为 null 来判断是哪种返回。   
  
这两个方法在我看来最终并没有起到实际的作用，因为具体的请求权限是在 `requestImplementation(permissions)` 方法中实现的，而通过以上两个方法得到的 `Observable` 对象最终在 `flatMap` 操作转换时并不会用到它们的对象，而是直接根据权限申请列表 permissions 作为参数，直接调用 `requestImplementation` 方法进行权限的实际请求，所以接下来主要看看 `requestImplementation` 方法的具体实现：   
```java   
@TargetApi(23)   
private Observable<Permission> requestImplementation(String... permissions) {   
   List<Observable<Permission>> list = new ArrayList(permissions.length); //保存所有已经处理过的权限   
   List<String> unrequestedPermissions = new ArrayList();//保存所有需要申请的权限   
   String[] unrequestedPermissionsArray = permissions;   
   int var5 = permissions.length;   
      
   for (int var6 = 0; var6 < var5; ++var6) { //循环权限申请列表   
      String permission = unrequestedPermissionsArray[var6];   
      ((RxPermissionsFragment) this.mRxPermissionsFragment.get()).log("Requesting permission " + permission);   
      if (this.isGranted(permission)) {   
         list.add(Observable.just(new Permission(permission, true, false))); //如果权限已经具有，则直接保存到集合中   
      } else if (this.isRevoked(permission)) {    
         list.add(Observable.just(new Permission(permission, false, false))); //如果权限被拒绝，则将其作为申请被拒绝的权限保存到集合中   
      } else {   
         PublishSubject<Permission> subject = ((RxPermissionsFragment) this.mRxPermissionsFragment.get()).getSubjectByPermission(permission);   
         if (subject == null) { // 先查询 map 中是否已经存在了该 subject，如果还未存在，则创建一个 PublishSubject 对象，并保存起来   
            unrequestedPermissions.add(permission); //此为需要申请的权限   
            subject = PublishSubject.create();   
            ((RxPermissionsFragment) this.mRxPermissionsFragment.get()).setSubjectForPermission(permission, subject); //保存到 map 中   
         }   
         list.add(subject);//将新创建的需要申请的权限保存到集合中   
      }   
   }   
      
   if (!unrequestedPermissions.isEmpty()) { //在循环结束之后，如果有未申请的权限，则进行权限的申请操作   
      unrequestedPermissionsArray = (String[]) unrequestedPermissions.toArray(new String[unrequestedPermissions.size()]);   
      this.requestPermissionsFromFragment(unrequestedPermissionsArray); //权限的申请操作   
   }   
   return Observable.concat(Observable.fromIterable(list));   
}   
```   
上面的就是进行权限申请的具体代码，主要的操作就是定义一个集合保存保存一次申请的所有权限，无论这个权限是否已经申请，还是被撤销，还是未申请，最终都会保存到list这个集合中，这样，我们在后续的操作中，才可以进行转换。同时，定义另外一个集合，用于保存未申请的权限，然后在循环结束之后进行未申请权限的申请。   
  
## 申请权限的方法   
上面分析道，在遍历所有申请的权限状态结束后，调用 requestPermissionsFromFragment 进行未申请权限的申请，下面我们看一下具体流程。   
```java   
@TargetApi(23)   
void requestPermissionsFromFragment(String[] permissions) {   
    ((RxPermissionsFragment)this.mRxPermissionsFragment.get()).requestPermissions(permissions);   
}   
```   
  
其实就是调用了 RxPermissionsFragment 中的 requestPermissions 方法，所以我们进到该方法看看：   
```java   
@TargetApi(23)   
void requestPermissions(@NonNull String[] permissions) {   
    this.requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);   
}   
```   
  
可以看到，该方法并没有进行什么特别操作，就是申请权限，那么既然申请了权限了，是否申请成功的处理应该在回调中，所以我们看看回调的处理   
```java   
public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {   
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);   
    if (requestCode != PERMISSIONS_REQUEST_CODE) return; //如果请求码不符合，则直接返回   
        boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length]; //保存权限申请结果   
        for (int i = 0; i &lt; permissions.length; i++) { //循环遍历，看权限是否被永久拒绝了   
            //调用Fragment中的**方法获取权限申请结果，如果被点击了不再在提醒，并且拒绝权限时，则会返回 false，否则返回 true   
            shouldShowRequestPermissionRationale[i] = this.shouldShowRequestPermissionRationale(permissions[i]);    
    }   
    // 调用 Fragment 中的调用重载方法   
    this.onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale);//【核心中的核心】   
}   
```   
  
主要是创建了一个 boolean 数组，用于保存判断权限申请的状态。如果权限被申请过，并且用户在对话框中点击了“不在提醒”选项，这个时候调用 `Fragment` 中的`shouldShowRequestPermissionRationale` 方法会返回false，代表说这个权限被拒绝，我们可以根据这个结果再给用户做一些提示性的工作。   
  
最后看最核心的部分：onRequestPermissionsResult   
```java   
@Override   
void onRequestPermissionsResult(String permissions[], int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {   
    for (int i = 0, size = permissions.length; i &lt; size; i++) {  //循环权限列表   
        // 查询 mSubjects 集合中是否存在代表该 permission 的 PublishSubject 对象   
        PublishSubject<Permission> subject = (PublishSubject)this.mSubjects.get(permissions[i]);   
        if (subject == null) return; // 如果没有，则直接返回   
        this.mSubjects.remove(permissions[i]); //将集合中的 permission 的 PublishSubject 对象进行移除   
        boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED; //判断是否申请成功   
        subject.onNext(new Permission(permissions[i], granted, shouldShowRequestPermissionRationale[i])); //返回相应的对象   
        subject.onCompleted();   
    }   
}   
```   
  
首先判断在 mSubjects 集合中是否已经存在了该 permission 的 PublishSubject 对象，如果没有，则直接返回，代表出错。其实这里的对象在RxPermissions 的 requestImplementation() 中已经通过 mRxPermissionsFragment.setSubjectForPermission(permission, subject) 语句进行赋值了，所以一定会是存在的。   
  
接着 mSubjects 集合将该 permission 的 PublishSubjects 对象移除，这是为什么呢？这个要看 `RxPermissions#requestImplementation()` 中的实现，在该方法中会 `PublishSubject<Permission> subject = mRxPermissionsFragment.getSubjectByPermission(permission);` 这个语句出现，这个语句代表说，如果权限未申请过，也未被撤销，那么就直接在 RxPermissionsFragment 的 mSubjects 集合中查找是否存在该 permission 的 PublishSubject 对象，如果有，就直接通过 list.add(subject) 这个语句保存到集合中了，那么这样被拒绝的权限，下次将不会被重新申请，所以需要移除，之后就是发射数据，发送时间结束标识了。   
  
## 申请结果回调方法   
分析完了 RxPermissionsFragment 中的权限申请的操作，我们就需要回到 Rxpermissions#request() 方法中，在该方法的 flatMap 的回调方法中返回了 Observable<Permission> 对象，因此这个方法的任务完成。   
```java   
return RxPermissions.this.request(o, permissions)   
    .buffer(permissions.length)   
    .flatMap(new Function<List<Permission>, ObservableSource<Boolean>>() {...}   
```   
  
接着再往回看，在ensure()方法中，我们可以看到在该方法中调用了request()方法之后，接着调用了buffer(permissions.length)方法，那么这个buffer()的作用是什么呢？它的作为是为了将一个序列的 Observable<Permission> 对象转换成 Observable<List<Permission>> 对象(flatMap的逆操作)。为什么要转换呢？因为 requestImplementation() 方法中的返回值为 Observable.concat(Observable.from(list))，这代表说这是一个 Observable<Permission> 对象序列，所以需要通过 buffer 方法进行转换。   
  
接着就是使用 flatMap 操作符转换成 Observable<Boolean> 对象：   
```java   
@Override   
public ObservableSource<Boolean> apply(List<Permission> permissionsx) {   
        ...   
        return Observable.just(false);  //我们可以根据实际需要进行自己的处理   
    }   
}   
```   
这里就是返回给我们的最终结果了，整个流程也就到此结束。   
  
2018-8-30  
  
