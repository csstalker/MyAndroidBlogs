| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Lifecycle Activity和Fragment生命周期感知组件 LifecycleObserver MD    
Demo地址：https://github.com/baiqiantao/LifecycleTest.git    
[官方文档](https://developer.android.google.cn/topic/libraries/architecture/lifecycle)    
官方公众号介绍文章：[正式发布 Android 架构组件 1.0 稳定版 | 附带中文介绍视频](https://mp.weixin.qq.com/s/9rC_5GhdAA_EMEbWKJT5vQ?)    
***  
目录  
===  

- [ 为什么要引进 Lifecycle](# 为什么要引进 lifecycle)
- [ 使用案例](# 使用案例)
	- [ LifecycleObserver 的几种实现方式](# lifecycleobserver 的几种实现方式)
		- [ 实现 LifecycleObserver 接口](# 实现 lifecycleobserver 接口)
		- [ 实现 GenericLifecycleObserver 接口](# 实现 genericlifecycleobserver 接口)
		- [ 实现 DefaultLifecycleObserver 接口](# 实现 defaultlifecycleobserver 接口)
	- [ 在 Activity 中使用](# 在 activity 中使用)
	- [ 在 Fragment 中使用](# 在 fragment 中使用)
- [ 相关的几个接口和类](# 相关的几个接口和类)
	- [ LifecycleObserver](# lifecycleobserver)
	- [ LifecycleOwner](# lifecycleowner)
	- [ Lifecycle](# lifecycle)
	- [ LifecycleRegistry](# lifecycleregistry)
  
# 为什么要引进 Lifecycle  
我们在处理Activity或者Fragment组件的生命周期相关时，不可避免会遇到这样的问题：  
> 我们在Activity的onCreate()中初始化某些成员，比如MVP架构中的Presenter，或者AudioManager、MediaPlayer等，然后在onStop中对这些成员进行对应处理，并在onDestroy中释放这些资源。  
  
这是很常见的情形，按照通常的做法，我们需要在Activity或者Fragment中的每个生命周期方法中都要调用一次IPresenter等成员中定义的相应的方法。我们可能觉得这没什么，但是其实这些完全就是模板代码，其本身往往没有任何意义，这不仅浪费我们的时间，更让我们的代码臃肿了很多，于是Lifecycle组件就诞生了。  
  
以下内容是对官方文档的翻译：  
>  生命周期感知组件`[Lifecycle-aware components]`执行操作`[perform actions]`以响应另一个组件(例如 Activity 或 Fragment)的生命周期状态的更改。这些组件可帮助您生成更易于组织`[better-organized]`且通常更轻量级`[lighter-weight]`的代码，并且这些代码更易于维护。  
  
> 一种常见的模式是，在 Activity 或 Fragment 的生命周期方法中实现依赖组件的操作`[the actions of the dependent components]`。 但是，这种模式导致代码组织不良`[poor organization]`以及错误的增加。通过使用生命周期感知组件，您可以将依赖组件的代码移出生命周期方法、并移入组件本身。  
  
> `android.arch.lifecycle` 包提供了类和接口，使您可以构建生命周期感知组件，这些组件可以根据 Activity 或 Fragment 的当前生命周期状态自动调整其行为。  
  
> Android框架中定义的大多数应用程序组件都附加了生命周期。生命周期由操作系统或流程中运行的框架代码所管理。它们是Android工作原理的核心，您的应用程序必须尊重它们。 不这样做可能会触发内存泄漏甚至应用程序崩溃。  
  
> android.arch.lifecycle 包提供了类和接口，可帮助您以弹性和隔离的方式`[resilient and isolated way]`解决这些问题。  
  
# 使用案例  
## LifecycleObserver 的几种实现方式  
如果使用`Java 8`，建议使用`DefaultLifecycleObserver`观察事件。要使用含它需添加 [common-java8](https://mvnrepository.com/artifact/android.arch.lifecycle/common-java8) 依赖：  
```  
implementation 'android.arch.lifecycle:common-java8:1.1.1'  
```  
  
如果使用`Java 7`，则可以使用注解观察生命周期事件。  
  
> 注意：一旦`Java 8`语言成为Android的主流，针对`Java 7`提供的注解方式将会被弃用，因此在`DefaultLifecycleObserver`和注解之间，您应该始终优先选择`DefaultLifecycleObserver`。  
  
实际使用中，我们通常是将所有要观察的生命周期事件都封装到 BasePresenter 等实现类中，这样每一个 BasePresenter 的子类都能感知到 Activity 对应的生命周期事件，并在子类重写的方法中实现相应的行为。  
  
### 实现 LifecycleObserver 接口  
- 使用Observer注解的方法可以接收零个或一个参数。如果使用，则第一个参数必须是`LifecycleOwner`类型。  
- 使用`Lifecycle.Event.ON_ANY`注解的方法可以接收第二个参数，该参数必须是`Lifecycle.Event`类型。  
- 提供这些附加参数是为了方便您观察多个providers和events而无需手动tracking它们。  
  
```java  
public class BaseLifecycleObserver implements LifecycleObserver {  
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)  
    public void onCreate() {  
        Log.i("bqt", "Observer【onCreate】");  
    }  
      
    @OnLifecycleEvent(Lifecycle.Event.ON_START)  
    public void onStart() {  
        Log.i("bqt", "Observer【onStart】");  
    }  
      
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)  
    public void onResume(LifecycleOwner source) {  
        Log.i("bqt", "Observer【onResume】" + source.getLifecycle().getClass().getSimpleName());  
    }  
      
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)  
    public void onPause() {  
        Log.i("bqt", "Observer【onPause】");  
    }  
      
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)  
    public void onStop() {  
        Log.i("bqt", "Observer【onStop】");  
    }  
      
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)  
    public void onDestroy() {  
        Log.i("bqt", "Observer【onDestroy】");  
    }  
      
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)  
    public void onAny(LifecycleOwner source, Lifecycle.Event event) {  
        Log.i("bqt", "Observer onAny：" + event.name());  
    }  
}  
```  
  
### 实现 GenericLifecycleObserver 接口  
```java  
public class BaseGenericLifecycleObserver implements GenericLifecycleObserver {  
      
    @Override  
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {  
        switch (event) {  
            case ON_CREATE:  
                Log.i("bqt", "Observer【onCreate】");  
                break;  
            case ON_START:  
                Log.i("bqt", "Observer【onStart】");  
                break;  
            case ON_RESUME:  
                Log.i("bqt", "Observer【onResume】" + source.getLifecycle().getClass().getSimpleName());  
                break;  
            case ON_PAUSE:  
                Log.i("bqt", "Observer【onPause】");  
                break;  
            case ON_STOP:  
                Log.i("bqt", "Observer【onStop】");  
                break;  
            case ON_DESTROY:  
                Log.i("bqt", "Observer【onDestroy】");  
                break;  
            case ON_ANY:  
                Log.i("bqt", "Observer onAny");  
                break;  
            default:  
                Log.i("bqt", "Observer【不存在的事件】");  
                break;  
        }  
    }  
}  
```  
  
### 实现 DefaultLifecycleObserver 接口  
`FullLifecycleObserver` 接口继承自 `LifecycleObserver`，并声明了与 Activity 声明周期一致的6个方法：  
```java  
interface FullLifecycleObserver extends LifecycleObserver {  
    void onCreate(LifecycleOwner owner);  
    void onStart(LifecycleOwner owner);  
    void onResume(LifecycleOwner owner);  
    void onPause(LifecycleOwner owner);  
    void onStop(LifecycleOwner owner);  
    void onDestroy(LifecycleOwner owner);  
}  
```  
  
`DefaultLifecycleObserver` 接口继承自 `FullLifecycleObserver` 接口，并利用 Java 8 的特性为每一个方法提供了空实现：  
```java  
public interface DefaultLifecycleObserver extends FullLifecycleObserver {  
   @Override  
   default void onCreate(@NonNull LifecycleOwner owner) {}  
   //...其他几个方法类似  
}  
```  
  
这样我们就不必重写接口中的每一个方法了，只需重写我们关心的方法即可。  
```java  
public class MyDefaultLifecycleObserver implements DefaultLifecycleObserver {  
    @Override  
    public void onCreate(@NonNull LifecycleOwner source) {  
        Log.i("bqt", "Observer【onCreate】");  
    }  
      
    @Override  
    public void onStart(@NonNull LifecycleOwner source) {  
        Log.i("bqt", "Observer【onStart】");  
    }  
      
    @Override  
    public void onResume(@NonNull LifecycleOwner source) {  
        Log.i("bqt", "Observer【onResume】" + source.getLifecycle().getClass().getSimpleName());  
    }  
      
    @Override  
    public void onPause(@NonNull LifecycleOwner source) {  
        Log.i("bqt", "Observer【onPause】");  
    }  
      
    @Override  
    public void onStop(@NonNull LifecycleOwner source) {  
        Log.i("bqt", "Observer【onStop】");  
    }  
      
    @Override  
    public void onDestroy(@NonNull LifecycleOwner source) {  
        Log.i("bqt", "Observer【onDestroy】");  
    }  
}  
```  
  
## 在 Activity 中使用  
```java  
public class LifecycleTestActivity extends AppCompatActivity {  
    @Override  
    protected void onCreate(@Nullable Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        getLifecycle().addObserver(new BaseLifecycleObserver()); //在onCreate方法中注册，以上三种使用任何一种效果都一样  
        ImageView imageView = new ImageView(this);  
        imageView.setImageResource(R.drawable.icon);  
        setContentView(imageView);  
    }  
}  
```  
  
打印日志  
```  
Activity【onCreate】  
    Observer【onCreate】  
Activity【onStart】  
    Observer【onStart】  
Activity【onResume】  
    Observer【onResume】  
------------------------------  
    Observer【onPause】  
Activity【onPause】  
    Observer【onStop】  
Activity【onStop】  
    Observer【onDestroy】  
Activity【onDestroy】  
```  
  
注意  
>    
   - Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME events in this class are dispatched **after** the LifecycleOwner's related method returns.   
   - Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY events in this class are dispatched **before** the LifecycleOwner's related method is called.     
   - This gives you certain guarantees某些保证 on which state the owner is in.    
  
这种结果的实现方式是通过：  
>    
   - Activity 或 Fragment 中的 performCreate()、performStart()、performResume() 方法会先调用自身的 onXXX() 方法，然后再调用 LifecycleRegistry 的 handleLifecycleEvent() 方法；  
   - 而Activity 或 Fragment 中的 performPause()、performStop()、performDestroy() 方法则会先调用 LifecycleRegistry 的 handleLifecycleEvent() 方法 ，然后再调用自身的 onXXX() 方法。  
  
  
## 在 Fragment 中使用  
```java  
public class LifecycleTestFragment extends Fragment {  
    public LifecycleTestFragment() {  
        getLifecycle().addObserver(new BaseGenericLifecycleObserver()); //不是在onCreate等生命周期方法中注册，而是在构造方法中就注册  
    }  
      
    @Nullable  
    @Override  
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {  
        ImageView imageView = new ImageView(getContext());  
        imageView.setImageResource(R.drawable.icon);  
        return imageView;  
    }  
}  
```  
  
打印日志  
```  
Fragment【onAttach】  
Fragment【onCreate】  
    Observer【onCreate】  
Fragment【onViewCreated】  
Fragment【onActivityCreated】  
Fragment【onStart】  
    Observer【onStart】  
Fragment【onResume】  
    Observer【onResume】  
------------------------------  
    Observer【onPause】  
Fragment【onPause】  
    Observer【onStop】  
Fragment【onStop】  
Fragment【onDestroyView】  
    Observer【onDestroy】  
Fragment【onDestroy】  
Fragment【onDetach】  
```  
  
# 相关的几个接口和类  
## LifecycleObserver  
`Lifecycle 观察者`    
实现该接口的类，可以通过被 LifecycleOwner 类中的 Lifecycle 对象的 addObserver(LifecycleObserver o) 方法注册，被注册后，LifecycleObserver 便可以观察 LifecycleOwner 的生命周期事件。  
```java  
public interface LifecycleObserver {} //此接口没有声明任何方法  
```  
  
## LifecycleOwner  
`Lifecycle 持有者`    
如 Activity 或 Fragment。实现该接口的类持有 Lifecycle 对象。    
持有 Lifecycle 有什么作用呢？    
作用是在将 LifecycleOwner 对应的生命周期事件传递给内部的 Lifecycle 对象去处理，于是其生命周期的改变便可被 Lifecycle 所注册的观察者 LifecycleObserver 观察到并触发其对应的事件。  
```java  
public interface LifecycleOwner {  
    Lifecycle getLifecycle();  
}  
```  
  
## Lifecycle  
`生命周期`     
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181002185717137.png)     
  
- Lifecycle 是一个抽象类，它持有关于组件(如 Activity 或 Fragment)生命周期状态的信息，并且允许其他对象观察此状态。  
- LifecycleOwner 持有 Lifecycle 对象，LifecycleOwner 通过 getLifecycle() 获取内部 Lifecycle 对象，然后就可以注册或取消注册观察者 LifecycleObserver。    
- Lifecycle 使用两个主要枚举来跟踪其关联组件的生命周期状态  
   - `State`：当前生命周期所处状态。由框架和 Lifecycle 类所调度的生命周期事件。这些事件映射到 Activity 或 Fragment 中的回调事件。  
   - `Event`：当前生命周期改变对应的事件。由 Lifecycle 对象所跟踪`[tracked]`的组件的当前状态。  
     
将State视为图形的节点`[nodes]`，将Event视为这些节点之间的边缘`[edges]`。    
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181002200730417.png)    
  
## LifecycleRegistry  
`生命周期`  
  
- 继承自 Lifecycle，是 Activity 或 Fragment 中所持有的 Lifecycle 对象的实际类型。  
- LifecycleRegistry 内部有一个成员变量 State 用于记录当前的生命周期，当其生命周期改变后，LifecycleRegistry 就会逐个通知每一个注册的 LifecycleObserver。    
  
> An implementation of Lifecycle that can handle multiple observers. It is used by Fragments and Support Library Activities. You can also directly use it if you have a custom LifecycleOwner.  
  
- ObserverWithState：LifecycleRegistry 的内部类  
```java  
static class ObserverWithState {  
    State mState;  
    GenericLifecycleObserver mLifecycleObserver;  
}  
```  
  
关系表：  
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181002182130148.png)    
  
2018-10-2  
