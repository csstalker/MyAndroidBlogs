| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
服务 Service 简介 启动方式 生命周期 案例  
***  
目录  
===  

- [Service 概述](#Service-概述)
- [Service 的启动方式](#Service-的启动方式)
	- [startService 方式](#startService-方式)
		- [特点](#特点)
		- [生命周期](#生命周期)
		- [显示启动和隐式启动](#显示启动和隐式启动)
		- [onStartCommand 方法详解](#onStartCommand-方法详解)
			- [startId](#startId)
			- [flags](#flags)
			- [返回值](#返回值)
	- [bindService 方式](#bindService-方式)
		- [典型过程](#典型过程)
		- [生命周期](#生命周期)
		- [特点](#特点)
		- [bindService 方法详解](#bindService-方法详解)
			- [ServiceConnection](#ServiceConnection)
			- [flag](#flag)
		- [bind 和 unbind 细节](#bind-和-unbind-细节)
	- [混合方式启动服务](#混合方式启动服务)
- [清单文件中可设置的属性](#清单文件中可设置的属性)
- [案例](#案例)
	- [MainActivity](#MainActivity)
	- [SecondActivity](#SecondActivity)
	- [MyService](#MyService)
	- [IMyBinder](#IMyBinder)
	- [MyThread](#MyThread)
	- [清单文件](#清单文件)
  
# Service 概述  
[demo地址](https://github.com/baiqiantao/ServiceDemo.git)  
Service通常总是称之为"后台服务"，其中"后台"一词是相对于前台而言的，具体是指`其本身的运行并不依赖于用户可视的UI界面`，因此，从实际业务需求上来理解，Service的适用场景应该具备以下条件：  
- `不依赖于用户可视的UI界面`。当然，这一条其实也不是绝对的，如前台Service就是与`Notification`界面结合使用的。  
- `具有较长时间的运行特性`  
  
你可以继承以下两个类中的任何一个来创建一个service：  
- `Service`：这是所有service的基类  
- `IntentService`：这是Service的子类，它`使用一个工作线程来处理所有的启动请求`，一次一个。如果你不要求你的service同时处理多个请求，这是最好的方式。你需要做的就是实现`onHandleIntent()`方法，他将接受每个启动请求的intent使得你可以做后台工作。  
  
# Service 的启动方式  
## startService 方式  
### 特点  
- 最核心的一句话：当Client采用startService方式启动一个Service后，`Client就和Service没有任何关联了`。  
- Started Service相对比较简单，通过`context.startService`启动Service，`context.stopService`停止此Service。当然，在Service内部，也可以通过 `stopSelf` 方式停止其本身。  
- Client A 通过`startService(..)`启动Service后，也可以在其他ClientB通过调用`stopService(..)`结束此Service。  
- `onBind` 函数是Service基类中的唯一抽象方法，子类都必须实现。但此函数的返回值仅针对Bound Service类型的Service才有用，在Started Service类型中，此函数直接返回 `null` 即可。  
- 对于同一类型的Service，Service实例一次永远`只存在一个`，而不管Client是否是相同的组件，也不管Client是否处于相同的进程中。  
- 如果Service需要运行在单独的进程中，需要通过`android:process`指明此进程名称；如果此Service需要对其他App开放，`android:exported`属性值需要设置为true。  
- 当Client调用`startService`启动Service时，可以通过Intent传递数据给Service；在Service执行过程中，如果需要传递数据给Client，一般可以通过借助于发送广播、EventBus、静态全局数据等方式。  
  
### 生命周期  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181118/gEHC4bda4J.png?imageslim)  
- 当Client调用startService后，如果Service是第一次启动，首先会执行`onCreate`，然后再执行`onStartCommand`。  
- 在Service启动后，当Client再次调用startService，将只执行`onStartCommand`。  
- 使用startService方式启动Service不会回调`onBind`方法。  
- 无论多少次的startService，只需要一次`stopService()`即可将此Service终止，此时Service会执行`onDestroy()`函数。  
- 多次调用stopService()，或在Service没有启动时调用stopService()，都不会出现任何报错或问题。  
- 当用户强制kill掉进程时，onDestroy()是不会执行的。  
- 完整生命周期：`onCreate > onStartCommand > onStartCommand ... > onDestroy`。  
  
### 显示启动和隐式启动  
**结论**  
- startService 和 stopService 中的 intent 既可以是显式 Intent，也可以是隐式 Intent。  
- 当 Client 与 Service 同处于一个 App 时，一般推荐使用显示 Intent；当处于不同 App 时，只能使用隐式 Intent。  
- 在高版本中，隐式方式开启或关闭或绑定服务必须设置包名 `intent.setPackage(getPackageName())`，否则直接挂掉。  
  
> PS：通过显式Intent启动Service时，如果Service没有在`AndroidManifest.xml`中声明，则不会像Activity那样直接崩溃并提示找不到Activity，而是会给出waring信息 `IllegalArgumentException: Service not registered`。  
  
**场景**  
如果在隐式启动Service的时候，遇到类似这样的问题：  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181118/1JFCB5fCJ4.png?imageslim)  
  
原因是在5.0后系统要求 `Service Intent must be explicit(明确的)`，文档说明：  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181118/Kgd8b62J2A.png?imageslim)  
  
所以如果这样隐式启动服务：`startService(new Intent("YourAction"))`，程序会直接crash掉  
我们改成显示启动就可以了：`startService(new Intent(getApplicationContext(), MyRemoteService.class))`    
  
但是如果我们不能引用服务的`.class`文件，只能隐式绑定服务才行，怎么办呢？这时我们只需加上包名即可：  
```java  
Intent service = new Intent("YourAction");    
service.setPackage("com.bqt.aidlservice");//设置包名后就可以正常使用隐式服务了    
startService(service );  
```  
这样就可以了~  
  
### onStartCommand 方法详解  
最开始其实只有`onStart`方法的，后来`onStart`方法被废弃掉了，并增加了`onStartCommand`方法，但是调用`onStartCommand`时首先调用的还是`onStart`方法，只不过多了一个返回值：  
```java  
@Deprecated  
public void onStart(Intent intent, int startId) { ... }  
  
public int onStartCommand(Intent intent, int flags, int startId) {  
    onStart(intent, startId);  
    return mStartCompatibility ? START_STICKY_COMPATIBILITY : START_STICKY;  
}  
```  
  
#### startId  
> A unique integer representing this specific request to start.  Use with `stopSelfResult(int)`  
  
startId表示的是：对这个service请求的activity(或者其他实体)的`编号`。  
  
每次`startService()`开启服务时，系统都会自动为开启的Service产生一个`不同的startId`，之前赋予它的startId(如果有)将会被覆盖，并且这个新产生的startId会成为这个Service的新的startId，无论Service是否正在运行。  
  
考虑如下情况：当多个组件启用了同一个Service，Service提供`互斥`的服务(使用synchronized关键字)，且要保证在Service把所有工作完成之前不能自杀。  
这个时候，startId就相当有用了。在Service 的 onStartCommand() 中把startId保存起来，因为互斥的使用服务，则Service是按顺序提供服务的，则Service自杀的时候只用检查当前Service的startId与保存的这个startId是否相同，不同则说明Service之后还有任务，不能自杀，相同则说明正在运行的任务已经是最后一个任务了，运行完后就可以使用`stopSelf(int startId)`方法自杀了。  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181118/L3665BICDj.png?imageslim)  
  
#### flags  
> Additional data about this start request.  Currently either 0, START_FLAG_REDELIVERY, or START_FLAG_RETRY.  
  
flags表示`启动服务的方式`，取值有：【0】或【`START_FLAG_REDELIVERY`=1】或【`START_FLAG_RETRY`=2】  
  
- `START_FLAG_REDELIVERY`：表示该 Intent 是先前传递的 intent 的重新传递(re-delivery)，该服务的`onStartCommand`方法先前返回的是`START_REDELIVER_INTENT`，但是该服务在调用 stopSelf 之前已被杀死。  
> This flag is set in onStartCommand if the Intent is a re-delivery of a previously delivered intent, because the service had previously returned START_REDELIVER_INTENT but had been killed before calling stopSelf(int) for that Intent.  
  
- `START_FLAG_RETRY`：表示服务之前被设为`START_STICKY`  
> This flag is set in onStartCommand if the Intent is a retry because the original attempt never got to or returned from onStartCommand(Intent, int, int).  
  
#### 返回值  
> return The return value indicates指示 what semantics语义 the system should use for the service's current started state.    
It may be one of the constants associated with the START_CONTINUATION_MASK bits.  
  
具体的可选值及含义如下：  
- `START_STICKY_COMPATIBILITY` = 0;  `START_STICKY`的兼容版本，不保证`onStartCommand`在被杀死后将再次被调用。  
- `START_STICKY` = 1;  当Service因为内存不足而被系统kill后，接下来未来的某个时间内，当系统内存足够可用的情况下，系统将会`尝试重新创建此Service`，一旦创建成功后将回调`onStartCommand`方法，但其中的`Intent将是null`(pendingintent除外)。  
- `START_NOT_STICKY` = 2;  当Service因为内存不足而被系统kill后，接下来未来的某个时间内，即使系统内存足够可用，系统也`不会尝试重新创建此Service`，除非程序中Client明确再次调用`startService`启动此Service。  
- `START_REDELIVER_INTENT` = 3;  与`START_STICKY`唯一不同的是，回调`onStartCommand`方法时，其中的`Intent将是非空`，将是最后一次调用`startService`中的intent。  
  
注意：  
以上的描述中，`当Service因为内存不足而被系统kill后` 这句话一定要非常注意，因为此函数的返回值设定只是针对此种情况才有意义的。  
换言之，当`人为`的kill掉Service进程，此函数返回值无论怎么设定，未来的某个时间内，即使系统内存足够可用，Service也`不会重启`。  
  
另外，需要注意的是，`小米`等国产手机针对此处可能做了一定的修改：  
- 在`自启动管理`中有一个自启动应用列表，默认情况下，只有极少应用默认是可以自启动的，其他应用默认都是禁止的。  
- 当然用户可以手动添加自启动应用，被允许自启动应用的Started Service，如果`onStartCommand()`的返回值是`START_STICKY`或`START_REDELIVER_INTENT`，那么当用户在小米手机上长按Home键结束App后，接下来未来的某个时间内，当系统内存足够可用时，Service依然可以按照上述规定重启。  
- 当然，如果用户在`设置 > 应用 > 强制kill掉App进程`，此时Service也是不会重启的。  
  
## bindService 方式  
`bindService()`方式启动的 Service 的生命周期和调用`bindService()`方法的 Activity 的`生命周期是一致的`，也就是说，如果 Activity 结束了，那么 Service 也就结束了。Service 和调用 `bindService()` 方法的进程`是同生共死`的。  
  
相对于Started Service，Bound Service 具有更多的知识点。Bound Service 的最重要的特性在于，通过 Service 中的`Binder`对象可以较为方便进行`Client-Service`通信。  
  
### 典型过程  
- 自定义 Service 继承 Service，并重写 onBind 方法，此方法中需要返回具体的`IBinder`对象，一般将其定义为 Service 的内部类  
- Client通过`bindService(Intent, ServiceConnection, flag)`将Service绑定到此Client上，绑定时需传递一个`ServiceConnection`实例，一般让Client自身实现ServiceConnection接口，或在Client中定义一个内部类  
- Client可在ServiceConnection的回调方法`onServiceConnected(ComponentName, IBinder)`中获取Service端`IBinder`实例，获取到IBinder实例后，Client端便可以通过自定义接口中的API间接调用Service端的方法，实现和Service的通信  
- 当Client在恰当的生命周期需要和Service解绑时，通过调用`unbindService(ServiceConnection)`即可和Service解绑。  
  
### 生命周期  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181118/E2LI0B5608.png?imageslim)  
- 当Client调用bindService后，如果Service没有启动，首先会执行`onCreate()`，然后再执行`onBind`。  
- 使用bindService方式启动Service不会回调`onStartCommand()`方法。  
- 在bindService后，当Client再次调用bindService，不会回调任何方法。  
- 当Client调用`unbindService()`和Service解绑后，Service会回调`onUnbind`方法。如果再没有其他Client与Service绑定，那么Service会回调`onDestory`方法自行销毁；如果还和其他Client与Service绑定，则不会回调`onDestory`方法。  
- 如果对一个服务进行多次解绑，会抛出服务没有注册的`异常`。  
- 完整生命周期：`onCreate > onBind > onUnbind > onDestroy`(并非Client调用unbindService后就会回调)。  
  
### 特点  
- bindService启动的服务在调用者和服务之间是典型的`client-server`模式，即调用者是客户端，service是服务端，service就一个，但是连接绑定到service上面的客户端client可以是一个或多个。这里特别要说明的是，这里所提到的client指的是`组件`，比如某个Activity。   
- 客户端client可以通过`IBinder`接口获取到Service的实例，从而可以实现在client端直接调用Service中的方法以实现灵活的交互。另外还可借助`IBinder`实现跨进程的client-server的交互，这在纯startService启动的Service中是无法实现的。   
- 不同于startService启动的服务默认是无限期执行的，bindService启动的服务的生命周期与其绑定的client息息相关，当client销毁的时候，client会自动与Service解除绑定。client也可以通过明确调用Context的unbindService方法与Service解除绑定。  
- 当没有任何client与Service绑定的时候，Service会`自行销毁`(当然，通过startService启动的Service除外)。   
- startService启动的服务会涉及Service的的`onStartCommand`回调方法，而通过bindService启动的服务会涉及Service的`onBind、onUnbind`回调方法。  
  
### bindService 方法详解  
> bindService(Intent, ServiceConnection, flag)  
  
#### ServiceConnection  
ServiceConnection可以理解为Client和Service之间的桥梁(连接器)，其作用有两个：  
- 当Client和Service成功绑定后，Service可以通过回调ServiceConnection的`onServiceConnected`方法，将Client需要的`IBinder`对象返回给Client。  
- 当Client和Service解绑定时，也是通过解除Client和Service之间的`ServiceConnection`来实现的  
  
ServiceConnection接口有2个方法需要重写。一个是当Service成功绑定后会被回调的`onServiceConnected()`方法，另一个是当Service被关闭时被回调的`onServiceDisconnected()`。前者会被传入一个IBinder参数，这个IBinder就是在Service的生命周期的回调方法onBind中的返回值，它对Service的绑定式IPC起到非常重要的作用。　  
  
> 注意：  
Client 在 bindService 成功之后就会回调 onServiceConnected 方法，但在 unbindService 成功之后并不会回调 onServiceDisconnected 方法，这个方法通常发生在托管(hosting)服务的进程崩溃或被杀死时。这种情况下不会删除ServiceConnection本身，且对服务的绑定将保持活动状态，并且当下次运行Service时，您将收到对onServiceConnected的调用。  
  
#### flag  
`flag`则是表明绑定Service时的一些设置，一般情况下可以直接使用0，其标志位可以为以下几种：  
[官方文档](https://developer.android.google.cn/reference/android/content/Context.html#BIND_AUTO_CREATE)  
- `BIND_AUTO_CREATE` = 1;//常用。表示收到绑定请求的时候，`如果服务尚未创建，则即刻创建`  
- `BIND_DEBUG_UNBIND` = 2;//通常用于调试场景中，判断绑定的服务是否正确，但容易引起内存泄漏  
- `BIND_NOT_FOREGROUND` = 4;//表示系统将阻止驻留该服务的进程具有前台优先级，仅在后台运行  
- `BIND_ABOVE_CLIENT` = 8;  
- `BIND_ALLOW_OOM_MANAGEMENT` = 16;  
- `BIND_WAIVE_PRIORITY` = 32;  
- `BIND_IMPORTANT` = 64;  
- `BIND_ADJUST_WITH_ACTIVITY` = 128;  
- `BIND_EXTERNAL_SERVICE` = -2147483648;  
  
### bind 和 unbind 细节  
- 绑定服务，首先要做的事情就是先用`Map`记录当前绑定服务所需的一些信息， 然后启动服务。  
- 解绑服务，先从早前的Map集合中移除记录，然后根据Map集合中是否还有元素决定是否销毁服务。  
- 如果解绑后再次解绑，无非就是再到这个map集合中找这条记录，没有找到就抛出服务没有注册的异常。  
  
## 混合方式启动服务  
当bindService之前已通过startService开启了服务，则其生命周期方法回调顺序为  
startService -> bindService -> unbindService -> startService -> startService -> stopService  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181118/IE9dLmg29H.png?imageslim)  
  
startService -> bindService -> stopService -> unbindService  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181118/fegJedF1jJ.png?imageslim)  
  
这种方式其实就是上面那两种方式特性的综合，或者说，兼具两者的优点：  
- 由于是通过`startService`方式开启的服务，所以在client通过`unbindService`解绑后Service并不会销毁，并且client注销后Service也不会销毁。  
- 由于又通过`bindService`方式绑定了服务，所以client同样可以方便的和Service进行通讯。  
  
> PS：  
使用`bindService`来绑定一个已通过`startService`方式启动的Service时，系统只是将Service的内部`IBinder`对象传递给启动者，并不会将Service的生命周期与启动者绑定，所以，此后调用`unBindService`方法取消绑定后，Service不会调用`onDestroy`方法。  
  
# 清单文件中可设置的属性  
可以设置的属性:  
- `enabled`=["true" | "false"]  //是否这个service能被系统实例化，如果能则为true，否则为false。默认为true。  
- `exported`=["true" | "false"]    //是否其它应用组件能调用这个service或同它交互，能为true，默认值依赖于是否包含过滤器。  
- `icon`="drawable resource"    //服务呈现的`图标`。  
- `isolatedProcess`=["true" | "false"]    //如果设置为true，这个服务将运行在专门的进程中  
- `label`="string resource"    //这个服务给用户`显示的名称`。  
- `name`="string"    //实现这个service的Service子`类名称`。  
- `permission`="string"    //为了启动这个service或绑定到它一个实体必须要有的`权限的名称`。  
- `process`="string"  //服务将要运行的`进程名称`。  
  
**android:enabled**  
- 是否这个 service 能被系统实例化，如果能则为true，否则为false。默认为true。  
- `<application>`元素有它自身的能应用到所有组件的`enabled`属性，要使这个service能够enabled，那么`<application>`和这个`<service>`的此属性都必须为true(默认值都是true)；如果有一个为false，这个服务就会disabled。  
  
**android:exported**  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181118/d6cmAc3CKB.png?imageslim)  
- 是否`其它应用的组件`能调用这个 service 或同它交互，如果能则为true，否则为false。  
- 当值为false时，只有`同一个应用的组件`或`有相同用户ID的应用的组件`能启动这个服务或绑定它。  
- 默认值依赖于服务是否包含`intent filters`：  
    - 没有`intent filters`意味着它只能通过指定它的准确类名来调用它，这就意味着这个服务只能在应用内部被使用(因为其它应用不知道这个类的类名)。因此，在这种情况下，默认值是false。  
    - 至少有一个`intent filters`意味着这个服务可以在外部被使用，因此，默认值为true。  
- 这个属性并非是限制这个服务暴露给其它服务的唯一途径，你也能通过`权限`来限制跟服务交互的外部实体(参见permisson属性)。  
  
**android:icon**  
- 服务呈现的图标。  
- 如果没有设置，那么将使用`application`的图标代替  
  
**android:isolatedProcess**  
- 如果设置为true，这个服务将运行在`专门的进程中`，这个进程从系统的剩余部分独立出来，它自身没有权限。同它唯一的通信方式就是通过这个Service API(binding或starting)。  
  
**android:label**  
- 这个服务给用户显示的名称。  
- 如果这个属性没有设置，将使用`<application>`的label属性代替  
  
**android:name**  
- 实现这个service的Service子类名称，没有默认值，这个名称必须被指定。  
- 可以是完整格式的类名，也可以是一个简写。  
  
**android:permission**  
- 为了启动这个service或绑定到它一个实体必须要有的`权限的名称`。  
- 如果`startService()，bindService()或stopService()`的调用者还没有获取这个授权，那么这些方法就不会工作，而且这个intent对象也不会传递到service。  
- 如果这个属性没有设置，由`<application>`元素的`permission`属性设置的权限就会应用到这个service。如果`<application>`也没有设置，那么这个服务就不再受权限保护。  
  
**android:process**  
- 服务将要运行的`进程名称`。  
- 一般来讲，应用的所有组件都运行在应用创建的`默认进程`中。就像应用的包名一样。  
- `<application>`元素的`process`属性能对所有组件设置不同的默认值。然而，组件能通过它自身的`process`属性重写默认值，从而允许你扩展你的应用跨越多个进程。  
- 如果分配到这个属性的名称以冒号`:`开始，那么当需要它的时候，一个新的、对这个应用`私有`的进程就被创建，同时这个服务就在哪个进程运行。  
- 如果进程的名字以`小写字母`开始，那么这个服务将运行在全局进程中。这就允许在不同应用中的组件`共享`这个进程，降低资源的消耗。  
  
# 案例  
## MainActivity  
```java  
public class MainActivity extends ListActivity implements ServiceConnection {  
    public boolean isKeepThreadRunning;//线程结束条件  
    private IMyBinder mIBinder;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"0、开启一个线程，执行死循环操作",  
            "1、通过标记位关闭上面开启的所有线程",  
            "2、通过startService方式显示开启服务",  
            "3、通过stopService方式显示关闭服务",  
            "4、隐式方式开启或关闭服务，必须设置包名",  
            "5、bindService方式开启服务 ",  
            "6、unbindService方式解除绑定服务",  
            "7、通过IBinder间接调用服务中的方法",  
            "8、启动另一个Activity"};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(array))));  
        isKeepThreadRunning = true;  
    }  
      
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        isKeepThreadRunning = false; //在onDestroy中把线程的关闭条件设为true，防止内存泄漏  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0: //开启一个线程，执行死循环操作  
                new MyThread(this).start();  
                break;  
            case 1: //通过标记位关闭上面开启的所有线程  
                isKeepThreadRunning = false;  
                break;  
            case 2: //startService方式显示开启服务  
                startService(new Intent(this, MyService.class));  
                break;  
            case 3://stopService方式显示关闭服务  
                stopService(new Intent(this, MyService.class));  
                break;  
            case 4://隐式方式开启或关闭服务，必须设置包名  
                Intent intent = new Intent(MyService.ACTION_MY_SERVICE);  
                intent.setPackage(getPackageName()); //在高版本中，隐式方式开启或关闭或绑定服务必须设置包名，否则直接挂掉  
                startService(intent);  
                break;  
            case 5://bindService方式开启服务  
                bindService(new Intent(this, MyService.class), this, Context.BIND_AUTO_CREATE);  
                break;  
            case 6: //unbindService方式解除绑定服务  
                unbindMyService();  
                break;  
            case 7: //通过IBinder间接调用服务中的方法  
                callMethodInService();  
                break;  
            case 8: //启动另一个Activity  
                startActivity(new Intent(this, SecondActivity.class));  
                break;  
        }  
    }  
      
    @Override  
    public void onServiceConnected(ComponentName name, IBinder service) {  
        Log.i("bqt", "MainActivity-onServiceConnected，" + name.toString());  
        mIBinder = (IMyBinder) service;  
    }  
      
    @Override  
    public void onServiceDisconnected(ComponentName name) {  
        Log.i("bqt", "MainActivity-onServiceDisconnected，" + name.toString());  
    }  
      
    private void unbindMyService() {  
        if (mIBinder != null) {  
            unbindService(this);//多次调用会报IllegalArgumentException异常，但是并不崩溃  
            mIBinder = null;//若不把mIBinder置为空，则服务销毁后仍然可以调用服务里的方法，因为内部类的引用还在  
        } else {  
            Toast.makeText(this, "还没有绑定服务，不需要解绑", Toast.LENGTH_SHORT).show();  
        }  
    }  
      
    private void callMethodInService() {  
        if (mIBinder != null) {  
            mIBinder.callMethodInService(new Random().nextInt(3));  
        } else {  
            Toast.makeText(this, "还没有绑定服务", Toast.LENGTH_SHORT).show();  
        }  
    }  
}  
```  
## SecondActivity  
```java  
public class SecondActivity extends ListActivity implements ServiceConnection {  
    private IMyBinder mIBinder;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"通过startService方式显示开启服务",  
            "通过stopService方式显示关闭服务",  
            "bindService方式开启服务",  
            "unbindService方式解除绑定服务"};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                startService(new Intent(this, MyService.class));  
                break;  
            case 1:  
                stopService(new Intent(this, MyService.class));  
                break;  
            case 2:  
                bindService(new Intent(this, MyService.class), this, Context.BIND_AUTO_CREATE);  
                break;  
            case 3:  
                unbindMyService();  
                break;  
        }  
    }  
      
    @Override  
    public void onServiceConnected(ComponentName name, IBinder service) {  
        Log.i("bqt", "SecondActivity-onServiceConnected，" + name.toString());  
        mIBinder = (IMyBinder) service;  
    }  
      
    @Override  
    public void onServiceDisconnected(ComponentName name) {  
        Log.i("bqt", "SecondActivity-onServiceDisconnected，" + name.toString());  
    }  
      
    private void unbindMyService() {  
        if (mIBinder != null) {  
            unbindService(this);//多次调用会报IllegalArgumentException异常，但是并不崩溃  
            mIBinder = null;//若不把mIBinder置为空，则服务销毁后仍然可以调用服务里的方法，因为内部类的引用还在  
        } else {  
            Toast.makeText(this, "还没有绑定服务，不需要解绑", Toast.LENGTH_SHORT).show();  
        }  
    }  
}  
```  
  
## MyService  
```java  
public class MyService extends Service {  
    public static final String ACTION_MY_SERVICE = "com.bqt.service.my_action";  
      
    @Override  
    public void onCreate() {  
        Log.i("bqt", "MyService-onCreate");  
        super.onCreate();  
    }  
      
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.i("bqt", "MyService-onStartCommand--flags=" + flags + "--startId=" + startId);//flags一直为0，startId每次会自动加1  
        return super.onStartCommand(intent, flags, startId); //每次调用startService时都会回调；调用bindService时不会回调  
    }  
      
    @Override  
    public IBinder onBind(Intent intent) {  
        Log.i("bqt", "MyService-onBind");  
        return new MyMyBinder(); //再次bindService时，系统不会再调用onBind()方法，而是直接把IBinder对象传递给其他后来增加的客户端  
    }  
      
    @Override  
    public void onRebind(Intent intent) {  
        super.onRebind(intent);  
        Log.i("bqt", "MyService-onRebind");  
    }  
      
    @Override  
    public boolean onUnbind(Intent intent) {  
        Log.i("bqt", "MyService-onUnbind");  
        return super.onUnbind(intent); //绑定多客户端情况下，需要解除所有的绑定后才会(就会自动)调用onDestoryed方法  
    }  
      
    @Override  
    public void onDestroy() {  
        Log.i("bqt", "MyService-onDestroy");  
        super.onDestroy(); //不知道什么时候调用，没有发现他被回调过  
    }  
      
    /**  
     * 这是服务里面的一个方法，对外是隐藏的，只能通过IBinder间接访问  
     */  
    private void methodInService(int money) {  
        Log.i("bqt", "MyService-call method in service");  
        Toast.makeText(this, "调用了服务里的方法，开启了大招：" + money, Toast.LENGTH_SHORT).show();  
    }  
      
    private class MyMyBinder extends Binder implements IMyBinder {//实现IBinder接口或继承Binder类  
          
        @Override  
        public void callMethodInService(int money) {  
            if (money < 1) {  
                Toast.makeText(MyService.this, "对不起，余额不足，不能发大招：" + money, Toast.LENGTH_SHORT).show();  
            } else {  
                methodInService(money);//间接调用了服务中的方法  
            }  
        }  
    }  
}  
```  
  
## IMyBinder  
```java  
public interface IMyBinder {  
    void callMethodInService(int money);  
}  
```  
  
## MyThread  
```java  
public class MyThread extends Thread {  
    private SoftReference<MainActivity> context;  
      
    MyThread(MainActivity activity) {  
        context = new SoftReference<>(activity);  
    }  
      
    @Override  
    public void run() {  
        if (context != null && context.get() != null) {  
            context.get().runOnUiThread(() -> showToast("线程" + getId() + "已开启……"));  
            while (context.get().isKeepThreadRunning) {//这是一个死循环，关闭线程的唯一条件就是isKeepThreadRunning==false  
                Log.i("bqt", getId() + " - " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));  
                SystemClock.sleep(2000);  
            }  
            context.get().runOnUiThread(() -> showToast("线程" + getId() + "已关闭……"));  
        }  
    }  
      
    private void showToast(String text) {  
        if (context != null && context.get() != null) {  
            Toast.makeText(context.get(), text, Toast.LENGTH_SHORT).show();  
        }  
    }  
}  
```  
  
## 清单文件  
```xml  
<service  
    android:name=".MyService"  
    android:permission="com.bqt.service.test_permission">  
    <intent-filter>  
        <action android:name="com.bqt.service.my_action"/>  
    </intent-filter>  
</service>  
```  
  
2018-11-19  
