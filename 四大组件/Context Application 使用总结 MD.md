| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Context Application 使用总结 MD  
***  
目录  
===  

- [Application 使用总结](#Application-使用总结)
	- [Application 简介](#Application-简介)
	- [通过反射方式获取当前应用的 Application](#通过反射方式获取当前应用的-Application)
	- [Application 的生命周期方法](#Application-的生命周期方法)
	- [注册全局的 ActivityLifecycleCallbacks](#注册全局的-ActivityLifecycleCallbacks)
	- [Application 中的其他注册方法](#Application-中的其他注册方法)
	- [使用 Application 实现数据全局共享](#使用-Application-实现数据全局共享)
	- [Application 方法执行顺序](#Application-方法执行顺序)
- [Context 使用总结](#Context-使用总结)
	- [Context 简介](#Context-简介)
	- [Context 的继承结构](#Context-的继承结构)
	- [ContextWrapper 类的结构](#ContextWrapper-类的结构)
	- [Context 的应用场景](#Context-的应用场景)
	- [如何获取 Context](#如何获取-Context)
	- [getApplication 和 getApplicationContext](#getApplication-和-getApplicationContext)
	- [Context 引起的内存泄露](#Context-引起的内存泄露)
  
# Application 使用总结  
  
## Application 简介  
[官方文档](https://developer.android.google.cn/reference/android/app/Application) 中的描述：  
  
> Application 是用来维护应用程序全局状态[maintain global application state]的基础类。你可以通过在 `AndroidManifest.xml` 文件的`<application>`标签中指出他的`android:name`属性的方式提供自己的实现。在创建应用程序/包的进程时，Application类或Application类的子类在任何其他类之前实例化。  
  
> 注意：通常不需要子类化Application。在大多数情况下，静态单例[static singletons]可以以更模块化的方式[more modular way]提供相同的功能。如果您的单例需要全局上下文(例如注册广播接收器)，则在调用单例的`getInstance()`方法时将`Context.getApplicationContext()`作为Context参数。  
  
Android系统会为每个程序运行时创建一个Application类的对象且仅创建一个，所以Application可以说是单例模式的一个类。且 Application 对象的生命周期是整个程序中最长的，它的生命周期就等于这个程序的生命周期。因为它是全局唯一的，所以在不同的Activity、Service中获得的对象都是同一个对象。所以通过 Application 来进行一些`数据传递、数据共享、数据缓存`等操作。  
  
Application和Activity，Service一样是Android框架的一个系统组件，当android程序启动时系统会创建一个 Application 对象，用来存储系统的一些信息。通常我们是不需要指定一个Application的，这时系统会自动帮我们创建，如果需要创建自己的Application，也很简单，创建一个类继承 Application并在manifest的application标签中进行注册，只需要给Application标签增加个name属性把自己的 Application 的名字定入即可。  
  
## 通过反射方式获取当前应用的 Application  
没有自定义Application时，当前应用的Application为`android.app.Application`  
自定义Application时，当前应用的Application为你所设置的 Application  
  
```java  
public class App {  
    private static Application application;  
      
    private App() {  
    }  
      
    //通过反射方式获取当前应用的Application  
    @SuppressLint("PrivateApi")  
    public static Application get() {  
        if (application == null) {  
            try {  
                Class<?> clazz = Class.forName("android.app.ActivityThread");  
                Field field = clazz.getDeclaredField("sCurrentActivityThread");  
                field.setAccessible(true);  
                Object object = field.get(null);//得到ActivityThread的对象，虽然是隐藏的，但已经指向了内存的堆地址  
                Method method = clazz.getDeclaredMethod("getApplication");  
                method.setAccessible(true);  
                application = (Application) method.invoke(object);  
            } catch (ClassNotFoundException e) {  
                e.printStackTrace();  
            } catch (NoSuchFieldException e) {  
                e.printStackTrace();  
            } catch (NoSuchMethodException e) {  
                e.printStackTrace();  
            } catch (IllegalAccessException e) {  
                e.printStackTrace();  
            } catch (InvocationTargetException e) {  
                e.printStackTrace();  
            }  
        }  
        //没有自定义时为【android.app.Application】，自定义时为你设置的Application  
        Log.i("bqt", "当前应用的Application为：" + application.getClass().getName());  
        return application;  
    }  
}  
```  
  
## Application 的生命周期方法  
- `onCreate`：在应用程序创建的时候被调用，可以实现这个这个方法来创建和实例化任何应用程序状态变量或共享资源。还可以在这个方法里面得到 Application 的单例。  
- `onTerminate`：当终止应用程序对象时调用，`不保证一定被调用`，当程序是被内核终止以便为其他应用程序释放资源，那么将不会提醒，并且不调用应用程序的对象的onTerminate方法而直接终止进程。  
- `onLowMemory`：当系统资源匮乏的时候，我们可以在这里可以释放额外的内存。这个方法一般只会在后台进程已经结束，但前台应用程序还是缺少内存时调用。可以重写这个方法来清空缓存或者释放不必要的资源。  
- `onConfigurationChanged`：重写此方法可以监听APP一些配置信息的改变事件(如屏幕旋转等)，当配置信息改变的时候会调用这个方法。与 Activity 不同，配置改变时，应用程序对象不会被终止和重启。如果应用程序使用的值依赖于特定的配置，则重写这个方法来加载这些值，或者在应用程序级处理配置值的改变。  
- `onTrimMemory(int level)`：系统用这个方法提醒APP释放一些缓存了，如图片缓存，数据缓存之类的。里有传入一个int类型的参数level，它告诉APP们内存不足的严重性(越高越严重)  
  
关于`onLowMemory`：  
按我的理解就是，当APP处于前台时，但是所有后台程序都被kill光了，但是还是内存不足时，系统就会调用这个方法告诉APP：兄弟轮到你了。我们可以在这个方法里面释放一些不重要的资源，来保证到时候内存足够而让APP进程不被系统杀掉，或者提醒用户清一下垃圾，让内存清一点空位出来，我的手机老是这样提示我，不知道是不是这个方法惹的祸。  
  
## 注册全局的 ActivityLifecycleCallbacks  
`registerActivityLifecycleCallbacks` 和 `unregisterActivityLifecycleCallbacks` 这两个方法用于注册或者注销对APP内所有Activity的生命周期监听，当APP内Activity的生命周期发生变化的时候就会调用ActivityLifecycleCallbacks里面的方法：  
```java  
registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {  
   @Override  
   public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}  
     
   @Override  
   public void onActivityDestroyed(Activity activity) {}  
     
   @Override  
   public void onActivityStarted(Activity activity) {}  
     
   @Override  
   public void onActivityResumed(Activity activity) {  
       Log.i("bqt", "当前Activity" + "(" + activity.getClass().getSimpleName() + ".java" + ":" + 30 + ")");  
   }  
     
   @Override  
   public void onActivityPaused(Activity activity) {}  
     
   @Override  
   public void onActivityStopped(Activity activity) {}  
     
   @Override  
   public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}  
});  
```  
  
## Application 中的其他注册方法  
**registerComponentCallbacks 和 unregisterComponentCallbacks 方法**  
  
用于注册和注销 `ComponentCallbacks2` 回调接口，里面的回调方法前面已经介绍过，看名字就知道。  
Context 类也有这两个方法，但是 Context 类的方法只可以使用 `ComponentCallbacks`，比 `ComponentCallbacks2` 少了一个 `onTrimMemory()` 回调。  
```java  
registerComponentCallbacks(new ComponentCallbacks2() {  
   @Override  
   public void onTrimMemory(int level) { //ComponentCallbacks2 比 ComponentCallbacks 多了这个回调方法  
      Log.i("bqt", "【onTrimMemory】" + level);  
   }  
     
   @Override  
   public void onConfigurationChanged(Configuration newConfig) {  
      Log.i("bqt", "【onConfigurationChanged】");  
   }  
     
   @Override  
   public void onLowMemory() {  
      Log.i("bqt", "【onLowMemory】");  
   }  
});  
```  
  
**registerOnProvideAssistDataListener 和 unregisterOnProvideAssistDataListener 方法**  
  
API18 以上的方法，网上关于这两个方法的介绍很少，几乎没有，在官网上的介绍是这样的：  
> This is called when the user is requesting an assist, to build a full ACTION_ASSIST Intent with all of the context of the current application.  
  
好像是当用户请求帮助的时候会调用这个方法，然后会启动一个 `ACTION_ASSIST` 的 Intent。什么时候才是用户请求帮助呢?  
  
StackOverflow 里有的人说是长按 Home 键，外国的机子会跳出 `Google Now` 这个助手，至于国内的机子...。然后尝试了一下用下面的代码来发送一个ACTION_ASSIST来看看有什么效果：  
```java  
context.startActivity(new Intent(ACTION_ASSIST));  
```  
  
结果打开了我手机上UC浏览器的语音搜索功能。最后还是搞不懂这个方法什么时候会回调，如果有知道的请告知，谢谢！  
  
## 使用 Application 实现数据全局共享  
目前基本上每一个应用程序都会写一个自己的Application，然后在自己的Application类中去封装一些通用的操作。其实这并不是Google所推荐的一种做法，因为这样我们只是把Application当成了一个通用工具类来使用的，而实际上使用一个简单的单例类也可以实现同样的功能。当然这种做法也并没有什么副作用，只是说明还是有不少人对于Application理解的还有些欠缺。  
  
可以在以下场景中使用 Application 实现数据全局共享：  
- 可以设置一些全局的共享`常量`，如一些TAG，枚举值等。  
- 可以设置一些全局使用的共享`变量`数据，如一个全局的Handler等等。但是要注意，这里缓存的变量数据的作用周期只在APP的生命周期，如果APP因为内存不足而结束的话，再开启这些数据就会消失，所以这里只能存储一些不重要的数据来使数据全APP共享，想要储存重要数据的话需要SharePreference、数据库或者文件存储等这些持久化本地存储方式。  
- 可以设置一些`静态方法`来让其他类调用，来使用Application里面的全局变量，如实现APP一键退出功能时候会用到。  
  
## Application 方法执行顺序  
加入在自定义 Application 的构造方法中添加如下代码：  
```java  
public class MyApplication extends Application {   
    public MyApplication() {   
        Log.d("TAG", "package name is " + getPackageName());   
    }   
}  
```  
  
应用程序一启动就立刻崩溃了，报的是一个空指针异常。如果你尝试把代码改成下面的写法，就会发现一切正常了：  
```java  
public class MyApplication extends Application {   
    @Override   
    public void onCreate() {   
        super.onCreate();   
        Log.d("TAG", "package name is " + getPackageName());   
    }   
}   
```  
  
在构造方法中调用Context的方法就会崩溃，在`onCreate()`方法中调用Context的方法就一切正常，那么这两个方法之间到底发生了什么事情呢？  
  
我们查看`ContextWrapper`类的源码可知道，ContextWrapper中有一个`attachBaseContext()`方法，这个方法会将传入的一个`Context`参数赋值给`mBase`对象，之后mBase对象就有值了。  
  
而我们又知道，所有Context的方法都是调用这个mBase对象的同名方法，那么也就是说如果在mBase对象还没赋值的情况下就去调用Context中的任何一个方法时，就会出现空指针异常，上面的代码就是这种情况。  
  
Application中方法的执行顺序为：  
> 构造方法 --> attachBaseContext --> onCreate  
  
Application中在onCreate()方法而非构造方法里去初始化各种全局的变量数据是一种比较推荐的做法，但是如果你想把初始化的时间点提前到极致，也可以去重写`attachBaseContext()`方法，如下所示：  
```java  
public class MyApplication extends Application {   
    @Override   
    protected void attachBaseContext(Context base) {   
        // 在这里调用Context的方法会崩溃  
        super.attachBaseContext(base);   
        // 在这里可以正常调用Context的方法  
    }  
}  
```  
  
# Context 使用总结  
  
## Context 简介  
Context字面意思上下文，或者叫做场景，也就是用户与操作系统操作的一个过程，比如你打电话，场景包括电话程序对应的界面，以及隐藏在背后的数据  
  
源码中是这么来解释Context的：  
- 提供关于应用环境全局信息的接口。  
- 它是一个抽象类，它的实现由Android系统所提供。  
- 它允许访问特定于应用程序的资源和类，以及对应用程序级操作的up-calls，例如启动Activity，广播和接收意图等。  
  
> Interface to global information about an application environment.   
> This is an abstract class whose implementation is provided by the Android system.  
> It allows access to application-specific resources and classes, as well as up-calls for application-level operations such as launching activities, broadcasting and receiving intents, etc.  
  
## Context 的继承结构  
![](index_files/e02a5d71-2b0b-49f9-898c-81d9c20843a5.png)  
  
Context类本身是一个abstract类，Context的直系子类有两个，一个是`ContextWrapper`，一个是`ContextImpl`。从名字上就可以看出，`ContextWrapper`是上下文功能的`封装类`，而`ContextImpl`则是上下文功能的`实现类`。  
  
ContextWrapper构造函数中必须包含一个真正的Context引用，同时ContextWrapper中提供了attachBaseContext用于给ContextWrapper对象中指定真正的Context对象，调用ContextWrapper的方法都会被转向其所包含的真正的Context对象。  
  
ContextWrapper有三个直接的子类，`ContextThemeWrapper、Service和Application`。其中，ContextThemeWrapper是一个带主题Theme(即在清单文件中指定的`android:theme`)的封装类，而它有一个直接子类就是`Activity`。  
  
ContextImpl类真正实现了Context中的所以函数，应用程序中所调用的各种Context类的方法，其实现均来自于该类。ContextImpl在IDE中时找不到的，这是属于保护文件在`frameworks\base\core\java\android\app`目录中。  
  
一句话总结：Context的两个子类分工明确，其中ContextImpl是Context的具体实现类，ContextWrapper是Context的包装类。Activity，Application，Service虽都继承自ContextWrapper，但它们初始化的过程中都会创建ContextImpl对象，由ContextImpl实现Context中的方法。  
  
## ContextWrapper 类的结构  
ContextWrapper只是对Context类的一种封装，它的构造函数包含了一个真正的Context引用，即ContextImpl对象，ContextImpl正是`上下文功能的实现类`。  
  
也就是说像Application、Activity这样的类其实并不会去具体实现Context的功能，而仅仅是做了一层接口封装而已，Context的具体功能都是由ContextImpl类去完成的。  
  
部分源码：  
```java  
public class ContextWrapper extends Context {   
    Context mBase; //其实就是系统创建的 ContextImpl，Context的具体功能都是由ContextImpl类去完成的  
    protected void attachBaseContext(Context base) {   
        if (mBase != null)  throw new IllegalStateException("Base context already set");   
        mBase = base;   
    }  
  
    public Context getBaseContext() {   
        return mBase; //得到的是一个ContextImpl对象  
    }  
  
    //*************************************** 常用的功能都是调用的 mBase 的同名方法  ***************************************  
    @Override   
    public AssetManager getAssets() {   
        return mBase.getAssets();   
    }  
    @Override  
    public Resources getResources() {   
        return mBase.getResources();   
    }   
}   
```  
  
![](index_files/aad2cc25-927e-4c68-98af-1f671f62c2e0.png)  
  
其实所有`ContextWrapper`中方法的实现都非常统一，就是调用了`mBase`对象中对应当前方法名的方法。  
  
那么这个mBase对象又是什么呢？  
  
我们来看`attachBaseContext()`方法，这个方法中传入了一个base参数，并把这个参数赋值给了mBase对象。  
  
而`attachBaseContext()`方法其实是`由系统来调用的`，它会把`ContextImpl`对象作为参数传递到`attachBaseContext()`方法当中，从而赋值给`mBase`对象，之后`ContextWrapper`中的所有方法其实都是通过这种`委托`的机制交由`ContextImpl`去具体实现的，所以说`ContextImpl`是上下文功能的`实现类`是非常准确的。  
  
那么另外再看一下`getBaseContext()`方法，这个方法就是返回了mBase对象而已，而mBase对象其实就是`ContextImpl`对象。  
  
## Context 的应用场景  
由于Context的具体实例是由`ContextImpl`类去实现的，因此在绝大多数场景下，在绝大多数场景下，Activity、Service和Application这三种类型的Context都是可以通用的。不过有几种场景比较特殊，比如启动Activity，还有弹出Dialog。  
  
出于安全原因的考虑，Android是不允许Activity或Dialog凭空出现的，一个Activity的启动必须要建立在另一个Activity的基础之上，也就是以此形成的`返回栈`。  
  
而Dialog则必须在一个Activity上面弹出，除非是`System Alert`类型的Dialog，因此在这种场景下，我们只能使用Activity类型的Context，否则将会出错。  
  
![](https://img-blog.csdn.net/20150104183450879)  
  
大家注意看到有一些NO上添加了一些数字，其实这些从能力上来说是YES，但是不建议使用：  
- 数字1：启动Activity在这些类中是可以的，但是需要创建一个新的`task`  
- 数字2：在这些类中去layout inflate是合法的，但是会使用系统默认的主题样式，如果你自定义了某些样式可能不会被使用  
- 数字3：在receiver为null时允许，在4.2或以上的版本中，用于获取黏性广播的当前值（可以无视）  
  
> 注：ContentProvider、BroadcastReceiver之所以在上述表格中，是因为在其内部方法中都有一个context用于使用。  
  
这里重点看下Activity和Application，可以看到，和UI相关的方法基本都不建议或者不可使用Application，并且，前三个操作基本不可能在Application中出现。  
  
## 如何获取 Context  
通常我们想要获取Context对象，主要有以下四种方法  
- `View.getContext`，返回当前View对象的Context对象，通常是当前正在展示的Activity对象。  
- `Activity.getApplicationContext`，获取当前Activity所在的(应用)进程的Context对象，通常我们使用Context对象时，要优先考虑这个全局的进程Context。  
- `Activity.this`，返回当前的Activity实例，如果是UI控件需要使用Activity作为Context对象，但是默认的Toast实际上使用ApplicationContext也可以。  
- `ContextWrapper.getBaseContext()`，用来获取一个ContextWrapper进行装饰之前的Context，可以使用这个方法，这个方法在实际开发中使用并不多，也不建议使用。  
  
  
## getApplication 和 getApplicationContext  
比如如下程序：  
```java  
public class MainActivity extends Activity {   
    @Override   
    protected void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.activity_main);   
  
        MyApplication myApp = (MyApplication) getApplication();   
        Log.d("TAG", "getApplication is " + myApp);   
        Context appContext = getApplicationContext();   
        Log.d("TAG", "getApplicationContext is " + appContext);   
    }  
}   
```  
  
结果如下图所示：  
![](index_files/88f887b6-391d-43c9-8b64-c018b64ba321.jpg)  
  
看来它们是同一个对象。其实这个结果也很好理解，因为Application本身就是一个Context，所以这里获取`getApplicationContext()`得到的结果就是`MyApplication`本身的实例。  
  
**那么，为什么要提供两个功能重复的方法呢？**  
其实仅仅是因为`Activity和Service`提供了这个方法：  
```java  
//Activity和Service的源码  
private Application mApplication;   
  
/** Return the application that owns this activity. */  
public final Application getApplication() {  
    return mApplication;  
}  
```  
  
而`getApplicationContext()`是`ContextWrapper`中提供的方法，任何一个`Context`的实例，比如`BroadcastReceiver`，只要调用`getApplicationContext()`方法都可以拿到我们的Application对象：  
```java  
Context mBase;  
  
@Override  
public Context getApplicationContext() {  
    return mBase.getApplicationContext();  
}  
```  
  
## Context 引起的内存泄露  
Context并不能随便乱用，用的不好有可能会引起内存泄露的问题，下面就示例两种错误的引用方式。  
  
**错误的单例模式**  
```java  
public class Singleton {  
    private static Singleton instance;  
    private Context mContext;  
      
    private Singleton(Context context) {  
        this.mContext = context;  
    }  
      
    public static Singleton getInstance(Context context) {  
        if (instance == null) {  
            instance = new Singleton(context);  
        }  
        return instance;  
    }  
}  
```  
这是一个非线程安全的单例模式，instance作为静态对象，其生命周期要长于Activity，假如Activity A去 getInstance 获得 instance 对象，传入this，常驻内存的 Singleton 保存了你传入的 Activity A 对象，并一直持有，即使 Activity A 被销毁掉，但因为它的引用还存在于一个 Singleton 中，就不可能被 GC 掉，这样就导致了内存泄漏。  
  
**静态对象持有Activity引用**  
```java  
public class MainActivity extends Activity {  
    private static Drawable mDrawable;  
      
    @Override  
    protected void onCreate(Bundle saveInstanceState) {  
        super.onCreate(saveInstanceState);  
        setContentView(R.layout.activity_main);  
        ImageView iv = new ImageView(this);  
        mDrawable = getResources().getDrawable(R.drawable.ic_launcher);  
        iv.setImageDrawable(mDrawable);  
    }  
}  
```  
  
有一个静态的Drawable对象，当ImageView设置这个Drawable时，ImageView会将自身设置到Drawable的callback上去，因为View是实现了`Drawable.Callback`接口，这样当Drawable需要刷新的时候，可以调用这个Callback，然后通知View重新绘制该Drawable。所以引用的正确顺序应该`Drawable->View->Context`，因为被static修饰的mDrawable是常驻内存的，MainActivity是它的间接引用，MainActivity被销毁时，也不能被GC掉，所以造成内存泄漏。  
  
  
**正确使用Context**  
一般Context造成的内存泄漏，几乎都是当Context销毁的时候，却因为被引用导致销毁失败，而Application的Context对象可以理解为随着进程存在的，所以我们总结出使用Context的正确姿势：  
- 当Application的Context能搞定的情况下，并且生命周期长的对象，优先使用Application的Context。  
- 不要让生命周期长于Activity的对象持有到Activity的引用。  
- 尽量不要在Activity中使用非静态内部类，因为非静态内部类会隐式持有外部类实例的引用；如果使用静态内部类，将外部实例引用作为弱引用持有。  
  
2018-9-8  
